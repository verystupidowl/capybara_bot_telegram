package ru.tggc.capybaratelegrambot.service.impl;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.request.DeleteMessage;
import com.pengrad.telegrambot.request.EditMessageCaption;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tggc.capybaratelegrambot.domain.dto.PhotoDto;
import ru.tggc.capybaratelegrambot.domain.dto.RequestType;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.timedaction.Happiness;
import ru.tggc.capybaratelegrambot.domain.model.Photo;
import ru.tggc.capybaratelegrambot.domain.model.Race;
import ru.tggc.capybaratelegrambot.domain.model.RaceRequest;
import ru.tggc.capybaratelegrambot.domain.model.enums.ImprovementValue;
import ru.tggc.capybaratelegrambot.domain.model.enums.RaceStatus;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraException;
import ru.tggc.capybaratelegrambot.handler.callback.CallbackHandler;
import ru.tggc.capybaratelegrambot.repository.RaceRequestRepository;
import ru.tggc.capybaratelegrambot.service.factory.AbstractRequestService;
import ru.tggc.capybaratelegrambot.utils.RandomUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;

@Slf4j
@Service
public class RaceService extends AbstractRequestService<RaceRequest> {
    private static final Random random = new Random();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);
    private final RaceRequestRepository raceRequestRepository;

    public RaceService(CapybaraService capybaraService, UserService userService,
                       RaceRequestRepository raceRequestRepository) {
        super(capybaraService, userService);
        this.raceRequestRepository = raceRequestRepository;
    }

    public BiConsumer<CallbackHandler, CallbackQuery> respondRace(Capybara opponent, boolean accept) {
        return Optional.ofNullable(opponent.getRaceRequest())
                .map(raceRequest -> {
                    BiConsumer<CallbackHandler, CallbackQuery> callback;
                    if (accept) {
                        raceRequest.setStatus(RaceStatus.ACCEPTED);
                        callback = acceptRace(
                                raceRequest.getChallenger(),
                                opponent
                        );
                    } else {
                        raceRequest.setStatus(RaceStatus.DECLINED);
                        callback = (handler, query) ->
                                handler.sendSimpleMessage(query, "ok", null);
                    }

                    raceRequest.getChallenger().setRaceRequest(null);
                    opponent.setRaceRequest(null);

                    raceRequestRepository.save(raceRequest);
                    return callback;
                })
                .orElseThrow(() -> new CapybaraException("No incoming challenge to respond to!"));
    }

    private BiConsumer<CallbackHandler, CallbackQuery> acceptRace(Capybara challenger, Capybara opponent) {
        checkStamina(challenger);
        checkStamina(opponent);
        log.info("accepting race between {} and {}", challenger.getName(), opponent.getName());
        return race(challenger, opponent);
    }

    private BiConsumer<CallbackHandler, CallbackQuery> race(Capybara capybara, Capybara capybaraToRace) {
        return (handler, query) -> {
            String chatId = query.maybeInaccessibleMessage().chat().id().toString();
            String text = "🏃Идёт забег капибар!!!\nСоревнуются " +
                    capybara.getName() + " и " + capybaraToRace.getName();
            Photo randomPhoto = RandomUtils.getRandomPhoto();
            PhotoDto photo = PhotoDto.builder()
                    .caption(text)
                    .url(randomPhoto.getUrl())
                    .chatId(chatId)
                    .build();
            Message sentMessage = handler.sendSimplePhoto(photo).message();
            int messageId = sentMessage.messageId();

            DeleteMessage deleteOldMessage = new DeleteMessage(
                    chatId,
                    Integer.parseInt(query.inlineMessageId())
            );

            handler.create(deleteOldMessage).send();

            int need = 100 + (((capybara.getLevel().getValue() + capybaraToRace.getLevel().getValue()) / 2) / 10) * 10;

            RaceContext context = new RaceContext(capybara, capybaraToRace, need, handler, query, messageId);
            scheduler.schedule(() -> raceStep(context), 1500, TimeUnit.MILLISECONDS);
        };
    }

    private void raceStep(RaceContext ctx) {
        Capybara c1 = ctx.capybara1();
        Capybara c2 = ctx.capybara2();

        ctx.percent1(random.nextInt(c1.getLevel().getValue() + 50 + c1.getImprovement().getImprovement().getChance()) + ctx.percent1());
        ctx.percent2(random.nextInt(c2.getLevel().getValue() + 50 + c2.getImprovement().getImprovement().getChance()) + ctx.percent2());

        EditMessageCaption editMessageCaption = new EditMessageCaption(
                ctx.query.maybeInaccessibleMessage().chat().id(),
                ctx.messageId
        );

        editMessageCaption.caption("🏃Идёт забег капибар!!!"
                + "\n\n" + (ctx.percent1() > ctx.percent2() ? "🥇" : "")
                + c1.getName() + " пробежала " + ctx.percent1() + "/" + ctx.need()
                + "\n\n" + (ctx.percent2() > ctx.percent1() ? "🥇" : "")
                + c2.getName() + " пробежала " + ctx.percent2() + "/" + ctx.need());

        ctx.handler().create(editMessageCaption);

        if (ctx.percent1() > ctx.need() || ctx.percent2() > ctx.need()) {
            scheduler.schedule(() -> finishRace(ctx), 2, TimeUnit.SECONDS);
        } else {
            scheduler.schedule(() -> raceStep(ctx), 1500, TimeUnit.MILLISECONDS);
        }
    }

    private void finishRace(RaceContext ctx) {
        Capybara c1 = ctx.capybara1();
        Capybara c2 = ctx.capybara2();

        if (ctx.percent1() > ctx.percent2()) {
            getResults(c1, c2);
            sendMessages(c1, c2, ctx);
        } else if (ctx.percent2() > ctx.percent1()) {
            getResults(c2, c1);
            sendMessages(c2, c1, ctx);
        } else {
            EditMessageCaption editMessageCaption = new EditMessageCaption(
                    ctx.query.maybeInaccessibleMessage().chat().id(),
                    ctx.messageId
            );
            editMessageCaption.caption(
                    "😲 ОГО! У нас тут ничья!\nКапибары не получают и не теряют счастья!"
            );
            ctx.handler().create(editMessageCaption);
        }

    }

    private void updateHappiness(Capybara capybara, boolean isWinner) {
        ImprovementValue improvement = capybara.getImprovement().getImprovement();
        Happiness happiness = capybara.getHappiness();

        if (isWinner) {
            happiness.setLevel(happiness.getLevel() + improvement.getWinHappiness());
        } else {
            happiness.setLevel(Math.max(0, happiness.getLevel() - improvement.getLoseHappiness()));
        }

        capybara.setHappiness(happiness);
    }

    private void sendMessages(Capybara winner, Capybara loser, RaceContext ctx) {
        EditMessageCaption editMessageCaption = new EditMessageCaption(
                ctx.query.maybeInaccessibleMessage().chat().id(),
                ctx.messageId
        );
        editMessageCaption.caption("🏆Выиграла капибара " + winner.getName() +
                "\nЕё счастье увеличилось на " + winner.getImprovement().getImprovement().getWinHappiness() + "!\nСчастье проигравшей уменьшилось на " +
                (loser.getImprovement().getImprovement().getLoseHappiness()));
        ctx.handler().create(editMessageCaption);
    }

    private void getResults(Capybara winner, Capybara loser) {
        updateStatuses(winner, true);
        updateStatuses(loser, false);
        addRace(winner, loser);
    }

    private void updateStatuses(Capybara capybara, boolean isWinner) {
        updateStamina(capybara);
        afterRaceUpdate(capybara);
        updateHappiness(capybara, isWinner);
    }

    private void updateStamina(Capybara capybara) {
        if (capybara.getLastRaceAt() == null) {
            return;
        }

        long minutesSinceLastRace = Duration.between(capybara.getLastRaceAt(), LocalDateTime.now()).toMinutes();

        if (minutesSinceLastRace >= 15) {
            capybara.setConsecutiveRaces(0);
        }
    }

    private void afterRaceUpdate(Capybara capybara) {
        capybara.setConsecutiveRaces(capybara.getConsecutiveRaces() + 1);
        capybara.setLastRaceAt(LocalDateTime.now());
    }

    private void addRace(Capybara winner, Capybara loser) {
        Race race = Race.builder()
                .raceDate(LocalDateTime.now())
                .winner(winner)
                .loser(loser)
                .build();

        winner.getRaces().add(race);
        loser.getRaces().add(race);
    }

    private void checkStamina(Capybara capybara) {
        updateStamina(capybara);

        if (capybara.getConsecutiveRaces() >= 5) {
            throw new CapybaraException("Too tired for a race! Wait a bit.");
        }
    }

    @Override
    protected void saveRequest(Capybara challenger, Capybara opponent, RaceRequest request) {
        challenger.setRaceRequest(request);
        opponent.setRaceRequest(request);

        raceRequestRepository.save(request);
    }

    @Override
    protected RaceRequest getRequest(Capybara challenger, Capybara opponent) {
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

    @Builder
    private record RaceContext(
            Capybara capybara1,
            Capybara capybara2,
            int need,
            CallbackHandler handler,
            CallbackQuery query,
            int[] p1,
            int[] p2,
            int messageId
    ) {
        RaceContext(Capybara c1, Capybara c2, int need, CallbackHandler handler, CallbackQuery query, int messageId) {
            this(c1, c2, need, handler, query, new int[]{0}, new int[]{0}, messageId);
        }

        int percent1() {
            return p1[0];
        }

        int percent2() {
            return p2[0];
        }

        void percent1(int v) {
            p1[0] = v;
        }

        void percent2(int v) {
            p2[0] = v;
        }
    }
}
