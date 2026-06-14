package ru.tggc.botapp.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.request.InlineKeyboardMarkup;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageCaption;
import com.pengrad.telegrambot.request.SendAnimation;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.tggc.botapp.domain.dto.RequestType;
import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.domain.model.RaceRequest;
import ru.tggc.botapp.domain.model.enums.ImprovementValue;
import ru.tggc.botapp.domain.model.enums.RaceStatus;
import ru.tggc.botapp.domain.model.timedaction.Happiness;
import ru.tggc.botapp.domain.model.timedaction.RaceAction;
import ru.tggc.botapp.exceptions.CapybaraException;
import ru.tggc.botapp.exceptions.CapybaraTiredException;
import ru.tggc.botapp.keyboard.KeyboardFactory;
import ru.tggc.botapp.keyboard.KeyboardKey;
import ru.tggc.botapp.repository.RaceRequestRepository;
import ru.tggc.botapp.service.factory.AbstractRequestService;
import ru.tggc.botapp.service.impl.HistoryServiceImpl;
import ru.tggc.botapp.service.impl.UserServiceImpl;
import ru.tggc.botapp.util.HistoryType;
import ru.tggc.botapp.util.RandomUtils;
import ru.tggc.telegrambotframework.dto.FileDto;
import ru.tggc.telegrambotframework.dto.FileType;
import ru.tggc.telegrambotframework.dto.Response;
import ru.tggc.telegrambotframework.dto.UpdateContext;
import ru.tggc.telegrambotframework.service.TelegramBotSender;
import ru.tggc.telegrambotframework.service.UserRateLimiterService;

import java.time.LocalDateTime;
import java.util.Random;

import static java.lang.Math.max;
import static ru.tggc.telegrambotframework.util.Utils.throwIf;

@Slf4j
@Service
public class RaceService extends AbstractRequestService<RaceRequest> {
    private static final Random random = new Random();

    private final RaceRequestRepository raceRequestRepository;
    private final CapybaraService capybaraService;
    private final TimedActionService timedActionService;
    private final HistoryServiceImpl historyService;
    private final KeyboardFactory keyboardFactory;
    private final UserRateLimiterService rateLimiterService;
    private final TelegramBotSender telegramBotService;

    @Setter(onMethod_ = {@Autowired, @Lazy})
    private RaceService self;

    public RaceService(CapybaraService capybaraService,
                       UserServiceImpl userService,
                       RaceRequestRepository raceRequestRepository,
                       TimedActionService timedActionService,
                       HistoryServiceImpl historyService,
                       KeyboardFactory keyboardFactory,
                       UserRateLimiterService rateLimiterService,
                       TelegramBotSender telegramBotService) {
        super(capybaraService, userService);
        this.raceRequestRepository = raceRequestRepository;
        this.capybaraService = capybaraService;
        this.timedActionService = timedActionService;
        this.historyService = historyService;
        this.keyboardFactory = keyboardFactory;
        this.rateLimiterService = rateLimiterService;
        this.telegramBotService = telegramBotService;
    }

    public Response acceptRace(UpdateContext ctx) {
        Capybara capybara = capybaraService.getRaceCapybara(ctx);
        return respondRace(capybara, ctx, true);
    }

    public Response refuseRace(UpdateContext ctx) {
        Capybara capybara = capybaraService.getCapybaraByContext(ctx);
        Response response = respondRace(capybara, ctx, false);
        capybaraService.save(capybara);
        return response;
    }

    public Response respondRace(Capybara opponent, UpdateContext ctx, boolean accept) {
        return raceRequestRepository.findByOpponentId(opponent.getId())
                .map(raceRequest -> {
                    Response response = Response.of(new DeleteMessage(ctx.chatId(), ctx.messageId()));
                    Capybara challenger = raceRequest.getChallenger();

                    Long challengerOwnerId = challenger.getUser().getId();
                    rateLimiterService.lock(challengerOwnerId);
                    rateLimiterService.lock(opponent.getUser().getId());

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

                    return response;
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
                messageId = bot.execute(new SendPhoto(chatId, fileDto.getUrl()).caption(caption))
                        .message()
                        .messageId();
            } else {
                SendResponse execute = bot.execute(new SendAnimation(chatId, fileDto.getUrl()).caption(caption));
                messageId = execute
                        .message()
                        .messageId();
            }

            int need = 100 + (((c1.getLevel().getValue() + c2.getLevel().getValue()) / 2) / 10) * 10;

            CapybaraContextDto contextDto1 = new CapybaraContextDto(c1);
            CapybaraContextDto contextDto2 = new CapybaraContextDto(c2);

            RaceStepContext ctx = new RaceStepContext(contextDto1, contextDto2, need, bot, chatId, messageId);

            scheduleNextStep(ctx);
        };
    }

    private void scheduleNextStep(RaceStepContext ctx) {
        telegramBotService.sendDelayed(bot -> {
            CapybaraContextDto c1 = ctx.c1;
            CapybaraContextDto c2 = ctx.c2;

            ctx.percent1 += random.nextInt(c1.level + 50 + c1.chance);
            ctx.percent2 += random.nextInt(c2.level + 50 + c2.chance);

            boolean isFinished = ctx.percent1 >= ctx.need || ctx.percent2 > ctx.need;

            bot.execute(new EditMessageCaption(ctx.chatId,
                    ctx.messageId)
                    .caption("🏃Идёт забег капибар!!!\n\n" +
                            (ctx.percent1 > ctx.percent2 ? "🥇" : "") + c1.name + " пробежала " + ctx.percent1 + "/" + ctx.need + "\n" +
                            (ctx.percent2 > ctx.percent1 ? "🥇" : "") + c2.name + " пробежала " + ctx.percent2 + "/" + ctx.need));

            if (isFinished) {
                try {
                    self.finishRace(ctx);
                } finally {
                    rateLimiterService.unlock(ctx.c1.userId);
                    rateLimiterService.unlock(ctx.c2.userId);
                }
            } else {
                scheduleNextStep(ctx);
            }
        }, 1500L);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void finishRace(RaceStepContext ctx) {
        Capybara c1 = capybaraService.getCapybara(ctx.c1.id);
        Capybara c2 = capybaraService.getCapybara(ctx.c2.id);

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
        telegramBotService.send(Response.of(new EditMessageCaption(ctx.chatId, ctx.messageId)
                .caption("🏆Выиграла капибара " + winner.getName() +
                        "\nСчастье + " + winner.getImprovement().getImprovementValue().getWinHappiness() +
                        ", проигравшая - " + loser.getImprovement().getImprovementValue().getLoseHappiness())));
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
            InlineKeyboardMarkup markup = keyboardFactory.getKeyboardInline(KeyboardKey.RACE_MASSAGE);
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
            InlineKeyboardMarkup markup = keyboardFactory.getKeyboardInline(KeyboardKey.RACE);
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

    public void startRace(UpdateContext ctx) {
        Capybara capybara = capybaraService.getRaceCapybara(ctx);
        self.checkStamina(capybara);
        historyService.setHistory(ctx, HistoryType.START_RACE);
    }

    public static class RaceStepContext {
        final CapybaraContextDto c1;
        final CapybaraContextDto c2;
        final int need;
        final TelegramBot bot;
        final long chatId;
        final int messageId;
        int percent1 = 0;
        int percent2 = 0;

        public RaceStepContext(CapybaraContextDto c1, CapybaraContextDto c2, int need, TelegramBot bot, long chatId, int messageId) {
            this.c1 = c1;
            this.c2 = c2;
            this.need = need;
            this.bot = bot;
            this.chatId = chatId;
            this.messageId = messageId;
        }
    }

    public record CapybaraContextDto(Long id, String name, int level, int chance, long userId) {
        public CapybaraContextDto(Capybara capybara) {
            this(
                    capybara.getId(),
                    capybara.getName(),
                    capybara.getLevel().getValue(),
                    capybara.getImprovement().getImprovementValue().getChance(),
                    capybara.getUser().getId()
            );
        }
    }
}
