package ru.tggc.capibaraBotTelegram.serveCommands;

import com.pengrad.telegrambot.model.Message;
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
import ru.tggc.capibaraBotTelegram.capybara.Username;
import ru.tggc.capibaraBotTelegram.capybara.properties.CapybaraRace;
import ru.tggc.capibaraBotTelegram.exceptions.CapybaraNullException;
import ru.tggc.capibaraBotTelegram.keyboard.InlineKeyboardCreator;
import ru.tggc.capibaraBotTelegram.keyboard.SimpleKeyboardCreator;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ru.tggc.capibaraBotTelegram.Utils.Utils.timeToString;

@Component
public class TextCommandsServer {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public TextCommandsServer(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void serveTextCommands(Message message, Bot bot) {
        Long chatId = message.chat().id();
        CapybaraDAO capybaraDAO = new CapybaraDAO(jdbcTemplate);
        SimpleKeyboardCreator keyboardCreator = new SimpleKeyboardCreator();
        InlineKeyboardCreator creator = new InlineKeyboardCreator();
        Text text = new Text();
        if (message.text().equals("/commandlist@capybara_pet_bot") || message.text().equals("/commandlist")) {
            bot.execute(new SendMessage(chatId, text.LIST_OF_COMMANDS));
        }
        if (!Objects.equals(message.chat().id(), message.from().id())) {
            switch (message.text().toLowerCase(Locale.ROOT)) {
                case "начать", "[club209917797|@capybarabot] начать" ->
                        bot.execute(new SendMessage(chatId, "Ну привет, дружок-пирожок!"));

                case "da" -> {
                    System.out.println(new Date().getTime());
                    System.out.println(message.date().longValue() * 1000);
                    System.out.println(new Date(message.date().longValue() * 1000));
                    bot.execute(new SendMessage(chatId, new Date(message.date().longValue() * 1000).toString()));
                }
                case "взять капибару" -> {
                    Capybara mbCabybara = capybaraDAO.getCapybaraFromDB(message.from().id().toString(), chatId.toString());
                    if (("" + mbCabybara.getName()).equals("null")) {
                        Username newUser = new Username(message.from().id().toString(), chatId.toString());
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
                case "моя капибара" -> {
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
                case "топ капибар" -> {
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
                case "в главное меню" -> {
                    bot.execute(new SendMessage(chatId, "ok").replyMarkup(keyboardCreator.createMenuKeyboard()));
                }
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
                                                        System.out.println(capybara.getRace());
                                                        try {
                                                            System.out.println(Integer.parseInt("5884986597"));
                                                        } catch (NumberFormatException e) {
                                                            System.out.println(e.getMessage());
                                                        }
                                                        capybara.setRace(new CapybaraRace(capybara.getRace().getTimeRemaining(), capybara.getRace().getLevel(), raceCapybara.getUsername().getUserID(), 1));
                                                        raceCapybara.setRace(new CapybaraRace(raceCapybara.getRace().getTimeRemaining(), raceCapybara.getRace().getLevel(), capybara.getUsername().getUserID(), 0));
                                                        Request request = new Request(capybara, message.text());
                                                        Request request1 = new Request(raceCapybara, message.text());
                                                        capybaraDAO.updateDB(request);
                                                        capybaraDAO.updateDB(request1);
                                                        bot.execute(new SendMessage(chatId, "[id" + raceCapybara.getUsername().getUserID() + "|" + raceCapybara.getName() + "]," +
                                                                " твою капибару вызвали на забег!\uD83C\uDFCE").replyMarkup(creator.raceKeyboard()));
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
                                                bot.execute(new SendMessage(chatId,  "Не повезло! Выпало чёрное⚫ " +
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
