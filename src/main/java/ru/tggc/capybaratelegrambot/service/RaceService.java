package ru.tggc.capybaratelegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageCaption;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.dto.RequestType;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.Photo;
import ru.tggc.capybaratelegrambot.domain.model.RaceRequest;
import ru.tggc.capybaratelegrambot.domain.model.enums.ImprovementValue;
import ru.tggc.capybaratelegrambot.domain.model.enums.RaceStatus;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.Happiness;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.RaceAction;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraException;
import ru.tggc.capybaratelegrambot.repository.RaceRequestRepository;
import ru.tggc.capybaratelegrambot.service.factory.AbstractRequestService;
import ru.tggc.capybaratelegrambot.utils.HistoryType;
import ru.tggc.capybaratelegrambot.utils.RandomUtils;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

import static java.lang.Math.max;
import static ru.tggc.capybaratelegrambot.utils.Utils.throwIf;

@Slf4j
@Service
public class RaceService extends AbstractRequestService<RaceRequest> {
    private static final Random random = new Random();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

    private final RaceRequestRepository raceRequestRepository;
    private final CapybaraService capybaraService;
    private final TimedActionService timedActionService;

    @Setter(onMethod_ = {@Autowired, @Lazy})
    private RaceService self;
    private final HistoryService historyService;

    public RaceService(CapybaraService capybaraService,
                       UserService userService,
                       RaceRequestRepository raceRequestRepository,
                       TimedActionService timedActionService,
                       HistoryService historyService) {
        super(capybaraService, userService);
        this.raceRequestRepository = raceRequestRepository;
        this.capybaraService = capybaraService;
        this.timedActionService = timedActionService;
        this.historyService = historyService;
    }

    public BiConsumer<TelegramBot, CallbackQuery> acceptRace(CapybaraContext ctx) {
        Capybara capybara = capybaraService.getRaceCapybara(ctx);
        return respondRace(capybara, true);
    }

    public void refuseRace(CapybaraContext ctx) {
        Capybara capybara = capybaraService.getCapybaraByContext(ctx);
        respondRace(capybara, false);
        capybaraService.save(capybara);
    }

    public BiConsumer<TelegramBot, CallbackQuery> respondRace(Capybara opponent, boolean accept) {
        return raceRequestRepository.findByOpponentId(opponent.getId())
                .map(raceRequest -> {
                    BiConsumer<TelegramBot, CallbackQuery> callback;
                    Capybara challenger = raceRequest.getChallenger();
                    if (accept) {
                        raceRequest.setStatus(RaceStatus.ACCEPTED);
                        callback = acceptRace(
                                challenger,
                                opponent
                        );
                    } else {
                        raceRequest.setStatus(RaceStatus.DECLINED);
                        callback = (bot, query) -> {
                            long chatId = query.maybeInaccessibleMessage().chat().id();
                            bot.execute(new SendMessage(chatId, "ok"));
                        };
                    }

                    challenger.setRaceRequest(null);
                    opponent.setRaceRequest(null);

                    raceRequestRepository.delete(raceRequest);
                    return callback;
                })
                .orElseThrow(() -> new CapybaraException("No incoming challenge to respond to!"));
    }

    public BiConsumer<TelegramBot, CallbackQuery> acceptRace(Capybara challenger, Capybara opponent) {
        self.checkStamina(challenger);
        self.checkStamina(opponent);
        log.info("accepting race between {} and {}", challenger.getName(), opponent.getName());
        return race(challenger, opponent);
    }

    public BiConsumer<TelegramBot, CallbackQuery> race(Capybara c1, Capybara c2) {
        return (bot, query) -> {
            String chatId = query.maybeInaccessibleMessage().chat().id().toString();
            Photo photo = RandomUtils.getRandomPhoto();
            int messageId = bot.execute(new SendPhoto(chatId, photo.getUrl())
                            .caption("🏃Идёт забег капибар!!!\nСоревнуются " + c1.getName() + " и " + c2.getName()))
                    .message().messageId();

            bot.execute(new DeleteMessage(chatId, query.maybeInaccessibleMessage().messageId()));

            int need = 100 + (((c1.getLevel().getValue() + c2.getLevel().getValue()) / 2) / 10) * 10;
            RaceStepContext ctx = new RaceStepContext(c1.getId(), c2.getId(), need, bot, query, messageId);

            scheduler.schedule(() -> raceStepAsync(ctx), 1500, TimeUnit.MILLISECONDS);
        };
    }

    public void raceStepAsync(RaceStepContext ctx) {
        Capybara c1 = capybaraService.getCapybaraById(ctx.c1Id);
        Capybara c2 = capybaraService.getCapybaraById(ctx.c2Id);

        ctx.percent1 += random.nextInt(c1.getLevel().getValue() + 50 + c1.getImprovement().getImprovementValue().getChance());
        ctx.percent2 += random.nextInt(c2.getLevel().getValue() + 50 + c2.getImprovement().getImprovementValue().getChance());

        ctx.bot.execute(new EditMessageCaption(ctx.query.maybeInaccessibleMessage().chat().id(),
                ctx.messageId)
                .caption("🏃Идёт забег капибар!!!\n\n" +
                        (ctx.percent1 > ctx.percent2 ? "🥇" : "") + c1.getName() + " пробежала " + ctx.percent1 + "/" + ctx.need + "\n" +
                        (ctx.percent2 > ctx.percent1 ? "🥇" : "") + c2.getName() + " пробежала " + ctx.percent2 + "/" + ctx.need));

        capybaraService.save(c1);
        capybaraService.save(c2);

        if (ctx.percent1 > ctx.need || ctx.percent2 > ctx.need) {
            scheduler.schedule(() -> self.finishRaceAsync(ctx), 2, TimeUnit.SECONDS);
        } else {
            scheduler.schedule(() -> raceStepAsync(ctx), 1500, TimeUnit.MILLISECONDS);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Async
    public void finishRaceAsync(RaceStepContext ctx) {
        Capybara c1 = capybaraService.getCapybaraById(ctx.c1Id);
        Capybara c2 = capybaraService.getCapybaraById(ctx.c2Id);

        if (ctx.percent1 > ctx.percent2) {
            self.getResults(c1, c2);
            sendMessages(c1, c2, ctx);
        } else if (ctx.percent2 > ctx.percent1) {
            self.getResults(c2, c1);
            sendMessages(c2, c1, ctx);
        } else {
            ctx.bot.execute(new EditMessageCaption(ctx.query.maybeInaccessibleMessage().chat().id(), ctx.messageId)
                    .caption("😲 Ничья! Капибары не получают и не теряют счастья!"));
        }

        capybaraService.save(c1);
        capybaraService.save(c2);
    }

    @Transactional
    public void updateHappiness(Capybara capybara, boolean isWinner) {
        ImprovementValue improvement = capybara.getImprovement().getImprovementValue();
        Happiness happiness = capybara.getHappiness();
        happiness.setLevel(isWinner ?
                happiness.getLevel() + improvement.getWinHappiness() :
                max(0, happiness.getLevel() - improvement.getLoseHappiness()));
        capybara.setHappiness(happiness);
    }

    public void sendMessages(Capybara winner, Capybara loser, RaceStepContext ctx) {
        ctx.bot.execute(new EditMessageCaption(ctx.query.maybeInaccessibleMessage().chat().id(),
                ctx.messageId)
                .caption("🏆Выиграла капибара " + winner.getName() +
                        "\nСчастье + " + winner.getImprovement().getImprovementValue().getWinHappiness() +
                        ", проигравшая - " + loser.getImprovement().getImprovementValue().getLoseHappiness()));
    }

    @Transactional
    public void getResults(Capybara winner, Capybara loser) {
        self.updateStatuses(winner, true);
        self.updateStatuses(loser, false);
        self.updateWinsAndDefeats(winner, loser);
    }

    @Transactional
    public void updateWinsAndDefeats(Capybara winner, Capybara loser) {
        winner.setWins(winner.getWins() + 1);
        loser.setDefeats(loser.getDefeats() + 1);
    }

    @Transactional
    public void updateStatuses(Capybara c, boolean isWinner) {
        self.afterRaceUpdate(c);
        self.updateHappiness(c, isWinner);
    }

    @Transactional
    public void afterRaceUpdate(Capybara c) {
        c.getRaceAction().recordRace();
    }

    @Transactional
    public void checkStamina(Capybara c) {
        RaceAction raceAction = c.getRaceAction();
        throwIf(!raceAction.canPerform(), () -> new CapybaraException("Too tired, wait " + timedActionService.getStatus(raceAction)));
    }

    @Override
    protected void saveRequest(Capybara challenger, Capybara opponent, RaceRequest request) {
        challenger.setRaceRequest(request);
        opponent.setRaceRequest(request);
        raceRequestRepository.save(request);
    }

    @Override
    protected RaceRequest getRequest(Capybara challenger, Capybara opponent) {
        boolean requestsAlreadyExists = raceRequestRepository.existsByChallengerOrOpponent(challenger, opponent);
        throwIf(requestsAlreadyExists, () -> new CapybaraException("u or ur opponent already has a challenge"));
        return RaceRequest.builder()
                .challenger(challenger)
                .opponent(opponent)
                .createdAt(LocalDateTime.now())
                .status(RaceStatus.PENDING)
                .build();
    }

    @Override
    public RequestType getRequestType() {
        return RequestType.RACE;
    }

    public void startRace(CapybaraContext ctx) {
        Capybara capybara = capybaraService.getRaceCapybara(ctx);
        throwIf(!capybara.getRaceAction().canPerform(), () -> new CapybaraException("U cant care yet"));
        historyService.setHistory(ctx, HistoryType.START_RACE);
    }

    public static class RaceStepContext {
        final Long c1Id;
        final Long c2Id;
        final int need;
        final TelegramBot bot;
        final CallbackQuery query;
        final int messageId;
        int percent1 = 0;
        int percent2 = 0;

        public RaceStepContext(Long c1, Long c2, int need, TelegramBot bot, CallbackQuery query, int messageId) {
            this.c1Id = c1;
            this.c2Id = c2;
            this.need = need;
            this.bot = bot;
            this.query = query;
            this.messageId = messageId;
        }
    }
}
