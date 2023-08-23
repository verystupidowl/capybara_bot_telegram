package ru.tggc.capibaraBotTelegram.Utils;


import com.pengrad.telegrambot.model.Message;
import ru.tggc.capibaraBotTelegram.DataBase.CapybaraDAO;
import ru.tggc.capibaraBotTelegram.capybara.Capybara;

import java.util.Objects;

public class Text {
    public final String ALREADY_HAVE_CAPYBARA = "У тебя уже есть капибара!\n" +
            "Ты можешь выкинуть ее и взять новую";
    public final String DONT_HAVE_CAPYBARA = "Сперва возьми капибару!";
    public final String ALREADY_ON_WORK = "Твоя капибара уже на работе!\n " +
            "Неужели ты хочешь, чтобы она работала на нескольких работах???";

    public final String newLevel(String userId, String name) {
        return "[id" + userId + "|" + name +
                "],Ваша капибара достигла нового уровня! Поздравляю!";
    }

    public final String newType(String userId, String name) {
        return "[id" + userId + "|" + name +
                "],Ваша капибара достигла нового типа! Поздравляю!";
    }

    public final String LIST_OF_COMMANDS = """
            \uD83D\uDCC3Список команд:

            Моя капибара - показывает всю информацию о твоей капибаре✨

            Топ капибар - показывает топ капибар по уровню на данный момент\uD83D\uDD1D

            Уволиться с работы - уволиться с работы\uD83D\uDC4E

            В главное меню - возвращает кнопки снизу, если их не было\uD83D\uDC47\uD83C\uDFFB

            Пожениться (пересланное сообщение пользователя, на котором хочешь пожениться) - поженить капибар\uD83D\uDC8D

            Расторгнуть брак - расторгнуть брак\uD83D\uDE45\uD83C\uDFFB\u200D♂

            Брак подарок - принять подарок своей капибаре и капибаре партнеру\uD83C\uDF81

            Забег <отметка пользователя , с кем хочешь устроить забег> (или пересланное сообщение игрока, с кем хочешь устроить забег) - игра-гонка, в которой соревнуются 2 капибары, кто быстрее прибежит к финишу\uD83C\uDFCE\s

            КМН - игра Камень, Ножницы, Бумага на 50 долек\uD83C\uDF49

            Казино <Ставка-количество долек> <на что ставишь> - казино\uD83C\uDFB0
            Пример: Казино 500 красное

            Перевести/передать дольки <Количество> (пересланное сообщение пользователя, кому хочешь перевести дольки) - переводит дольки\uD83C\uDF49

            Восстановить (пересланное сообщение бота, когда он последний раз присылал фото твоей капибары) - восстанавливает фото твоей капибары, если она была удалена\uD83D\uDD04

            Список будет пополняться\uD83D\uDCC8""";

    public final String getMyCapybara(Capybara capybara, Message message, CapybaraDAO capybaraDAO) {
        String wins = "\n\n\uD83E\uDD47Побед в забегах: " + capybara.getWins() + "\n\uD83D\uDE14Поражений в забегах: " + capybara.getDefeats();
        return "Твоя капибара:\n✨Имя: " + capybara.getName() + "\n\uD83C\uDF1FУровень капибары: " + capybara.getLevel() + "\n\uD83D\uDC51Тип капибары: " +
                capybara.getType(capybara.getIndexOfType()) +
                "\n\uD83E\uDD71Бодрость капибары: " + capybara.getRace().getLevel() +
                "\n\uD83D\uDCBCРабота: " + (capybara.getJob().getStringJob(capybara).equals("null") ? "Безработная" : capybara.getJob().getStringJob(capybara)) +
                "\n\uD83C\uDF49Дольки арбуза: " + capybara.getCurrency() +
                (!Objects.equals(message.from().id(), message.chat().id()) ? ("\n\uD83D\uDC8DБрак: " + (capybara.getWedding().equals("0") ? "Нет" : (capybaraDAO.getCapybaraFromDB(capybara.getWedding(), message.chat().id().toString()).getName()))) :
                        "") + "\n\uD83C\uDF3DСытость капибары: " + capybara.getSatiety().getLevel() + "/" + (100 + ((capybara.getLevel() / 10) * 10 * 2)) + "\n\uD83E\uDD29Счастье капибары: " + capybara.getHappiness().getLevel() + "/" + (100 + ((capybara.getLevel() / 10) * 10 * 2)) + (!Objects.equals(message.from().id(), message.chat().id()) ? wins : "");
    }

    public final String getTea(Capybara capybara, Capybara capybara1) {
        return "[id" + capybara.getUsername().getUserID() + "|" + capybara.getName() + "]"
                + ", твой собеседник сегодня - " + capybara1.getName() + "\nСчастье увеличено на 10";
    }

    public final String getCurrency(int random) {
        return random != 0 ? "Твоя капибара заработала целых " + random + " арбузных долек!\nВот это да!" : "Твоя капибара только учится! Поэтому вернулась домой ни с чем(";
    }

    public final String BUSTED = "КАПЕЦ! Твою капибару поймали и отобрали 10% арбузных долек";

    public final String NO_MONEY = "У твоей капибары недостаточно денег. Может ей стоит сходить на работу?";

    public final String getInfo(Capybara capybara, int message) {
        return "ℹИформация о твоей капибаре:" +
                "\n\n✨Имя: " + capybara.getName() +
                "\n ☕Чаепитие " + (capybara.getTea().getLevel() == 0 ? (capybara.getTea().getTimeRemaining() <= message ? "уже можно" :
                ("через: " + timeToString(capybara.getTea().getTimeRemaining() - message))) : "в ожидании собеседника") +
                (capybara.hasWork() ?
                        (capybara.getCapybaraBigJob().getLevel() == 0 ?
                                (capybara.getJob().getJobTimer().getLevel() == 0 ?
                                        "\n\uD83D\uDD28Отправить на работу " +
                                                (capybara.getJob().getJobTimer().getNextJob() >= message ? "через: " + timeToString(capybara.getJob().getJobTimer().getNextJob() - message) : "уже можно") :
                                        "\n\uD83D\uDD28Забрать с работы " + (capybara.getJob().getJobTimer().getTimeRemaining() >= message ? "через: " + timeToString(capybara.getJob().getJobTimer().getTimeRemaining() - message) : "уже можно")) : "")
                                + "\n\uD83D\uDCBCПовышение: " + capybara.getJob().getRise() + "/" + (capybara.getJob().getIndex() + 1) * 10 +
                                (capybara.getCapybaraBigJob().getLevel() == 1 ? (capybara.getCapybaraBigJob().getTimeRemaining() <= message ? "\n\uD83D\uDE0FМожно забрать с большого дела" :
                                        "\n\uD83D\uDE0FМожно забрать с большого дела через: " + timeToString(capybara.getCapybaraBigJob().getTimeRemaining() - message)) :
                                        capybara.getLevel() >= 20 ? (capybara.getJob().getJobTimer().getLevel() == 0 ? (capybara.getCapybaraBigJob().getNextJob() <= message ?
                                                "\n\uD83D\uDE0FМожно отправить на большое дело" : "\n\uD83D\uDE0FМожно отправить на большое дело через: " + timeToString(capybara.getCapybaraBigJob().getNextJob() - message)) : "") : "") :
                        "") + "\n\uD83C\uDF3DПокормить/откормить " + (capybara.getSatiety().getTimeRemaining() <= message ? "уже можно" :
                "через: " + timeToString(capybara.getSatiety().getTimeRemaining() - message)) +
                "\n\uD83E\uDD29Осчастливить " + (capybara.getHappiness().getTimeRemaining() <= message ? "уже можно" :
                "через: " + timeToString(capybara.getHappiness().getTimeRemaining() - message)) + (capybara.getRace().getLevel() == (5 + (capybara.getLevel() / 10)) ? "" :
                "\n\uD83E\uDD71Времени до полного восстановления бодрости:" + timeToString(capybara.getRace().getTimeRemaining() - message)) +
                "\n⬆Улучшения для гонок: " + capybara.getImprovement().toString();
    }

    public final String LIST_OF_IMPROVEMENTS = """
            ⬆Список улучшений для гонок (улучшение дается только на одну гонку!):

            1. \uD83E\uDD7EУдобные ботиночки: твоя капибара будет бежать быстрее, шанс победить увеличен.\s
            Стоимость - 50 арбузных долек

            2. \uD83C\uDF49Вкусный арбуз: шанс победы уменьшается на 5%, но при поражении капибара не потеряет счастья.\s
            Стоимость - 100 арбузных долек

            3. \uD83D\uDC8AАнтипроигрыш: шанс победить увеличивается до 90%!!! Но при поражении 30 счастья твоей капибары уходит сопернику.\s
            Стоимость - 150 арбузных долек""";

    public final String LIST_OF_THINGS_FOR_ROBBERY = """
            ⬆Список вещей для ограбления!

            1. \uD83E\uDD7EУдобные ботиночки: Твоя капибара будет бежать быстрее. Шанс быть пойманным уменьшается.
            Стоимость - 50 арбузных долек

            2. \uD83D\uDE97Быстрая машина: Твоя капибара будет ехать быстрее. Время ограбления уменьшается
            Стоимость - 75 арбузных долек

            3. \uD83D\uDCB0Мешок для денег: Твоя капибара сможет взять больше денег.
            Стоимость - 100 арбузных долек.

            4. \uD83D\uDC4C\uD83C\uDFFBНичего: Начать ограбление без улучшений""";

    public final String LIST_OF_THINGS_FOR_BIG_IT_PROJECT = """
            ⬆Список вещей для большого айти проекта!

            1. ☕Банка кофе: Твоя капибара будет работать по ночам. Шанс завершить всё вовремя увеличивается.
            Стоимость - 50 арбузных долек

            2. \uD83D\uDCDAКурсы по программированию: Твоя капибара будет работать быстрее. Время проекта уменьшается
            Стоимость - 75 арбузных долек

            3. \uD83D\uDCB0Мешок для денег: Твоя капибара сможет взять больше денег.
            Стоимость - 100 арбузных долек.

            4. \uD83D\uDC4C\uD83C\uDFFBНичего: Начать проект без улучшений""";

    public final String LIST_OF_THINGS_FOR_CASH_REPORT = """
            ⬆Список вещей для большого отчета!

            1. ☕Банка кофе: Твоя капибара будет работать по ночам. Шанс завершить всё вовремя увеличивается.
            Стоимость - 50 арбузных долек

            2. \uD83D\uDDA8Принтер: Твоя капибара будет работать быстрее. Время проекта уменьшается
            Стоимость - 75 арбузных долек

            3. \uD83D\uDCB0Мешок для денег: Твоя капибара сможет взять больше денег.
            Стоимость - 100 арбузных долек.

            4. \uD83D\uDC4C\uD83C\uDFFBНичего: Начать проект без улучшений""";

    private static String timeToString(Integer secs) {
        long hour = secs / 3600,
                min = secs / 60 % 60,
                sec = secs % 60;
        return String.format("%02d:%02d:%02d", hour, min, sec);
    }
}
