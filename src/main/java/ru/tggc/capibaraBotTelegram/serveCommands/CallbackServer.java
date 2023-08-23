package ru.tggc.capibaraBotTelegram.serveCommands;

import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.request.EditMessageCaption;
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
import ru.tggc.capibaraBotTelegram.capybara.Username;
import ru.tggc.capibaraBotTelegram.capybara.job.Jobs;
import ru.tggc.capibaraBotTelegram.capybara.job.MainJob;
import ru.tggc.capibaraBotTelegram.capybara.properties.CapybaraHappiness;
import ru.tggc.capibaraBotTelegram.capybara.properties.CapybaraSatiety;
import ru.tggc.capibaraBotTelegram.capybara.properties.CapybaraTea;
import ru.tggc.capibaraBotTelegram.exceptions.CapybaraException;
import ru.tggc.capibaraBotTelegram.exceptions.CapybaraNullException;
import ru.tggc.capibaraBotTelegram.keyboard.InlineKeyboardCreator;
import ru.tggc.capibaraBotTelegram.keyboard.SimpleKeyboardCreator;

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
        SimpleKeyboardCreator KeyboardCreator = new SimpleKeyboardCreator();
        System.out.println(query.data());
        Long userId = query.from().id();
        Long chatId = query.message().chat().id();

        switch (query.data()) {
            case "take_capybara" -> {
                Capybara mbCabybara = capybaraDAO.getCapybaraFromDB(userId.toString(), chatId.toString());
                if (("" + mbCabybara.getName()).equals("null")) {
                    Username newUser = new Username(userId.toString(), chatId.toString());
                    Capybara capybara;
                    if (!capybaraDAO.checkOriginalName(chatId.toString(), "Моя капибара")) {
                        int i = 1;
                        while (!capybaraDAO.checkOriginalName(chatId.toString(), "Моя капибара (" + i + ")")) {
                            i++;
                        }
                        capybara = new Capybara(newUser, "Моя капибара (" + i + ")");
                    } else {
                        capybara = new Capybara(newUser, "Моя капибара");
                    }
                    Request request = new Request(capybara, query.data());
                    capybaraDAO.addCapybaraToDB(request);
                    bot.execute(new SendPhoto(chatId, capybara.getCapybaraPhoto().toUrl())
                            .caption("Теперь у тебя есть капибара!\nПоздравляю!!!" +
                                    "\nЕё имя: " + capybara.getName() + ". \nНо ты всегда можешь поменять его!")
                            .replyMarkup(KeyboardCreator.createMenuKeyboard()));
                } else {
                    bot.execute(new SendMessage(chatId, text.ALREADY_HAVE_CAPYBARA).replyMarkup(KeyboardCreator.createMenuKeyboard()));
                }
            }
            case "info" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(userId.toString(), chatId.toString());
                try {
                    capybara.checkNotNull();
                    bot.execute(new EditMessageCaption(chatId, query.message().messageId())
                            .caption(text.getInfo(capybara, query.message().date()))
                            .replyMarkup(inlineCreator.infoKeyboard(capybara, query)));
                } catch (CapybaraNullException e) {
                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA));
                }
            }
            case "go_to_main" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(userId.toString(), chatId.toString());
                try {
                    capybara.checkNotNull();
                    bot.execute(new EditMessageCaption(chatId, query.message().messageId())
                            .caption(text.getMyCapybara(capybara, query.message(), capybaraDAO))
                            .replyMarkup(inlineCreator.myCapybaraKeyboard(capybara, query.message())));
                } catch (CapybaraNullException e) {
                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA).replyMarkup(inlineCreator.startKeyboard()));
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
            case "feed_fatten" -> {
                bot.execute(new EditMessageCaption(chatId, query.message().messageId())
                        .caption("""
                                Выбери, что сделать:
                                Покормить капибару: Добавляется 5 сытости. \uD83C\uDF49Арбузные дольки не тратятся

                                Откормить капибару: Добавляется 50 сытости!!! \uD83C\uDF49Стоимость - 500 арбузных долек.""")
                        .replyMarkup(inlineCreator.feedKeyboard()));
            }
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
                    if (query.message().date() >= capybara.getHappiness().getTimeRemaining()) {
                        bot.execute(new SendMessage(chatId, happinessThings.get(randomNumber)));
                        Request request = new Request(capybara, query.data());
                        switch (randomNumber) {
                            case 0 -> capybara.setHappiness(new CapybaraHappiness(query.message().date() + 10800,
                                    capybara.getHappiness().getLevel() + 5));
                            case 1 -> {
                                capybara.setHappiness(new CapybaraHappiness(query.message().date() + 10800,
                                        capybara.getHappiness().getLevel() + 20));
                                capybara.setSatiety(new CapybaraSatiety(capybara.getSatiety().getTimeRemaining(), capybara.getSatiety().getLevel() + 5));
                            }
                            case 2 -> capybara.setHappiness(new CapybaraHappiness(query.message().date() + 10800,
                                    capybara.getHappiness().getLevel() + 15));
                            case 3 -> capybara.setHappiness(new CapybaraHappiness(query.message().date() + 10800,
                                    capybara.getHappiness().getLevel() + 10));
                            case 4 -> {
                                if (capybara.getHappiness().getLevel() > 10) {
                                    capybara.setHappiness(new CapybaraHappiness(query.message().date() + 10800,
                                            capybara.getHappiness().getLevel() - 10));
                                } else {
                                    capybara.setHappiness(new CapybaraHappiness(query.message().date() + 10800,
                                            0));
                                }
                            }
                        }
                        bot.execute(new SendMessage(chatId, levelUp(capybara)));
                        capybaraDAO.updateDB(request);
                    } else {
                        bot.execute(new SendMessage(chatId, "Ты сможешь сделать хорошие дела для вашей капибары только через " +
                                timeToString(capybara.getHappiness().getTimeRemaining() - query.message().date())));
                    }
                } catch (CapybaraNullException e) {
                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA).replyMarkup(inlineCreator.startKeyboard()));
                }
            }
            case "feed" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(userId.toString(), chatId.toString());
                try {
                    capybara.checkNotNull();
                    if (capybaraDAO.checkChangeName(userId, chatId))
                        capybaraDAO.checkOriginalName(capybara.getName(), userId, chatId);
                    if (query.message().date() >= capybara.getSatiety().getTimeRemaining()) {
                        bot.execute(new SendMessage(chatId, "Твоя капибара успешно покушала, возвращайся через 2 часа!"));
                        bot.execute(new EditMessageCaption(chatId, query.message().messageId())
                                .caption(text.getMyCapybara(capybara, query.message(), capybaraDAO))
                                .replyMarkup(inlineCreator.myCapybaraKeyboard(capybara, query.message())));
                        capybara.setSatiety(new CapybaraSatiety(query.message().date() + 7200,
                                capybara.getSatiety().getLevel() + 5));
                        bot.execute(new SendMessage(chatId, levelUp(capybara)));
                        Request request = new Request(capybara, query.data());
                        capybaraDAO.updateDB(request);
                    } else {
                        bot.execute(new SendMessage(chatId, "Ты сможешь покормить капибару только через " +
                                timeToString(capybara.getSatiety().getTimeRemaining() - query.message().date())));
                    }
                } catch (CapybaraNullException e) {
                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA).replyMarkup(inlineCreator.startKeyboard()));
                }
            }
            case "fatten" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(userId.toString(), chatId.toString());
                try {
                    capybara.checkNotNull();
                    if (capybaraDAO.checkChangeName(userId, chatId))
                        capybaraDAO.checkOriginalName(capybara.getName(), userId, chatId);
                    if (query.message().date() >= capybara.getSatiety().getTimeRemaining()) {
                        if (capybara.getCurrency() >= 500) {
                            bot.execute(new SendPhoto(chatId, new CapybaraPhoto("photo", -209917797, 457245510).toUrl())
                                    .caption("""
                                            Твоя капибара съела целый арбуз!
                                            Её сытость увеличилась на 50!
                                            Возвращайся через 2 часа!"""));
                            capybara.setSatiety(new CapybaraSatiety(query.message().date() + 7200,
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
                                timeToString(capybara.getSatiety().getTimeRemaining() - query.message().date())));
                    }
                } catch (CapybaraNullException e) {
                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA).replyMarkup(inlineCreator.startKeyboard()));
                }
            }
            case "go_tea" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(userId.toString(), chatId.toString());
                try {
                    capybara.checkNotNull();
                    if (capybaraDAO.checkChangeName(userId, chatId))
                        capybaraDAO.checkOriginalName(capybara.getName(), userId, chatId);
                    if (capybara.getTea().getLevel() == 0) {
                        if (query.message().date() >= capybara.getTea().getTimeRemaining()) {
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
                                capybara.setTeaTime(new CapybaraTea(query.message().date() + 10800, 0));
                                capybara.setHappiness(new CapybaraHappiness(capybara.getHappiness().getTimeRemaining(), capybara.getHappiness().getLevel() + 10));
                                capybara1.setTeaTime(new CapybaraTea(query.message().date() + 10800, 0));
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
                                    timeToString(capybara.getTea().getTimeRemaining() - query.message().date())));
                        }
                    } else {
                        bot.execute(new SendMessage(chatId, "Твоя капибара и так ждёт собеседника!")
                                .replyMarkup(inlineCreator.teaKeyboard()));
                    }
                } catch (CapybaraNullException e) {
                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA).replyMarkup(inlineCreator.startKeyboard()));
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
                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA).replyMarkup(inlineCreator.startKeyboard()));
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
                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA)
                            .replyMarkup(inlineCreator.startKeyboard()));
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
                                bot.execute(new SendMessage(chatId, "Ты сможешь отправить капибару на работу только через " + timeToString(capybara.getJob().getJobTimer()
                                        .getNextJob() - query.message().date())));
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
                                                .getTimeRemaining() - query.message().date())));
                            else if (e.getMessage().equals("2"))
                                bot.execute(new SendMessage(chatId, "Твоя капибарка и не была на работе\n " +
                                        "Откуда ты ее забирать собрался?"));
                        }
                    } else {
                        bot.execute(new SendMessage(chatId, "Твоя капибарка и не была на работе\n " +
                                "Откуда ты ее забирать собрался?"));
                    }
                } catch (CapybaraNullException e) {
                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA).replyMarkup(inlineCreator.startKeyboard()));
                }
            }
            default -> System.out.println(query.data());
        }
    }
}
