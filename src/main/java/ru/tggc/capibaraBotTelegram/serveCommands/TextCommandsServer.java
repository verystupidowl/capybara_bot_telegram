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
import ru.tggc.capibaraBotTelegram.capybara.properties.CapybaraRace;
import ru.tggc.capibaraBotTelegram.exceptions.CapybaraNullException;
import ru.tggc.capibaraBotTelegram.keyboard.InlineKeyboardCreator;
import ru.tggc.capibaraBotTelegram.keyboard.SimpleKeyboardCreator;

import java.util.Locale;
import java.util.Objects;

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
        if (!Objects.equals(message.chat().id(), message.from().id())) {
            switch (message.text().toLowerCase(Locale.ROOT)) {
                case "начать", "[club209917797|@capybarabot] начать" -> {
                    bot.execute(new SendMessage(chatId, "Ну привет, дружок-пирожок!"));
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
                                .replyMarkup(creator.myCapybaraKeyboard(capybara, message)));
                    } catch (CapybaraNullException e) {
                        bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA).replyMarkup(creator.startKeyboard()));
                    }
                }
                default -> {
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
                                        bot.execute(new SendMessage(chatId, text.DONT_HAVE_CAPYBARA).replyMarkup(creator.startKeyboard()));
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
