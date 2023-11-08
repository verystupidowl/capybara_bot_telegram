package ru.tggc.capibaraBotTelegram.serveCommands;

import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.PhotoSize;
import com.pengrad.telegrambot.model.request.*;
import com.pengrad.telegrambot.request.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.tggc.capibaraBotTelegram.Bot;
import ru.tggc.capibaraBotTelegram.DataBase.CapybaraDAO;
import ru.tggc.capibaraBotTelegram.DataBase.Request;
import ru.tggc.capibaraBotTelegram.Utils.Text;
import ru.tggc.capibaraBotTelegram.capybara.Capybara;
import ru.tggc.capibaraBotTelegram.capybara.CapybaraPhoto;
import ru.tggc.capibaraBotTelegram.capybara.Username;
import ru.tggc.capibaraBotTelegram.capybara.job.Jobs;
import ru.tggc.capibaraBotTelegram.capybara.job.MainJob;
import ru.tggc.capibaraBotTelegram.capybara.properties.CapybaraRace;
import ru.tggc.capibaraBotTelegram.capybara.properties.WeddingGiftDate;
import ru.tggc.capibaraBotTelegram.exceptions.CapybaraHasNoMoneyException;
import ru.tggc.capibaraBotTelegram.exceptions.CapybaraNullException;
import ru.tggc.capibaraBotTelegram.keyboard.InlineKeyboardCreator;
import ru.tggc.capibaraBotTelegram.keyboard.SimpleKeyboardCreator;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static ru.tggc.capibaraBotTelegram.Utils.Utils.timeToString;

@Component
@Slf4j
public class TextCommandsServer {

    private final JdbcTemplate jdbcTemplate;
    private final CapybaraDAO capybaraDAO;

    @Autowired
    public TextCommandsServer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.capybaraDAO = new CapybaraDAO(jdbcTemplate);
    }

    public synchronized void serveTextCommandsPrivate(Message message, Bot bot) {
        Long chatId = message.chat().id();
        Long userId = message.from().id();
        Text text = new Text();
        SimpleKeyboardCreator keyboardCreator = new SimpleKeyboardCreator();
        InlineKeyboardCreator creator = new InlineKeyboardCreator();
        switch (message.text()) {
            case "/command_list@capybara_pet_bot", "/command_list" -> {
                bot.execute(new SendMessage(chatId, text.LIST_OF_COMMANDS));
            }
            case "моя капибара", "/my_capybara", "/my_capybara@capybara_pet_bot" -> {
                List<Capybara> userCapybara = capybaraDAO.getCapybaraByUserId(userId);
//                String reduce = userCapybara.stream().map(Capybara::getName).reduce("", (c1, c2) -> c1 + "\n" + c2);
                bot.execute(new SendMediaGroup(chatId, userCapybara.stream()
                        .map(capybara -> capybara.getCapybaraPhoto().toUrl())
                        .map(InputMediaPhoto::new)
                        .toArray(InputMediaPhoto[]::new)));
                bot.execute(new SendMessage(chatId, "Твои капибары: ")
                        .replyMarkup(creator.myCapybaraList(userCapybara)));
            }
        }
    }

    public synchronized void serveTextCommandsPublic(Message message, Bot bot) {
        Long chatId = message.chat().id();
        SimpleKeyboardCreator keyboardCreator = new SimpleKeyboardCreator();
        InlineKeyboardCreator creator = new InlineKeyboardCreator();
        Text text = new Text();
        if (message.text().equals("/command_list@capybara_pet_bot") || message.text().equals("/command_list")) {
            bot.execute(new SendMessage(chatId, text.LIST_OF_COMMANDS));
        }
        switch (message.text().toLowerCase(Locale.ROOT)) {
            case "начать", "[club209917797|@capybarabot] начать" ->
                    bot.execute(new SendMessage(chatId, "Ну привет, дружок-пирожок!"));

            case "da" -> {
                bot.execute(new SendMessage(chatId, new Date(message.date().longValue() * 1000).toString()));
                bot.execute(new SendMessage(chatId, "По [ссылке](https://t.me/SneakyThrowss)").parseMode(ParseMode.MarkdownV2));
            }
            case "/command_list@capybara_pet_bot", "/command_list" -> {
                bot.execute(new SendMessage(chatId, text.LIST_OF_COMMANDS));
            }
            case "взять капибару" -> {
                Capybara mbCabybara = capybaraDAO.getCapybaraFromDB(message.from().id().toString(), chatId.toString());
                if (("" + mbCabybara.getName()).equals("null")) {
                    Username newUser = new Username(message.from().id().toString(), chatId.toString(), message.from().username());
                    Capybara capybara;
                    int timeZone = (int) (new Date().getTime() / 1000) - message.date();
                    if (!capybaraDAO.checkOriginalName(chatId.toString(), "Моя капибара")) {
                        int i = 1;
                        while (!capybaraDAO.checkOriginalName(chatId.toString(), "Моя капибара (" + i + ")")) {
                            i++;
                        }
                        capybara = new Capybara(newUser, "Моя капибара (" + i + ")", timeZone);
                    } else {
                        capybara = new Capybara(newUser, "Моя капибара", timeZone);
                    }
                    Request request = new Request(capybara, message.text());
                    capybaraDAO.addCapybaraToDB(request);
                    bot.execute(new SendPhoto(chatId, capybara.getCapybaraPhoto().toUrl())
                            .caption("Теперь у тебя есть капибара!\nПоздравляю!!!" +
                                    "\nЕё имя: " + capybara.getName() + ". \nНо ты всегда можешь поменять его!")
                            .replyMarkup(keyboardCreator.createMenuKeyboard()));
                } else {
                    bot.execute(new SendMessage(chatId, text.ALREADY_HAVE_CAPYBARA).replyMarkup(keyboardCreator.createMenuKeyboard()));
                }
            }
            case "моя капибара", "/my_capybara", "/my_capybara@capybara_pet_bot" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(message.from().id().toString(), chatId.toString());
                try {
                    capybara.checkNotNull();
                    if (capybaraDAO.checkChangeName(message.from().id(), message.chat().id()))
                        capybaraDAO.checkOriginalName(capybara.getName(), message.from().id(), message.chat().id());
                    capybara.setRace(new CapybaraRace(capybara.getRace().getTimeRemaining(),
                            (Math.min(race(capybara, message), (5 + (capybara.getLevel() / 10)))),
                            capybara.getRace().getWantsRace(), capybara.getRace().getStartedRace()));
                    capybaraDAO.updateDB(new Request(capybara, message.text()));
                    bot.execute(new SendPhoto(chatId, capybara.getCapybaraPhoto().toUrl())
                            .caption(text.getMyCapybara(capybara, message, capybaraDAO))
                            .replyMarkup(creator.myCapybaraKeyboard(capybara, message.date())));
                } catch (CapybaraNullException e) {
                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA));
                }
            }
            case "топ капибар", "/top_capybar", "/top_capybar@capybara_pet_bot" -> {
                List<Capybara> capybaras = capybaraDAO.topCapybara();
                CapybaraPhoto capybaraPhoto = new CapybaraPhoto(capybaras.get(0).getCapybaraPhoto().toString());
                int i = 1;
                StringBuilder s = new StringBuilder();
                for (Capybara capybara : capybaras) {
                    s.append(i).append(") ");
                    s.append(capybara.getName()).append(" (Уровень ").append(capybara.getLevel()).append(")").append("\n");
                    i++;
                }
                bot.execute(new SendPhoto(chatId, capybaraPhoto.toUrl()).caption("\uD83D\uDD1DТоп капибар на данный момент:\n " + s));
            }
            case "выкинуть бедную капибару" -> {
                bot.execute(new SendMessage(chatId, "Ты точно хочешь выкинуть свою капибару?").replyMarkup(creator.deleteCapybaraKeyboard()));
                bot.execute(new SendDocument(chatId, "CgACAgQAAx0CdQiYOQACAd5k5ju_JuE0b7eJ_3WlQtcbklonlAACJgMAAlHxBVMdVrQJnCG5KjAE"));
            }
            case "расторгнуть брак" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(message.from().id().toString(), message.chat().id().toString());
                try {
                    capybara.checkNotNull();
                    if (!capybara.getWedding().equals("0")) {
                        capybara.setWantsWedding(Long.parseLong(capybara.getWedding()));
                        capybara.setIsWedding(1);
                        Capybara weddingCapybara = capybaraDAO.getCapybaraFromDB(capybara.getWedding(), message.chat().id().toString());
                        Request request = new Request(capybara, message.text());
                        capybaraDAO.updateDB(request);
                        bot.execute(new SendMessage(chatId, weddingCapybara.getName() + ", Для подтверждения расторжения брака, напиши \"Подтвердить расторжение\"")
                                .replyMarkup(creator.unWeddingKeyboard()));
                    } else {
                        bot.execute(new SendMessage(chatId, "Тебе нечего расторгать!"));
                    }
                } catch (CapybaraNullException e) {
                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA));
                }
            }
            case "уволиться с работы" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(message.from().id().toString(), chatId.toString());
                try {
                    capybara.checkNotNull();
                    if (capybaraDAO.checkChangeName(message.from().id(), message.chat().id()))
                        capybaraDAO.checkOriginalName(capybara.getName(), message.from().id(), message.chat().id());
                    if (capybara.hasWork()) {
                        if (capybara.getJob().getJobTimer().getLevel() == 0 && capybara.getCapybaraBigJob().getLevel() == 0) {
                            capybara.setJob(new MainJob().mainJob(Jobs.PROGRAMMING, 0));
                            capybara.getJob().setRise(0);
                            capybaraDAO.deleteJob(capybara);
                            bot.execute(new SendMessage(chatId, "Ты уволилился с работы!"));
                        }
                    } else {
                        bot.execute(new SendMessage(chatId, text.ALREADY_ON_WORK));
                    }
                } catch (CapybaraNullException e) {
                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA));
                }
            }
            case "брак подарок" -> {
                Capybara capybara = capybaraDAO.getCapybaraFromDB(message.from().id().toString(), message.chat().id().toString());
                if (!("" + capybara.getName()).equals("null")) {
                    Capybara capybaraWedding = capybaraDAO.getCapybaraFromDB(capybara.getWedding(), message.chat().id().toString());
                    if (!("" + capybaraWedding.getName()).equals("null")) {
                        if (capybaraDAO.checkChangeName(message.from().id(), message.chat().id()))
                            capybaraDAO.checkOriginalName(capybara.getName(), message.from().id(), message.chat().id());
                        if (capybara.getWeddingGiftDate().getTimeRemaining() <= message.date() && capybaraWedding.getWeddingGiftDate().getTimeRemaining() <= message.date()) {
                            int currency = new Random().nextInt((capybara.getLevel() + capybaraWedding.getLevel()) == 0 ? 1 : (capybara.getLevel() + capybaraWedding.getLevel()) / 2) + 50;
                            bot.execute(new SendMessage(chatId, "Твоя капибара и капибара твоего партнера получили по " + currency + " долек!!!\nВот это да!\nВозвращайся через 3 дня за новым подарком!"));
                            capybara.setWeddingGiftDate(new WeddingGiftDate(message.date() + 259200));
                            capybaraWedding.setWeddingGiftDate(new WeddingGiftDate(message.date() + 259200));
                            capybara.setCurrency(capybara.getCurrency() + currency);
                            capybaraWedding.setCurrency(capybaraWedding.getCurrency() + currency);
                            Request request = new Request(capybara, "");
                            Request request1 = new Request(capybaraWedding, "");
                            capybaraDAO.updateDB(request);
                            capybaraDAO.updateDB(request1);
                        } else {
                            bot.execute(new SendMessage(chatId, "Ты уже недавно брал подарок! Следующий можно будет забрать только через "
                                    + timeToString(capybara.getWeddingGiftDate().getTimeRemaining() - message.date())));
                        }
                    }
                }
            }
            case "убрать клавиатуру" ->
                    bot.execute(new SendMessage(chatId, "ok").replyMarkup(new ReplyKeyboardRemove(true)));
            case "в главное меню", "/show_keyboard", "/show_keyboard@capybara_pet_bot" ->
                    bot.execute(new SendMessage(chatId, "ok").replyMarkup(keyboardCreator.createMenuKeyboard()));
            default -> {
                if (message.text().toLowerCase(Locale.ROOT).equals("гонка") || message.text().toLowerCase(Locale.ROOT).equals("забег")
                        || message.text().toLowerCase(Locale.ROOT).matches("забег @.+") ||
                        message.text().toLowerCase(Locale.ROOT).matches("гонка @.+")) {
                    if ((message.replyToMessage() != null && !Objects.equals(message.from().id(), message.replyToMessage().from().id()) ||
                            message.text().toLowerCase(Locale.ROOT).matches("забег @.+") ||
                            message.text().toLowerCase(Locale.ROOT).matches("гонка @.+"))) {
                        Long id2 = 0L;
                        if (message.text().toLowerCase(Locale.ROOT).equals("гонка") ||
                                message.text().toLowerCase(Locale.ROOT).equals("забег")) {
                            id2 = message.replyToMessage().from().id();
                        } else {
                            Pattern pattern;
                            if (message.text().toLowerCase(Locale.ROOT).matches("забег @.+")) {
                                pattern = Pattern.compile("забег @.+");
                            } else {
                                pattern = Pattern.compile("гонка @.+");
                            }
                            Matcher matcher = pattern.matcher(message.text().toLowerCase(Locale.ROOT));
                            if (matcher.find()) {
                                id2 = Long.parseLong(matcher.group(1));
                            }
                        }
                        if (!Objects.equals(id2, message.from().id()) || !Objects.equals(message.from().id(), message.chat().id())) {
                            Capybara capybara = capybaraDAO.getCapybaraFromDB(message.from().id().toString(), message.chat().id().toString());
                            Capybara raceCapybara = capybaraDAO.getCapybaraFromDB(id2.toString(), message.chat().id().toString());
                            if (!("" + capybara.getName()).equals("null") && !("" + raceCapybara.getName()).equals("null")) {
                                if (capybaraDAO.checkChangeName(message.from().id(), message.chat().id()))
                                    capybaraDAO.checkOriginalName(capybara.getName(), message.from().id(), message.chat().id());
                                if (raceCapybara.getRace().getWantsRace().equals("0")) {
                                    if (capybara.getRace().getWantsRace().equals("0")) {
                                        if ((!capybara.hasWork() || !raceCapybara.hasWork()) ||
                                                (capybara.hasWork() && capybara.getCapybaraBigJob().getLevel() == 0 &&
                                                        raceCapybara.hasWork() && raceCapybara.getCapybaraBigJob().getLevel() == 0)) {
                                            int race = Math.max(capybara.getRace().getLevel(), Math.min(race(capybara, message), (5 + (capybara.getLevel() / 10))));
                                            int race1 = Math.max(raceCapybara.getRace().getLevel(), Math.min(race(raceCapybara, message), (5 + (raceCapybara.getLevel() / 10))));
                                            capybara.setRace(new CapybaraRace(capybara.getRace().getTimeRemaining(), race, capybara.getRace().getWantsRace(), capybara.getRace().getStartedRace()));
                                            raceCapybara.setRace(new CapybaraRace(raceCapybara.getRace().getTimeRemaining(), race1, raceCapybara.getRace().getWantsRace(), capybara.getRace().getStartedRace()));
                                            if (capybara.getRace().getLevel() > 0) {
                                                if (raceCapybara.getRace().getLevel() > 0) {
                                                    capybara.setRace(new CapybaraRace(capybara.getRace().getTimeRemaining(), capybara.getRace().getLevel(), raceCapybara.getUsername().getUserID(), 1));
                                                    raceCapybara.setRace(new CapybaraRace(raceCapybara.getRace().getTimeRemaining(), raceCapybara.getRace().getLevel(), capybara.getUsername().getUserID(), 0));
                                                    Request request = new Request(capybara, message.text());
                                                    Request request1 = new Request(raceCapybara, message.text());
                                                    capybaraDAO.updateDB(request);
                                                    capybaraDAO.updateDB(request1);
                                                    bot.execute(new SendMessage(chatId, "[" + raceCapybara.getName() + "](https://t.me/" + raceCapybara.getUsername().getUsername() + ")," +
                                                            " твою капибару вызвали на забег!\uD83C\uDFCE").replyMarkup(creator.raceKeyboard()).parseMode(ParseMode.Markdown));
                                                } else {
                                                    bot.execute(new SendMessage(chatId, "[id" + raceCapybara.getUsername().getUserID() + "|" + raceCapybara.getName() + "] " +
                                                            "устала! Ей надо отдохнуть! Возвращайся через " + timeToString(raceCapybara.getRace().getTimeRemaining() - message.date()) +
                                                            "\nИли восстанови бодрость, сделав своей капибаре массаж за 50 долек арбуза!").replyMarkup(creator.raceMassage()));
                                                }
                                            } else {
                                                bot.execute(new SendMessage(chatId, "[id" + capybara.getUsername().getUserID() + "|" + capybara.getName() + "] " +
                                                        "устала! Ей надо отдохнуть! Возвращайся через " + timeToString(capybara.getRace().getTimeRemaining() - message.date()) +
                                                        "\nИли восстанови бодрость, сделав своей капибаре массаж за 50 долек арбуза!").replyMarkup(creator.raceMassage()));
                                            }
                                        } else {
                                            bot.execute(new SendMessage(chatId, "Твоя капибара или капибара соперника сейчас на большом деле!\n Они не могут участвовать в забеге!"));
                                        }
                                    } else {
                                        bot.execute(new SendMessage(chatId, "Ты не можешь вызвать капибару на гонку!\nТвоя капибара уже участвует в забеге с [id" +
                                                capybara.getRace().getWantsRace() + "|" + capybaraDAO.getCapybaraFromDB(capybara.getRace().getWantsRace(), message.chat().id().toString()).getName() + "]")
                                                .replyMarkup(creator.raceKeyboard()));
                                    }
                                } else {
                                    bot.execute(new SendMessage(chatId, "Ты не можешь вызвать капибару на гонку!\nОна уже участвует в забеге с [id" +
                                            raceCapybara.getRace().getWantsRace() + "|" + capybaraDAO.getCapybaraFromDB(raceCapybara.getRace().getWantsRace(), message.chat().id().toString()).getName() + "]")
                                            .replyMarkup(creator.raceKeyboard()));
                                }
                            } else {
                                bot.execute(new SendMessage(chatId, "Как ты собираешься участвовать в забеге без капибарки?"));
                            }
                        } else {
                            bot.execute(new SendMessage(chatId, "Чтобы вызвать кого-либо на забег, перешли любое его сообщение с сообщением \"Забег\""));
                        }
                    }
                } else if (message.text().toLowerCase(Locale.ROOT).equals("пожениться") && message.replyToMessage() != null && !Objects.equals(message.from().id(), message.chat().id())
                        && !Objects.equals(message.replyToMessage().from().id(), message.from().id())) {
                    Capybara capybara = capybaraDAO.getCapybaraFromDB(message.from().id().toString(), message.chat().id().toString());
                    Capybara capybaraWedding = capybaraDAO.getCapybaraFromDB(message.replyToMessage().from().id().toString(), message.chat().id().toString());
                    if (!("" + capybara.getName()).equals("null") && !("" + capybaraWedding.getName()).equals("null")) {
                        if (capybaraDAO.checkChangeName(message.from().id(), message.chat().id()))
                            capybaraDAO.checkOriginalName(capybara.getName(), message.from().id(), message.chat().id());
                        if (capybaraWedding.getWedding().equals("0") && capybara.getWedding().equals("0") && capybara.getWantsWedding().toString().equals("0") && capybaraWedding.getWantsWedding().toString().equals("0")
                                && capybara.getIsWedding() == 0 && capybaraWedding.getIsWedding() == 0) {
                            Request request = new Request(capybara, message.text());
                            Request request1 = new Request(capybaraWedding, message.text());
                            capybara.setWantsWedding(message.replyToMessage().from().id());
                            capybaraWedding.setWantsWedding(Long.parseLong(capybara.getUsername().getUserID()));
                            capybara.setIsWedding(1);
                            capybaraDAO.updateDB(request);
                            capybaraDAO.updateDB(request1);
                            bot.execute(new SendMessage(chatId, capybaraWedding.getName() + " Тебе сделали предложение\uD83D\uDC8D" +
                                    "\nПерешли сообщение пользователя, сделавшего его и напиши \"Принять брак\", чтобы принять предложение").replyMarkup(creator.weddingKeyboard()));
                        } else {
                            bot.execute(new SendMessage(chatId, "Вы не можете пожениться!\nВозможно кто-то из вас уже замужем или женат"));
                        }
                    } else {
                        bot.execute(new SendMessage(chatId, "Вы не можете пожениться!\nУ кого-то из вас нет капибарки"));
                    }
                } else if (message.text().toLowerCase(Locale.ROOT).matches("казино \\d+ (чёрное|красное|ноль|черное)")) {
                    int amount;
                    Capybara capybara = capybaraDAO.getCapybaraFromDB(message.from().id().toString(), chatId.toString());
                    if (!("" + capybara.getName()).equals("null")) {
                        if (capybaraDAO.checkChangeName(message.from().id(), chatId))
                            capybaraDAO.checkOriginalName(capybara.getName(), message.from().id(), message.chat().id());
                        Pattern pattern = Pattern.compile("(казино) (\\d+) (чёрное|красное|ноль|черное)");
                        Matcher matcher = pattern.matcher(message.text().toLowerCase(Locale.ROOT));
                        if (matcher.find()) {
                            amount = Integer.parseInt(matcher.group(2));
                            if (amount <= capybara.getCurrency()) {
                                if (amount >= (capybara.getLevel() / 10) * 25) {
                                    capybara.setCurrency(capybara.getCurrency() - amount);
                                    String choose = matcher.group(3);
                                    int random = new Random().nextInt(50);
                                    if (random <= 23) {
                                        if (choose.equals("чёрное") || choose.equals("черное")) {
                                            capybara.setCurrency(capybara.getCurrency() + (amount * 2));
                                            bot.execute(new SendMessage(chatId, "Вау! Твоя капибара выиграла целых " + ((amount * 2) - amount) +
                                                    " арбузных долек!\uD83C\uDF49"));
                                        } else {
                                            bot.execute(new SendMessage(chatId, "Не повезло! Выпало чёрное⚫ " +
                                                    "\nТвоя капибара проиграла " + amount +
                                                    " арбузных долек!\uD83C\uDF49"));
                                        }
                                    } else if (random <= 46) {
                                        if (choose.equals("красное")) {
                                            capybara.setCurrency(capybara.getCurrency() + (amount * 2));
                                            bot.execute(new SendMessage(chatId, "Вау! Твоя капибара выиграла целых " + ((amount * 2) - amount) +
                                                    " арбузных долек!\uD83C\uDF49"));
                                        } else {
                                            bot.execute(new SendMessage(chatId, "Не повезло! Выпало красное\uD83D\uDD34\nТвоя капибара проиграла " + amount +
                                                    " арбузных долек!\uD83C\uDF49"));
                                        }
                                    } else {
                                        if (choose.equals("ноль")) {
                                            capybara.setCurrency(capybara.getCurrency() + (amount * 10));
                                            bot.execute(new SendMessage(chatId, "Вау! Твоя капибара выиграла целых " + ((amount * 10) - amount) +
                                                    " арбузных долек!\uD83C\uDF49"));
                                        } else {
                                            bot.execute(new SendMessage(chatId, "Не повезло! Выпал ноль\uD83D\uDFE2\nТвоя капибара проиграла " + amount +
                                                    " арбузных долек!\uD83C\uDF49"));
                                        }
                                    }
                                    capybaraDAO.updateDB(new Request(capybara, ""));
                                } else {
                                    bot.execute(new SendMessage(chatId, "Минимальная ставка для тебя в казино - " +
                                            (capybara.getLevel() / 10) * 25 +
                                            " арбузных долек!\uD83C\uDF49"));
                                }
                            } else {
                                bot.execute(new SendMessage(chatId, "У твоей капибары нет столько долек!\nМожет ей стоит сходить на работу?"));
                            }
                        }
                    }
                } else if (message.replyToMessage() != null && message.text().toLowerCase(Locale.ROOT).matches("перевести дольки \\d+")) {
                    int amount;
                    if (!message.from().id().equals(message.replyToMessage().from().id())) {
                        Capybara capybara = capybaraDAO.getCapybaraFromDB(message.from().id().toString(), message.chat().id().toString());
                        if (!("" + capybara.getName()).equals("null")) {
                            Capybara capybara1 = capybaraDAO.getCapybaraFromDB(message.replyToMessage().from().id().toString(), message.chat().id().toString());
                            if (!("" + capybara1.getName()).equals("null")) {
                                if (capybaraDAO.checkChangeName(message.from().id(), message.chat().id()))
                                    capybaraDAO.checkOriginalName(capybara.getName(), message.from().id(), message.chat().id());
                                try {
                                    Pattern pattern = Pattern.compile("(перевести дольки) (\\d+)");
                                    Matcher matcher = pattern.matcher(message.text().toLowerCase(Locale.ROOT));
                                    if (matcher.find()) {
                                        amount = Integer.parseInt(matcher.group(2));
                                        transfer(capybara, capybara1, amount);
                                        bot.execute(new SendMessage(chatId, "Ух ты! Твоя капибара настолько расщедрилась, что перевела " + amount + " долек капибаре " + capybara1.getName()));
                                    } else {
                                        bot.execute(new SendMessage(chatId, """
                                                Напиши\s
                                                "Перевести дольки <Количество долек>"
                                                 и перешли сообщение человека, которому ты хочешь переслать дольки, чтобы переслать"""));
                                    }
                                } catch (CapybaraHasNoMoneyException e) {
                                    bot.execute(new SendMessage(chatId, e.getMessage()));
                                }
                            } else {
                                bot.execute(new SendMessage(chatId, "Кому ты собрался переводить деньги?"));
                            }
                        } else {
                            bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA));
                        }
                    } else {
                        bot.execute(new SendMessage(chatId, "Ты хочешь, чтобы твоя капибара перевела дольки сама себе?"));
                    }
                }
                if (capybaraDAO.checkChangeName(message.from().id(), message.chat().id())) {
                    if (message.text().length() > 1) {
                        if (message.text().length() < 25) {
                            if (!message.text().equals("null")) {
                                Capybara capybara = capybaraDAO.getCapybaraFromDB(message.from().id().toString(), message.chat().id().toString());
                                try {
                                    capybara.checkNotNull();
                                    if (capybaraDAO.checkOriginalName(message.from().id().toString(), message.text())) {
                                        capybaraDAO.checkOriginalName(message.text(), message.from().id(), message.chat().id());
                                        bot.execute(new SendMessage(chatId, "Капибара успешно переименована!!!\nНовое имя: " + message.text() + "\nПоздравляю!"));
                                    } else {
                                        bot.execute(new SendMessage(chatId, "Такое имя уже есть в этой беседе!\nИмя должно быть уникальным в пределах беседы!"));
                                    }
                                } catch (CapybaraNullException e) {
                                    bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA));
                                }
                            } else {
                                bot.execute(new SendMessage(chatId, "Имя \"null\" недоступно\nВыберите другое имя!"));
                            }
                        } else {
                            bot.execute(new SendMessage(chatId, "Длина нового имени не должна превышать 25 символов!"));
                        }
                    } else {
                        bot.execute(new SendMessage(chatId, "Введи имя"));
                    }
                }
            }
        }
    }

    private void transfer(Capybara capybara1, Capybara capybara2, int currency) {
        if (capybara1.getCurrency() >= currency) {
            capybara1.setCurrency(capybara1.getCurrency() - currency);
            capybara2.setCurrency(capybara2.getCurrency() + currency);
            Request request = new Request(capybara1, "");
            Request request1 = new Request(capybara2, "");
            capybaraDAO.updateDB(request);
            capybaraDAO.updateDB(request1);
        } else {
            throw new CapybaraHasNoMoneyException("У твоей капибары нет столько долек!\nМожет ей стоит сходить на работу," +
                    " прежде чем разбрасываться деньгами?");
        }
    }

    public void servePhoto(Message message, Bot bot) {
        CapybaraDAO capybaraDAO = new CapybaraDAO(jdbcTemplate);

        Capybara capybara = capybaraDAO.getCapybaraFromDB(message.from().id().toString(), message.chat().id().toString());
        if (!("" + capybara.getName()).equals("null")) {
            if (capybara.getCurrency() >= 50) {
                String photo = message.photo()[0].fileId();
                changePhoto(message, bot, capybaraDAO, capybara, photo);
            } else {
                capybaraDAO.changePhoto(capybara.getCapybaraPhoto(), message.from().id(), message.chat().id());
                bot.execute(new SendMessage(message.chat().id(), "У твоей капибары недостаточно арбузных долек!\nМожет ей стоит сходить на рыботу?"));
            }
        }
    }

    private void changePhoto(Message message, Bot bot, CapybaraDAO capybaraDAO, Capybara capybara, String photo) {
        if (capybaraDAO.checkChangePhoto(message.from().id(), message.chat().id())) {
            CapybaraPhoto capybaraPhoto = new CapybaraPhoto(photo);
            capybara.setCurrency(capybara.getCurrency() - 50);
            capybaraDAO.updateDB(new Request(capybara, ""));
            capybaraDAO.changePhoto(capybaraPhoto, message.from().id(), message.chat().id());
            bot.execute(new SendMessage(message.chat().id(), "Фотография твоей прекрасной капибары успешно изменена!"));
        }
    }

    private int race(Capybara capybara, Message message) {
        if (message.date() >= capybara.getRace().getTimeRemaining()) {
            return 5 + (capybara.getLevel() / 10);
        } else {
            return capybara.getRace().getLevel();
        }
    }
}
