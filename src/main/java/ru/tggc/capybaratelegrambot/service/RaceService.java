package ru.tggc.capybaratelegrambot.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageCaption;
import com.pengrad.telegrambot.request.SendAnimation;
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
import ru.tggc.capybaratelegrambot.domain.dto.FileDto;
import ru.tggc.capybaratelegrambot.domain.dto.enums.FileType;
import ru.tggc.capybaratelegrambot.domain.dto.enums.RequestType;
import ru.tggc.capybaratelegrambot.domain.dto.response.Response;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.RaceRequest;
import ru.tggc.capybaratelegrambot.domain.model.enums.ImprovementValue;
import ru.tggc.capybaratelegrambot.domain.model.enums.RaceStatus;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.Happiness;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.RaceAction;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraException;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraTiredException;
import ru.tggc.capybaratelegrambot.keyboard.InlineKeyboardCreator;
import ru.tggc.capybaratelegrambot.repository.RaceRequestRepository;
import ru.tggc.capybaratelegrambot.service.factory.AbstractRequestService;
import ru.tggc.capybaratelegrambot.utils.HistoryType;
import ru.tggc.capybaratelegrambot.utils.RandomUtils;
import ru.tggc.capybaratelegrambot.utils.UserRateLimiterService;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    private final HistoryService historyService;
    private final InlineKeyboardCreator inlineKeyboardCreator;
    private final UserRateLimiterService rateLimiterService;

    @Setter(onMethod_ = {@Autowired, @Lazy})
    private RaceService self;

    public RaceService(CapybaraService capybaraService,
                       UserService userService,
                       RaceRequestRepository raceRequestRepository,
                       TimedActionService timedActionService,
                       HistoryService historyService,
                       InlineKeyboardCreator inlineKeyboardCreator,
                       UserRateLimiterService rateLimiterService) {
        super(capybaraService, userService);
        this.raceRequestRepository = raceRequestRepository;
        this.capybaraService = capybaraService;
        this.timedActionService = timedActionService;
        this.historyService = historyService;
        this.inlineKeyboardCreator = inlineKeyboardCreator;
        this.rateLimiterService = rateLimiterService;
    }

    public Response acceptRace(CapybaraContext ctx) {
        Capybara capybara = capybaraService.getRaceCapybara(ctx);
        return respondRace(capybara, ctx, true);
    }

    public Response refuseRace(CapybaraContext ctx) {
        Capybara capybara = capybaraService.getCapybaraByContext(ctx);
        Response response = respondRace(capybara, ctx, false);
        capybaraService.save(capybara);
        return response;
    }

    public Response respondRace(Capybara opponent, CapybaraContext ctx, boolean accept) {
        return raceRequestRepository.findByOpponentId(opponent.getId())
                .map(raceRequest -> {
                    Response response = Response.of(new DeleteMessage(ctx.chatId(), ctx.messageId()));
                    Capybara challenger = raceRequest.getChallenger();

                    Long challengerOwnerId = challenger.getUser().getId();
                    rateLimiterService.lock(challengerOwnerId);

                    if (accept) {
                        raceRequest.setStatus(RaceStatus.ACCEPTED);
                        response = response.andThen(acceptRace(
                                challenger,
                                opponent
                        ));
                    } else {
                        raceRequest.setStatus(RaceStatus.DECLINED);
                        response = response.andThen(Response.of(new SendMessage(ctx.chatId(), "ok")));
                    }

                    challenger.setRaceRequest(null);
                    opponent.setRaceRequest(null);

                    raceRequestRepository.delete(raceRequest);

                    return response.andThen(bot -> {
                        rateLimiterService.unlock(challengerOwnerId);
                        return CompletableFuture.completedFuture(null);
                    });
                })
                .orElseThrow(() -> new CapybaraException("No incoming challenge to respond to!"));
    }

    public Response acceptRace(Capybara challenger, Capybara opponent) {
        self.checkStamina(challenger);
        self.checkStamina(opponent);
        log.info("accepting race between {} and {}", challenger.getName(), opponent.getName());
        return race(challenger, opponent);
    }

    public Response race(Capybara c1, Capybara c2) {
        return bot -> {
            long chatId = c1.getChat().getId();
            FileDto fileDto = RandomUtils.getRandomRacePhoto();
            int messageId;
            String caption = "\uD83C\uDFC3Идёт забег капибар!!!\nСоревнуются " + c1.getName() + " и " + c2.getName();
            if (fileDto.getType() == FileType.PHOTO) {
                messageId = bot.execute(new SendPhoto(chatId, fileDto.getUrl())
                                .caption(caption))
                        .message().messageId();
            } else {
                messageId = bot.execute(new SendAnimation(chatId, fileDto.getUrl()).caption(caption))
                        .message().messageId();
            }

            int need = 100 + (((c1.getLevel().getValue() + c2.getLevel().getValue()) / 2) / 10) * 10;
            RaceStepContext ctx = new RaceStepContext(c1.getId(), c2.getId(), need, bot, chatId, messageId);

            CompletableFuture<Void> future = new CompletableFuture<>();
            scheduler.schedule(() -> raceStepAsync(ctx, future), 1500, TimeUnit.MILLISECONDS);
            return future;
        };
    }

    public void raceStepAsync(RaceStepContext ctx, CompletableFuture<Void> future) {
        Capybara c1 = capybaraService.getCapybaraById(ctx.c1Id);
        Capybara c2 = capybaraService.getCapybaraById(ctx.c2Id);

        ctx.percent1 += random.nextInt(c1.getLevel().getValue() + 50 + c1.getImprovement().getImprovementValue().getChance());
        ctx.percent2 += random.nextInt(c2.getLevel().getValue() + 50 + c2.getImprovement().getImprovementValue().getChance());

        ctx.bot.execute(new EditMessageCaption(ctx.chatId,
                ctx.messageId)
                .caption("🏃Идёт забег капибар!!!\n\n" +
                        (ctx.percent1 > ctx.percent2 ? "🥇" : "") + c1.getName() + " пробежала " + ctx.percent1 + "/" + ctx.need + "\n" +
                        (ctx.percent2 > ctx.percent1 ? "🥇" : "") + c2.getName() + " пробежала " + ctx.percent2 + "/" + ctx.need));

        capybaraService.save(c1);
        capybaraService.save(c2);

        if (ctx.percent1 > ctx.need || ctx.percent2 > ctx.need) {
            scheduler.schedule(() -> self.finishRaceAsync(ctx), 2, TimeUnit.SECONDS);
            future.complete(null);
        } else {
            scheduler.schedule(() -> raceStepAsync(ctx, future), 1500, TimeUnit.MILLISECONDS);
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
            ctx.bot.execute(new EditMessageCaption(ctx.chatId, ctx.messageId)
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
        ctx.bot.execute(new EditMessageCaption(ctx.chatId,
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
        winner.getRace().setWins(winner.getRace().getWins() + 1);
        loser.getRace().setDefeats(loser.getRace().getDefeats() + 1);
    }

    @Transactional
    public void updateStatuses(Capybara c, boolean isWinner) {
        self.afterRaceUpdate(c);
        self.updateHappiness(c, isWinner);
    }

    @Transactional
    public void afterRaceUpdate(Capybara c) {
        c.getRace().getRaceAction().recordRace();
    }

    @Transactional
    public void checkStamina(Capybara c) {
        RaceAction raceAction = c.getRace().getRaceAction();
        throwIf(!raceAction.canPerform(), () -> {
            String status = getStatus(raceAction);
            InlineKeyboardMarkup markup = inlineKeyboardCreator.raceMassage();
            return new CapybaraTiredException(status, markup);
        });
    }

    private String getStatus(RaceAction raceAction) {
        return timedActionService.getStatus(raceAction);
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
        throwIf(requestsAlreadyExists, () -> {
            String messageToSend = "u or ur opponent already has a challenge";
            InlineKeyboardMarkup markup = inlineKeyboardCreator.raceKeyboard();
            return new CapybaraException(messageToSend, markup);
        });
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
        self.checkStamina(capybara);
        historyService.setHistory(ctx, HistoryType.START_RACE);
    }

    public static class RaceStepContext {
        final Long c1Id;
        final Long c2Id;
        final int need;
        final TelegramBot bot;
        final long chatId;
        final int messageId;
        int percent1 = 0;
        int percent2 = 0;

        public RaceStepContext(Long c1, Long c2, int need, TelegramBot bot, long chatId, int messageId) {
            this.c1Id = c1;
            this.c2Id = c2;
            this.need = need;
            this.bot = bot;
            this.chatId = chatId;
            this.messageId = messageId;
        }
    }
}
