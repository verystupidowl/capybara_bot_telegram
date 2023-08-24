package ru.tggc.capibaraBotTelegram.serveCommands;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.request.InputMediaPhoto;
import com.pengrad.telegrambot.request.EditMessageCaption;
import com.pengrad.telegrambot.request.EditMessageMedia;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.request.SendPhoto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.tggc.capibaraBotTelegram.Bot;
import ru.tggc.capibaraBotTelegram.DataBase.CapybaraDAO;
import ru.tggc.capibaraBotTelegram.DataBase.Request;
import ru.tggc.capibaraBotTelegram.Utils.Text;
import ru.tggc.capibaraBotTelegram.capybara.Capybara;
import ru.tggc.capibaraBotTelegram.capybara.CapybaraPhoto;
import ru.tggc.capibaraBotTelegram.capybara.Happiness;
import ru.tggc.capibaraBotTelegram.capybara.job.Jobs;
import ru.tggc.capibaraBotTelegram.capybara.job.MainJob;
import ru.tggc.capibaraBotTelegram.capybara.properties.*;
import ru.tggc.capibaraBotTelegram.exceptions.CapybaraException;
import ru.tggc.capibaraBotTelegram.exceptions.CapybaraNullException;
import ru.tggc.capibaraBotTelegram.keyboard.InlineKeyboardCreator;

import java.util.Date;
import java.util.List;
import java.util.Random;

import static ru.tggc.capibaraBotTelegram.Utils.Utils.levelUp;
import static ru.tggc.capibaraBotTelegram.Utils.Utils.timeToString;

@Component
public class CallbackServer {

    private final JdbcTemplate jdbcTemplate;
    private final MainJob mainJob;

    @Autowired
    public CallbackServer(JdbcTemplate jdbcTemplate, MainJob mainJob) {
        this.jdbcTemplate = jdbcTemplate;
        this.mainJob = mainJob;
    }

    public void serveCallbackCommands(CallbackQuery query, Bot bot) {

        CapybaraDAO capybaraDAO = new CapybaraDAO(jdbcTemplate);
        Text text = new Text();
        InlineKeyboardCreator inlineCreator = new InlineKeyboardCreator();
        System.out.println(query.data());
        Long userId = query.from().id();
        Long chatId = query.message().chat().id();
        int date = (int) (new Date().getTime() / 1000);

        switch (query.data()) {
            case "info" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(userId.toString(), chatId.toString());
                try {
                    capybara.checkNotNull();
                    bot.execute(new EditMessageCaption(chatId, query.message().messageId())
                            .caption(text.getInfo(capybara, date - capybara.getTimeZone()))
                            .replyMarkup(inlineCreator.infoKeyboard(capybara, (date - capybara.getTimeZone()))));
                } catch (CapybaraNullException e) {
                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA));
                }
            }
            case "go_to_main" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(userId.toString(), chatId.toString());
                try {
                    capybara.checkNotNull();
                    bot.execute(new EditMessageMedia(chatId, query.message().messageId(), new InputMediaPhoto(capybara.getCapybaraPhoto().toUrl())));
                    bot.execute(new EditMessageCaption(chatId, query.message().messageId())
                            .caption(text.getMyCapybara(capybara, query.message(), capybaraDAO))
                            .replyMarkup(inlineCreator.myCapybaraKeyboard(capybara, (date - capybara.getTimeZone()))));
                } catch (CapybaraNullException e) {
                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA));
                }
            }
            case "set_name" -> {
                if (!capybaraDAO.checkChangeName(query.from().id(), query.message().chat().id())) {
                    capybaraDAO.changeNameFirst(query.from().id(), query.message().chat().id());
                }
                bot.execute(new SendMessage(chatId, """
                        ✨Как ты хочешь, чтобы звали твою капибарку?
                        \uD83D\uDCACТекст твоего следующего сообщения, отправленного в этот чат, незамедлительно станет новым именем твоей прелестной капибары.

                        ⛔Имя должно быть уникальным в пределах беседы!""").replyMarkup(inlineCreator.notChange()));
            }
            case "set_photo" -> {
                if (!capybaraDAO.checkChangePhoto(userId, chatId)) {
                    capybaraDAO.changePhotoFirst(userId, chatId);
                }
                bot.execute(new SendMessage(chatId, """
                        \uD83D\uDDBCПришли новую картинку для своей капибары. Стоимость - 50 долек
                        Следующая твоя картинка, отправленная в этот чат, незамедлительно станет новой фотографией твоей прелестной капибары.
                        ⛔Чтобы отменить это нажми "Не менять ничего"
                        Или можешь выбрать случайную за 25 \uD83C\uDF49долек!

                        \uD83E\uDD2FЕсли у твоей капибары исчезла фотография, ты можешь переслать последнее сообщение с этой фотографией в ЭТОМ чате, и написать "Восстановить"

                        Либо прислать фотографию из вложений этой беседы с фотографией.\s
                        Не забудь также написать "Восстановить" Это бесплатно!""").replyMarkup(inlineCreator.defaultPhoto()));
            }
            case "not_change" -> {
                if (capybaraDAO.checkChangePhoto(userId, chatId)
                        || capybaraDAO.checkChangeName(userId, chatId)) {
                    capybaraDAO.notChange(userId, chatId);
                    bot.execute(new SendMessage(chatId, "Ок"));
                }
            }
            case "set_default_photo" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(userId.toString(), chatId.toString());
                try {
                    capybara.checkNotNull();
                    if (capybaraDAO.checkChangeName(userId, chatId))
                        capybaraDAO.checkOriginalName(capybara.getName(), userId, chatId);
                    if (capybara.getCurrency() >= 25) {
                        capybara.setCapybaraPhoto(CapybaraPhoto.getDefaultPhoto());
                        capybara.setCurrency(capybara.getCurrency() - 25);
                        capybaraDAO.updateDB(new Request(capybara, query.data()));
                        bot.execute(new SendMessage(chatId, "Выбрано случайное фото. Со счета капибры списано 25 арбузных долек"));
                    } else {
                        bot.execute(new SendMessage(chatId, text.NO_MONEY));
                    }
                } catch (CapybaraNullException e) {
                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA));
                }
            }
            case "feed_fatten" -> bot.execute(new EditMessageCaption(chatId, query.message().messageId())
                    .caption("""
                            Выбери, что сделать:
                            Покормить капибару: Добавляется 5 сытости. \uD83C\uDF49Арбузные дольки не тратятся

                            Откормить капибару: Добавляется 50 сытости!!! \uD83C\uDF49Стоимость - 500 арбузных долек.""")
                    .replyMarkup(inlineCreator.feedKeyboard()));
            case "make_happy" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(userId.toString(), chatId.toString());
                Random random = new Random();
                try {
                    capybara.checkNotNull();
                    if (capybaraDAO.checkChangeName(userId, chatId))
                        capybaraDAO.checkOriginalName(capybara.getName(), userId, chatId);
                    Happiness happiness = new Happiness();
                    List<String> happinessThings = happiness.getHappinessThings();
                    int randomNumber = random.nextInt(5);
                    if (date - capybara.getTimeZone() >= capybara.getHappiness().getTimeRemaining()) {
                        bot.execute(new SendMessage(chatId, happinessThings.get(randomNumber)));
                        Request request = new Request(capybara, query.data());
                        switch (randomNumber) {
                            case 0 ->
                                    capybara.setHappiness(new CapybaraHappiness((date - capybara.getTimeZone()) + 10800,
                                            capybara.getHappiness().getLevel() + 5));
                            case 1 -> {
                                capybara.setHappiness(new CapybaraHappiness((date - capybara.getTimeZone()) + 10800,
                                        capybara.getHappiness().getLevel() + 20));
                                capybara.setSatiety(new CapybaraSatiety(capybara.getSatiety().getTimeRemaining(), capybara.getSatiety().getLevel() + 5));
                            }
                            case 2 ->
                                    capybara.setHappiness(new CapybaraHappiness((date - capybara.getTimeZone()) + 10800,
                                            capybara.getHappiness().getLevel() + 15));
                            case 3 ->
                                    capybara.setHappiness(new CapybaraHappiness((date - capybara.getTimeZone()) + 10800,
                                            capybara.getHappiness().getLevel() + 10));
                            case 4 -> {
                                if (capybara.getHappiness().getLevel() > 10) {
                                    capybara.setHappiness(new CapybaraHappiness((date - capybara.getTimeZone()) + 10800,
                                            capybara.getHappiness().getLevel() - 10));
                                } else {
                                    capybara.setHappiness(new CapybaraHappiness((date - capybara.getTimeZone()) + 10800,
                                            0));
                                }
                            }
                        }
                        bot.execute(new SendMessage(chatId, levelUp(capybara)));
                        capybaraDAO.updateDB(request);
                    } else {
                        bot.execute(new SendMessage(chatId, "Ты сможешь сделать хорошие дела для вашей капибары только через " +
                                timeToString(capybara.getHappiness().getTimeRemaining() - (date - capybara.getTimeZone()))));
                    }
                } catch (CapybaraNullException e) {
                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA));
                }
            }
            case "feed" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(userId.toString(), chatId.toString());
                try {
                    capybara.checkNotNull();
                    if (capybaraDAO.checkChangeName(userId, chatId))
                        capybaraDAO.checkOriginalName(capybara.getName(), userId, chatId);
                    if ((date - capybara.getTimeZone()) >= capybara.getSatiety().getTimeRemaining()) {
                        bot.execute(new SendMessage(chatId, "Твоя капибара успешно покушала, возвращайся через 2 часа!"));
                        bot.execute(new EditMessageCaption(chatId, query.message().messageId())
                                .caption(text.getMyCapybara(capybara, query.message(), capybaraDAO))
                                .replyMarkup(inlineCreator.myCapybaraKeyboard(capybara, (date - capybara.getTimeZone()))));
                        capybara.setSatiety(new CapybaraSatiety((date - capybara.getTimeZone()) + 7200,
                                capybara.getSatiety().getLevel() + 5));
                        bot.execute(new SendMessage(chatId, levelUp(capybara)));
                        Request request = new Request(capybara, query.data());
                        capybaraDAO.updateDB(request);
                    } else {
                        bot.execute(new SendMessage(chatId, "Ты сможешь покормить капибару только через " +
                                timeToString(capybara.getSatiety().getTimeRemaining() - (date - capybara.getTimeZone()))));
                    }
                } catch (CapybaraNullException e) {
                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA));
                }
            }
            case "fatten" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(userId.toString(), chatId.toString());
                try {
                    capybara.checkNotNull();
                    if (capybaraDAO.checkChangeName(userId, chatId))
                        capybaraDAO.checkOriginalName(capybara.getName(), userId, chatId);
                    if ((date - capybara.getTimeZone()) >= capybara.getSatiety().getTimeRemaining()) {
                        if (capybara.getCurrency() >= 500) {
                            bot.execute(new SendPhoto(chatId, new CapybaraPhoto("photo", -209917797, 457245510).toUrl())
                                    .caption("""
                                            Твоя капибара съела целый арбуз!
                                            Её сытость увеличилась на 50!
                                            Возвращайся через 2 часа!"""));
                            capybara.setSatiety(new CapybaraSatiety((date - capybara.getTimeZone()) + 7200,
                                    capybara.getSatiety().getLevel() + 50));
                            levelUp(capybara);
                            capybara.setCurrency(capybara.getCurrency() - 500);
                            Request request = new Request(capybara, query.data());
                            capybaraDAO.updateDB(request);
                        } else {
                            bot.execute(new SendMessage(chatId, "У тебя нет столько Арбузных долек! Может стоит сходить на работу?"));
                        }
                    } else {
                        bot.execute(new SendMessage(chatId, "Ты сможешь откормить капибару только через " +
                                timeToString(capybara.getSatiety().getTimeRemaining() - (date - capybara.getTimeZone()))));
                    }
                } catch (CapybaraNullException e) {
                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA));
                }
            }
            case "go_tea" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(userId.toString(), chatId.toString());
                try {
                    capybara.checkNotNull();
                    if (capybaraDAO.checkChangeName(userId, chatId))
                        capybaraDAO.checkOriginalName(capybara.getName(), userId, chatId);
                    if (capybara.getTea().getLevel() == 0) {
                        if ((date - capybara.getTimeZone()) >= capybara.getTea().getTimeRemaining()) {
                            bot.execute(new SendMessage(chatId, "Вы ждете собеседника для чаепития!")
                                    .replyMarkup(inlineCreator.teaKeyboard()));
                            capybara.setTeaTime(new CapybaraTea(capybara.getTea().getTimeRemaining(), 1));
                            Request request3 = new Request(capybara, query.data());
                            capybaraDAO.updateDB(request3);
                            Capybara capybara1 = capybaraDAO.getTeaCapybara(userId.toString(), chatId.toString());
                            if (!("" + capybara1.getName()).equals("null")) {
                                bot.execute(new SendPhoto(chatId, capybara1.getCapybaraPhoto().toUrl())
                                        .caption("[id" + capybara.getUsername().getUserID() + "|" + capybara.getName() +
                                                "]" + ", твой собеседник сегодня - " + capybara1.getName() + "\nСчастье увеличено на 10"));

                                bot.execute(new SendPhoto(capybara1.getUsername().getPeerID(), capybara.getCapybaraPhoto().toUrl())
                                        .caption("[id" + capybara1.getUsername().getUserID() + "|" + capybara1.getName() +
                                                "]" + ", твой собеседник сегодня - " + capybara.getName() + "\nСчастье увеличено на 10"));
                                capybara.setTeaTime(new CapybaraTea((date - capybara.getTimeZone()) + 10800, 0));
                                capybara.setHappiness(new CapybaraHappiness(capybara.getHappiness().getTimeRemaining(), capybara.getHappiness().getLevel() + 10));
                                capybara1.setTeaTime(new CapybaraTea((date - capybara.getTimeZone()) + 10800, 0));
                                capybara1.setHappiness(new CapybaraHappiness(capybara1.getHappiness().getTimeRemaining(), capybara1.getHappiness().getLevel() + 10));
                                bot.execute(new SendMessage(chatId, levelUp(capybara)));
                                bot.execute(new SendMessage(capybara1.getUsername().getPeerID(), levelUp(capybara1)));
                                Request request = new Request(capybara, query.data());
                                Request request1 = new Request(capybara1, query.data());
                                capybaraDAO.updateDB(request1);
                                capybaraDAO.updateDB(request);
                            }

                        } else {
                            bot.execute(new SendMessage(chatId, "Твоя капибара сможет пойти на чаепитие только через " +
                                    timeToString(capybara.getTea().getTimeRemaining() - (date - capybara.getTimeZone()))));
                        }
                    } else {
                        bot.execute(new SendMessage(chatId, "Твоя капибара и так ждёт собеседника!")
                                .replyMarkup(inlineCreator.teaKeyboard()));
                    }
                } catch (CapybaraNullException e) {
                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA));
                }
            }
            case "take_from_tea" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(userId.toString(), chatId.toString());
                try {
                    capybara.checkNotNull();
                    if (capybaraDAO.checkChangeName(userId, chatId))
                        capybaraDAO.checkOriginalName(capybara.getName(), userId, chatId);
                    if (capybara.getTea().getLevel() == 1) {


                        capybara.setTeaTime(new CapybaraTea(capybara.getTea().getTimeRemaining(), 0));
                        Request request = new Request(capybara, query.data());

                        capybaraDAO.updateDB(request);

                        bot.execute(new SendMessage(chatId, "Капибара больше не ждет собеседника"));
                    } else {
                        bot.execute(new SendMessage(chatId, "Твоя капибара не на чаепитии!"));
                    }
                } catch (CapybaraNullException e) {
                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA));
                }
            }
            case "get_job" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(userId.toString(), chatId.toString());
                try {
                    capybara.checkNotNull();
                    if (capybaraDAO.checkChangeName(userId, chatId))
                        capybaraDAO.checkOriginalName(capybara.getName(), userId, chatId);
                    if (!capybara.hasWork()) {
                        bot.execute(new EditMessageCaption(chatId, query.message().messageId()).caption("Выбери работу")
                                .replyMarkup(inlineCreator.newJob()));
                    } else {
                        bot.execute(new SendMessage(chatId, text.ALREADY_ON_WORK));
                    }
                } catch (CapybaraNullException e) {
                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA));
                }
            }
            case "prog_job" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(userId.toString(), chatId.toString());
                try {
                    capybara.checkNotNull();
                    if (capybaraDAO.checkChangeName(userId, chatId))
                        capybaraDAO.checkOriginalName(capybara.getName(), userId, chatId);
                    if (!capybara.hasWork()) {
                        capybara.setJob(new MainJob().mainJob(Jobs.PROGRAMMING, 0));
                        capybara.getJob().setRise(1);
                        capybaraDAO.newJob(capybara);
                        CapybaraPhoto capybaraPhoto = CapybaraPhoto.workingPhotos().get(0);
                        bot.execute(new SendPhoto(chatId, capybaraPhoto.toUrl())
                                .caption("Твоя капибара устроилась работать программистом! Поздравляю!"));
                    } else {
                        bot.execute(new SendMessage(chatId, text.ALREADY_ON_WORK));
                    }
                } catch (CapybaraNullException e) {
                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA));
                }
            }
            case "crim_job" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(userId.toString(), chatId.toString());
                try {
                    capybara.checkNotNull();
                    if (capybaraDAO.checkChangeName(userId, chatId))
                        capybaraDAO.checkOriginalName(capybara.getName(), userId, chatId);
                    if (!capybara.hasWork()) {
                        capybara.setJob(new MainJob().mainJob(Jobs.CRIMINAL, 0));
                        capybara.getJob().setRise(1);
                        capybaraDAO.newJob(capybara);
                        CapybaraPhoto capybaraPhoto = CapybaraPhoto.workingPhotos().get(2);
                        bot.execute(new SendPhoto(chatId, capybaraPhoto.toUrl())
                                .caption("Твоя капибара устроилась работать грабителем! Поздравляю!"));
                    } else {
                        bot.execute(new SendMessage(chatId, text.ALREADY_ON_WORK));
                    }
                } catch (CapybaraNullException e) {
                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA));
                }
            }
            case "cash_job" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(userId.toString(), chatId.toString());
                try {
                    capybara.checkNotNull();
                    if (capybaraDAO.checkChangeName(userId, chatId))
                        capybaraDAO.checkOriginalName(capybara.getName(), userId, chatId);
                    if (!capybara.hasWork()) {
                        capybara.setJob(new MainJob().mainJob(Jobs.CASHIER, 0));
                        capybara.getJob().setRise(1);
                        capybaraDAO.newJob(capybara);
                        CapybaraPhoto capybaraPhoto = CapybaraPhoto.workingPhotos().get(1);
                        bot.execute(new SendPhoto(chatId, capybaraPhoto.toUrl())
                                .caption("Твоя капибара устроилась работать кассиром! Поздравляю!"));
                    } else {
                        bot.execute(new SendMessage(chatId, text.ALREADY_ON_WORK));
                    }
                } catch (CapybaraNullException e) {
                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA));
                }
            }
            case "go_job" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(userId.toString(), chatId.toString());
                try {
                    capybara.checkNotNull();
                    if (capybaraDAO.checkChangeName(userId, chatId))
                        capybaraDAO.checkOriginalName(capybara.getName(), userId, chatId);
                    if (capybara.hasWork()) {
                        mainJob.mainJob(capybara.getJob().getEnum(), capybara.getJob().getIndex());
                        try {
                            mainJob.work(query.message(), capybara);
                            bot.execute(new SendMessage(chatId, "Твоя капибара ушла на работу! " +
                                    "\nНе забудь забрать ее оттуда через 2 часа!"));
                            capybaraDAO.updateJob(capybara);
                        } catch (CapybaraException e) {
                            if (e.getMessage().equals("2"))
                                bot.execute(new SendMessage(chatId, "Ты сможешь отправить капибару на работу только через " + timeToString((int) (capybara.getJob().getJobTimer()
                                        .getNextJob() - (date - capybara.getTimeZone())))));
                            else if (e.getMessage().equals("1"))
                                bot.execute(new SendMessage(chatId, text.ALREADY_ON_WORK));
                            else if (e.getMessage().equals("3"))
                                bot.execute(new SendMessage(chatId, "Сначала забери капибару с большого дела!"));
                        }
                    } else {
                        bot.execute(new SendMessage(chatId, "Твоя капибара безработная!"));
                    }
                } catch (CapybaraNullException e) {
                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA));
                }
            }
            case "take_from_job" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(userId.toString(), chatId.toString());
                try {
                    capybara.checkNotNull();
                    if (capybaraDAO.checkChangeName(userId, chatId))
                        capybaraDAO.checkOriginalName(capybara.getName(), userId, chatId);
                    if (capybara.hasWork()) {
                        mainJob.mainJob(capybara.getJob().getEnum(), capybara.getJob().getIndex());
                        try {
                            mainJob.fromWork(query.message(), capybara, bot);
                            capybaraDAO.updateJob(capybara);
                            bot.execute(new SendMessage(chatId, "Ты забрал капибару с работы!"));
                        } catch (CapybaraException e) {
                            if (e.getMessage().equals("1"))
                                bot.execute(new SendMessage(chatId, "Ты сможешь забрать капибару с работы только через " +
                                        timeToString(capybara.getJob().getJobTimer()
                                                .getTimeRemaining() - (date - capybara.getTimeZone()))));
                            else if (e.getMessage().equals("2"))
                                bot.execute(new SendMessage(chatId, "Твоя капибарка и не была на работе\n " +
                                        "Откуда ты ее забирать собрался?"));
                        }
                    } else {
                        bot.execute(new SendMessage(chatId, "Твоя капибарка и не была на работе\n " +
                                "Откуда ты ее забирать собрался?"));
                    }
                } catch (CapybaraNullException e) {
                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA));
                }
            }
            case "accept_race" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(userId.toString(), chatId.toString());
                if (!("" + capybara.getName()).equals("null")) {
                    if (capybaraDAO.checkChangeName(userId, chatId))
                        capybaraDAO.checkOriginalName(capybara.getName(), userId, chatId);
                    if (!capybara.getRace().getWantsRace().equals("0")) {
                        Capybara raceCapybara = capybaraDAO.getCapybaraFromDB(capybara.getRace().getWantsRace(), chatId.toString());
                        if (raceCapybara.getRace().getWantsRace().equals(capybara.getUsername().getUserID()) && capybara.getRace().getWantsRace().equals(raceCapybara.getUsername().getUserID())) {
                            if (capybara.getRace().getStartedRace() == 0 && raceCapybara.getRace().getStartedRace() == 1) {
                                raceCapybara.setRace(new CapybaraRace(raceCapybara.getRace().getTimeRemaining(), raceCapybara.getRace().getLevel(), "0", 0));
                                capybara.setRace(new CapybaraRace(capybara.getRace().getTimeRemaining(), capybara.getRace().getLevel(), "0", 0));
                                Random random1 = new Random();
                                Random random2 = new Random();

                                CapybaraPhoto capybaraPhoto = Capybara.racePhoto();

                                Thread raceThread = new Thread(() -> {
                                    capybaraDAO.updateDB(new Request(raceCapybara, ""));
                                    capybaraDAO.updateDB(new Request(capybara, ""));
                                    System.out.println("thread");
                                    int messageId;
                                    Message message = null;
                                    while (message == null) {
                                        System.out.println("yes");
                                        try {
                                            message = bot.execute(new SendPhoto(chatId, capybaraPhoto.toUrl()).caption("\uD83C\uDFC3Идёт забег капибар!!!\nСоревнуются " +
                                                    capybara.getName() + " и " + raceCapybara.getName())).message();
                                            Thread.sleep(100);
                                        } catch (InterruptedException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                                    messageId = message.messageId();
                                    int percent1 = 0;
                                    int percent2 = 0;
                                    int need = 100 + ((((capybara.getLevel() + raceCapybara.getLevel()) / 2) / 10) * 10);
                                    do {
                                        try {
                                            Thread.sleep(1500);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        int randomWin1 = random1.nextInt(capybara.getLevel() + 50 + capybara.getImprovement().getImprove());
                                        int randomWin2 = random2.nextInt(raceCapybara.getLevel() + 50 + raceCapybara.getImprovement().getImprove());
                                        percent1 += randomWin1;
                                        percent2 += randomWin2;
                                        bot.execute(new EditMessageCaption(chatId, messageId)
                                                .caption("\uD83C\uDFC3Идёт забег капибар!!!"
                                                        + "\n\n" + (percent1 > percent2 ? "\uD83E\uDD47" : "")
                                                        + capybara.getName() + " пробежала " + percent1 + "/" + need
                                                        + "\n\n" + (percent2 > percent1 ? "\uD83E\uDD47" : "")
                                                        + raceCapybara.getName() + " пробежала " + percent2 + "/" + need));
                                    } while (percent1 <= need && percent2 <= need);

                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }

                                    int race = Math.max(capybara.getRace().getLevel(), Math.min(race(capybara, (date - capybara.getTimeZone())), (5 + (capybara.getLevel() / 10)))) - 1;
                                    int race1 = Math.max(raceCapybara.getRace().getLevel(), Math.min(race(raceCapybara, (date - capybara.getTimeZone())), (5 + (capybara.getLevel() / 10)))) - 1;

                                    capybara.setRace(new CapybaraRace((date - capybara.getTimeZone()) + 300, race, capybara.getRace().getWantsRace(), 0));
                                    raceCapybara.setRace(new CapybaraRace((date - capybara.getTimeZone()) + 300, race1, raceCapybara.getRace().getWantsRace(), 0));
                                    CapybaraImprovement capybaraImprovement = new CapybaraImprovement();
                                    capybaraImprovement.setLevel(0);
                                    Random random = new Random();
                                    CapybaraPhoto winCapybaraPhoto = CapybaraPhoto.winCapybara().get(random.nextInt(4));


                                    if (percent1 > percent2) {
                                        bot.execute(new EditMessageMedia(chatId, messageId, new InputMediaPhoto(winCapybaraPhoto.toUrl())));
                                        bot.execute(new EditMessageCaption(chatId, messageId)
                                                .caption("\uD83C\uDFC6Выиграла капибара " + capybara.getName() +
                                                        "\nЕё счастье увеличилось на 10!\nСчастье проигравшей уменьшилось на " +
                                                        (raceCapybara.getImprovement().getLevel() == 2 ? "0" :
                                                                (raceCapybara.getImprovement().getLevel() == 3 ? "30" : "10"))));

                                        capybara.setWins(capybara.getWins() + 1);
                                        capybara.setHappiness(new CapybaraHappiness(capybara.getHappiness().getTimeRemaining(), capybara.getHappiness().getLevel() + 10 *
                                                (raceCapybara.getLevel() == 3 ? 3 : 1)));
                                        raceCapybara.setDefeats(raceCapybara.getDefeats() + 1);
                                        if (raceCapybara.getImprovement().getLevel() != 2) {
                                            if (raceCapybara.getHappiness().getLevel() >= 10) {
                                                raceCapybara.setHappiness(new CapybaraHappiness(raceCapybara.getHappiness().getTimeRemaining(), raceCapybara.getHappiness().getLevel() - 10 *
                                                        (raceCapybara.getLevel() == 3 ? 3 : 1)));
                                            } else {
                                                raceCapybara.setHappiness(new CapybaraHappiness(raceCapybara.getHappiness().getTimeRemaining(), 0));
                                            }
                                        }
                                        levelUp(capybara);
                                    } else if (percent1 < percent2) {
                                        bot.execute(new EditMessageCaption(chatId, messageId)
                                                .caption("\uD83C\uDFC6Выиграла капибара " + raceCapybara.getName() +
                                                        " Её счастье увеличилось на 10!\nСчастье проигравшей уменьшилось на " +
                                                        (capybara.getImprovement().getLevel() == 2 ? "0" :
                                                                (capybara.getImprovement().getLevel() == 3 ? "30" : "10"))));

                                        raceCapybara.setWins(raceCapybara.getWins() + 1);
                                        raceCapybara.setHappiness(new CapybaraHappiness(raceCapybara.getHappiness().getTimeRemaining(), raceCapybara.getHappiness().getLevel() + 10 *
                                                (capybara.getLevel() == 3 ? 3 : 1)));
                                        capybara.setDefeats(capybara.getDefeats() + 1);
                                        if (raceCapybara.getImprovement().getLevel() != 2) {
                                            if (capybara.getHappiness().getLevel() >= 10) {
                                                capybara.setHappiness(new CapybaraHappiness(capybara.getHappiness().getTimeRemaining(), capybara.getHappiness().getLevel() - 10 *
                                                        (capybara.getLevel() == 3 ? 3 : 1)));
                                            } else {
                                                capybara.setHappiness(new CapybaraHappiness(capybara.getHappiness().getTimeRemaining(), 0));
                                            }
                                        }
                                        levelUp(raceCapybara);
                                    } else {
                                        bot.execute(new EditMessageCaption(chatId, query.message().messageId())
                                                .caption("ОГО! У нас тут ничья!\nКапибары не получают и не теряют счастья!"));

                                        raceCapybara.setWins(raceCapybara.getWins() + 1);
                                        raceCapybara.setHappiness(new CapybaraHappiness(raceCapybara.getHappiness().getTimeRemaining(), raceCapybara.getHappiness().getLevel() + 10 *
                                                (capybara.getLevel() == 3 ? 3 : 1)));
                                        capybara.setDefeats(capybara.getDefeats() + 1);
                                        if (raceCapybara.getImprovement().getLevel() != 2) {
                                            if (capybara.getHappiness().getLevel() >= 10) {
                                                capybara.setHappiness(new CapybaraHappiness(capybara.getHappiness().getTimeRemaining(), capybara.getHappiness().getLevel() - 10 *
                                                        (capybara.getLevel() == 3 ? 3 : 1)));
                                            } else {
                                                capybara.setHappiness(new CapybaraHappiness(capybara.getHappiness().getTimeRemaining(), 0));
                                            }
                                        }
                                    }


                                    Request request1 = new Request(capybara, query.data());
                                    Request request2 = new Request(raceCapybara, query.data());

                                    capybara.setImprovement(capybaraImprovement);
                                    raceCapybara.setImprovement(capybaraImprovement);

                                    capybaraDAO.updateDB(request1);
                                    capybaraDAO.updateDB(request2);
                                });
                                raceThread.start();
                            } else {
                                bot.execute(new SendMessage(chatId, "Ты хочешь принять забег за другую капибару?"));
                            }
                        } else {
                            bot.execute(new SendMessage(chatId, "Возможно твою капибару не вызывали на забег"));
                        }
                    }
                }
            }
            case "refuse_race" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(userId.toString(), chatId.toString());
                if (!("" + capybara.getName()).equals("null")) {
                    Capybara raceCapybara = capybaraDAO.getCapybaraFromDB(capybara.getRace().getWantsRace(), chatId.toString());
                    if (!("" + raceCapybara.getName()).equals("null")) {
                        if (capybaraDAO.checkChangeName(userId, chatId))
                            capybaraDAO.checkOriginalName(capybara.getName(), userId, chatId);
                        raceCapybara.setRace(new CapybaraRace(raceCapybara.getRace().getTimeRemaining(), raceCapybara.getRace().getLevel(), "0", 0));
                        capybara.setRace(new CapybaraRace(capybara.getRace().getTimeRemaining(), capybara.getRace().getLevel(), "0", 0));
                        Request request = new Request(capybara, query.data());
                        Request request1 = new Request(raceCapybara, query.data());
                        capybaraDAO.updateDB(request);
                        capybaraDAO.updateDB(request1);
                        bot.execute(new SendMessage(chatId, "Забег отменен"));
                    }
                }
            }
            case "do_massage" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(userId.toString(), chatId.toString());
                try {
                    capybara.checkNotNull();
                    if (capybaraDAO.checkChangeName(userId, chatId))
                        capybaraDAO.checkOriginalName(capybara.getName(), userId, chatId);
                    if (capybara.getRace().getLevel() < 5) {
                        if (capybara.getCurrency() >= 50) {
                            capybara.setCurrency(capybara.getCurrency() - 50);
                            capybara.setRace(new CapybaraRace(((date - capybara.getTimeZone())), 5, capybara.getRace().getWantsRace(),
                                    capybara.getRace().getStartedRace()));
                            Request request = new Request(capybara, query.data());
                            capybaraDAO.updateDB(request);
                            bot.execute(new SendMessage(chatId, "Ты сделал капибаре массаж! Бодрость полностью восстановлена!"));
                        } else {
                            bot.execute(new SendMessage(chatId, "У тебя не хватает арбузных долек!"));
                        }
                    } else {
                        bot.execute(new SendMessage(chatId, "У твоей капибары и так полная бодрость!"));
                    }
                } catch (CapybaraNullException e) {
                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA));
                }
            }
            case "buy_improve" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(userId.toString(), chatId.toString());
                try {
                    capybara.checkNotNull();
                    if (capybaraDAO.checkChangeName(userId, chatId))
                        capybaraDAO.checkOriginalName(capybara.getName(), userId, chatId);
                    if (capybara.getImprovement().getLevel() == 0) {
                        bot.execute(new EditMessageCaption(chatId, query.message().messageId())
                                .caption(text.LIST_OF_IMPROVEMENTS)
                                .replyMarkup(inlineCreator.improvements()));
                    } else {
                        bot.execute(new SendMessage(chatId, "У твоей капибары уже есть улучшения!"));
                    }
                } catch (CapybaraNullException e) {
                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA));
                }
            }
            case "improve_boots" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(userId.toString(), chatId.toString());
                try {
                    capybara.checkNotNull();
                    if (capybaraDAO.checkChangeName(userId, chatId))
                        capybaraDAO.checkOriginalName(capybara.getName(), userId, chatId);
                    if (capybara.getImprovement().getLevel() == 0) {
                        if (capybara.getCurrency() >= 50) {
                            CapybaraImprovement capybaraImprovement = new CapybaraImprovement();
                            capybaraImprovement.setLevel(1);
                            capybara.setImprovement(capybaraImprovement);
                            capybara.setCurrency(capybara.getCurrency() - 50);
                            bot.execute(new SendMessage(chatId, "Твоя капибара теперь носит новые стильные ботиночки"));
                            capybaraDAO.updateImprovement(capybara);
                        } else {
                            bot.execute(new SendMessage(chatId, "Похоже у твоей капибары не хватает арбузных долек! " +
                                    "\nМожет ей стоит сходить на работу?"));
                        }
                    } else {
                        bot.execute(new SendMessage(chatId, "У твоей капибары уже есть улучшения!"));
                    }
                } catch (CapybaraNullException e) {
                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA));
                }
            }
            case "improve_watermelon" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(userId.toString(), chatId.toString());
                try {
                    capybara.checkNotNull();
                    if (capybaraDAO.checkChangeName(userId, chatId))
                        capybaraDAO.checkOriginalName(capybara.getName(), userId, chatId);
                    if (capybara.getImprovement().getLevel() == 0) {
                        if (capybara.getCurrency() >= 100) {
                            CapybaraImprovement capybaraImprovement = new CapybaraImprovement();
                            capybaraImprovement.setLevel(2);
                            capybara.setImprovement(capybaraImprovement);
                            capybara.setCurrency(capybara.getCurrency() - 100);
                            bot.execute(new SendMessage(chatId, "Твоя капибара съела вкусный сладкий арбуз"));
                            capybaraDAO.updateImprovement(capybara);
                        } else {
                            bot.execute(new SendMessage(chatId, "Похоже у твоей капибары не хватает арбузных долек! " +
                                    "\nМожет ей стоит сходить на работу?"));
                        }
                    } else {
                        bot.execute(new SendMessage(chatId, "У твоей капибары уже есть улучшения!"));
                    }
                } catch (CapybaraNullException e) {
                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA));
                }
            }
            case "improve_pill" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(userId.toString(), chatId.toString());
                try {
                    capybara.checkNotNull();
                    if (capybaraDAO.checkChangeName(userId, chatId))
                        capybaraDAO.checkOriginalName(capybara.getName(), userId, chatId);
                    if (capybara.getImprovement().getLevel() == 0) {
                        if (capybara.getCurrency() >= 150) {
                            CapybaraImprovement capybaraImprovement = new CapybaraImprovement();
                            capybaraImprovement.setLevel(3);
                            capybara.setImprovement(capybaraImprovement);
                            capybara.setCurrency(capybara.getCurrency() - 150);
                            bot.execute(new SendMessage(chatId, "Твоя капибара приняла антипроигрыш!"));
                            capybaraDAO.updateImprovement(capybara);
                        } else {
                            bot.execute(new SendMessage(chatId, "Похоже у твоей капибары не хватает арбузных долек! " +
                                    "\nМожет ей стоит сходить на работу?"));
                        }
                    } else {
                        bot.execute(new SendMessage(chatId, "У твоей капибары уже есть улучшения!"));
                    }
                } catch (CapybaraNullException e) {
                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA));
                }
            }
            case "accept_wedding" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(userId.toString(), chatId.toString());
                if (!("" + capybara.getName()).equals("null")) {
                    Capybara capybaraWedding = capybaraDAO.getCapybaraFromDB(capybara.getWantsWedding().toString(), chatId.toString());
                    if (!("" + capybaraWedding.getName()).equals("null")) {
                        if (capybaraDAO.checkChangeName(userId, chatId))
                            capybaraDAO.checkOriginalName(capybara.getName(), userId, chatId);
                        if (capybaraWedding.getIsWedding() == 1) {
                            if (capybaraWedding.getWantsWedding().toString().equals(userId.toString())) {
                                capybara.setWedding(capybaraWedding.getUsername().getUserID());
                                capybaraWedding.setWedding(capybara.getUsername().getUserID());
                                capybara.setWantsWedding(0L);
                                capybaraWedding.setWantsWedding(0L);
                                capybara.setIsWedding(0);
                                capybaraWedding.setIsWedding(0);
                                Request request = new Request(capybara, query.data());
                                Request request1 = new Request(capybaraWedding, query.data());
                                capybaraDAO.updateDB(request);
                                capybaraDAO.updateDB(request1);
                                bot.execute(new SendPhoto(chatId, new CapybaraPhoto("photo", -209917797, 457245520).toUrl())
                                        .caption("Ура!\nТеперь " + capybara.getName() + " и " + capybaraWedding.getName() + " Женаты!"));
                            } else {
                                bot.execute(new SendMessage(chatId, "Ты не можешь согласиться на брак!\nВозможно тебе никто и не делал предложения"));
                            }
                        } else {
                            bot.execute(new SendMessage(chatId, "Ты не можешь согласиться на брак!\nВозможно тебе никто и не делал предложения"));
                        }
                    }
                }
            }
            case "refuse_wedding" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(userId.toString(), chatId.toString());
                try {
                    capybara.checkNotNull();
                    if (capybaraDAO.checkChangeName(userId, chatId))
                        capybaraDAO.checkOriginalName(capybara.getName(), userId, chatId);
                    if (!capybara.getWantsWedding().toString().equals("0")) {
                        Capybara capybara1 = capybaraDAO.getCapybaraFromDB(capybara.getWantsWedding().toString(), chatId.toString());
                        capybara1.setWantsWedding(0L);
                        capybara.setWantsWedding(0L);
                        capybara.setIsWedding(0);
                        capybara1.setIsWedding(0);
                        Request request = new Request(capybara, query.data());
                        Request request1 = new Request(capybara1, query.data());
                        capybaraDAO.updateDB(request);
                        capybaraDAO.updateDB(request1);
                        bot.execute(new SendMessage(chatId, "Вы забрали свои слова назад"));
                    } else {
                        bot.execute(new SendMessage(chatId, "Тебе нечего забирать назад!"));
                    }
                } catch (CapybaraNullException e) {
                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA));
                }
            }
            case "un_wedding" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(userId.toString(), chatId.toString());
                Capybara secondCapybara = capybaraDAO.getCapybaraFromDB(capybara.getWedding(), chatId.toString());
                if (!secondCapybara.getName().equals("null")) {
                    try {
                        capybara.checkNotNull();
                        if (capybara.getWedding().equals(secondCapybara.getUsername().getUserID()) &&
                                secondCapybara.getWantsWedding().toString().equals(capybara.getUsername().getUserID()) && secondCapybara.getIsWedding() == 1) {
                            capybara.setWedding("0");
                            secondCapybara.setWedding("0");
                            capybara.setWantsWedding(0L);
                            secondCapybara.setWantsWedding(0L);
                            Request request = new Request(capybara, query.data());
                            Request request1 = new Request(secondCapybara, query.data());
                            capybaraDAO.updateDB(request);
                            capybaraDAO.updateDB(request1);
                            bot.execute(new SendMessage(chatId, "Капибары разведены! Теперь у каждой своя дорога"));
                        }
                    } catch (CapybaraNullException e) {
                        bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA));
                    }
                } else {
                    bot.execute(new SendMessage(chatId, "Тебе не с кем разводиться!"));
                }
            }
            case "exactly_delete" -> {
                capybaraDAO.deleteCapybara(userId.toString(), chatId.toString());
                bot.execute(new SendMessage(chatId, "Ты выкинул бедную капибарку(\nНадеюсь ты счастлив!"));
            }
            default -> System.out.println(query.data());
        }
    }

    private int race(Capybara capybara, Integer date) {
        if (date >= capybara.getRace().getTimeRemaining()) {
            return 5 + (capybara.getLevel() / 10);
        } else {
            return capybara.getRace().getLevel();
        }
    }
}
