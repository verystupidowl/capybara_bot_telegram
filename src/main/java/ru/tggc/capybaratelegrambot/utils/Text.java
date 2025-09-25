package ru.tggc.capybaratelegrambot.utils;


import lombok.experimental.UtilityClass;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraInfoDto;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraTeaDto;
import ru.tggc.capybaratelegrambot.domain.dto.MyCapybaraDto;

@UtilityClass
public class Text {
    public final String ALREADY_HAVE_CAPYBARA = "У тебя уже есть капибара!\n" +
            "Ты можешь выкинуть ее и взять новую";
    public final String DONT_HAVE_CAPYBARA = "Сперва возьми капибару!\nЧтобы сделать это напиши \"Взять капибару\"";
    public final String ALREADY_ON_WORK = "Твоя капибара уже на работе!\n " +
            "Неужели ты хочешь, чтобы она работала на нескольких работах???";

    public String newLevel(String userId, String name) {
        return "[id" + userId + "|" + name +
                "],Ваша капибара достигла нового уровня! Поздравляю!";
    }

    public String newType(String userId, String name) {
        return "[id" + userId + "|" + name +
                "],Ваша капибара достигла нового типа! Поздравляю!";
    }

    public final String START_CHANGE_NAME = """
            ✨Как ты хочешь, чтобы звали твою капибарку?
            \uD83D\uDCACТекст твоего следующего сообщения, отправленного в этот чат, незамедлительно станет новым именем твоей прелестной капибары.
            
            ⛔Имя должно быть уникальным в пределах беседы!""";

    public final String START_CHANGE_PHOTO = """
            \uD83D\uDDBCПришли новую картинку для своей капибары. Стоимость - 50 долек
            Следующая твоя картинка, отправленная в этот чат, незамедлительно станет новой фотографией твоей прелестной капибары.
            ⛔Чтобы отменить это нажми "Не менять ничего"
            Или можешь выбрать случайную за 25 \uD83C\uDF49долек!""";

    public final String FEED_FATTEN = """
            Выбери, что сделать:
            Покормить капибару: Добавляется 5 сытости. \uD83C\uDF49Арбузные дольки не тратятся
            
            Откормить капибару: Добавляется 50 сытости!!! \uD83C\uDF49Стоимость - 500 арбузных долек.""";

    public String newType(String label, Integer gift) {
        return "Поздравляю, твоя капибара достигла нового типа %s и получила дополнительно %s арбузных долек! поздравляю!"
                .formatted(label, gift.toString());
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

    public String getMyCapybara(MyCapybaraDto capybara) {
        String wins = "\n\n\uD83E\uDD47Побед в забегах: " + capybara.wins() + "\n\uD83D\uDE14Поражений в забегах: " + capybara.defeats();
        return "Твоя капибара:\n✨Имя: " + capybara.name() +
                "\n\uD83C\uDF1FУровень капибары: " + capybara.level() +
                "\n\uD83D\uDC51Тип капибары: " + capybara.type() +
                "\n\uD83E\uDD71Бодрость капибары: " + capybara.cheerfulness() +
                "\n\uD83D\uDCBCРабота: " + capybara.job() +
                "\n\uD83C\uDF49Дольки арбуза: " + capybara.currency() +
                (capybara.wedding() != null ? "\n\uD83D\uDC8DБрак: " + capybara.wedding() : "") +
                "\n\uD83C\uDF3DСытость капибары: " + capybara.satietyLevel() + "/" + capybara.satietyMaxLevel() +
                "\n\uD83E\uDD29Счастье капибары: " + capybara.happinessLevel() + "/" + capybara.happinessMaxLevel() +
                wins;
    }

    public String getTea(CapybaraTeaDto capybara, CapybaraTeaDto capybara1) {
        return capybara.name()
                + ", твой собеседник сегодня - " + capybara1.name() + "\nСчастье увеличено на 10";
    }

    public String getCurrency(int random) {
        return random != 0 ? "Твоя капибара заработала целых " + random + " арбузных долек!\nВот это да!" : "Твоя капибара только учится! Поэтому вернулась домой ни с чем(";
    }

    public final String BUSTED = "КАПЕЦ! Твою капибару поймали и отобрали 10% арбузных долек";

    public final String NO_MONEY = "У твоей капибары недостаточно денег. Может ей стоит сходить на работу?";

    public String getInfo(CapybaraInfoDto capybara) {
        StringBuilder sb = new StringBuilder("ℹ Информация о твоей капибаре:\n\n");

        sb.append("✨ Имя: ").append(capybara.name());

        sb.append("\n ☕ Чаепитие ");
        if (Boolean.FALSE.equals(capybara.isTeaWaiting())) {
            sb.append(capybara.canTea()
                    ? "уже можно"
                    : "через: " + capybara.teaTime());
        } else {
            sb.append("в ожидании собеседника");
        }

        if (capybara.hasWork()) {
            if (!capybara.isOnBigJob()) {
                if (!capybara.isWorking()) {
                    sb.append("\n🔨 Отправить на работу ")
                            .append(capybara.canGoWork()
                                    ? "уже можно"
                                    : "через: " + capybara.workTime());
                } else {
                    sb.append("\n🔨 Забрать с работы ")
                            .append(capybara.canTakeFromWork()
                                    ? "уже можно"
                                    : "через: " + capybara.takeFromWork());
                }
            }

            sb.append("\n💼 Повышение: ")
                    .append(capybara.rise())
                    .append("/")
                    .append((capybara.index() + 1) * 10);

            if (capybara.isOnBigJob()) {
                sb.append(capybara.canTakeFromBigJob()
                        ? "\n😏 Можно забрать с большого дела"
                        : "\n😏 Можно забрать с большого дела через: "
                        + timeToString(capybara.takeFromBigJob()));
            } else if (capybara.level() >= 20 && capybara.isWorking()) {
                sb.append(capybara.canGoBigJob()
                        ? "\n😏 Можно отправить на большое дело"
                        : "\n😏 Можно отправить на большое дело через: "
                        + timeToString(capybara.bigJobTime()));
            }
        }

        sb.append("\n🌽 Покормить/откормить ")
                .append(capybara.canSatiety()
                        ? "уже можно"
                        : "через: " + capybara.satietyTime());

        sb.append("\n🥳 Осчастливить ")
                .append(capybara.canHappiness()
                        ? "уже можно"
                        : "через: " + capybara.happinessTime());

        if (!capybara.canRace()) {
            sb.append("\n🩱 Времени до полного восстановления бодрости: ")
                    .append(timeToString(capybara.raceTime()));
        }

        sb.append("\n⬆ Улучшения для гонок: ").append(capybara.improvement());

        return sb.toString();
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

    private static String timeToString(long secs) {
        long hour = secs / 3600;
        long min = secs / 60 % 60;
        long sec = secs % 60;
        return String.format("%02d:%02d:%02d", hour, min, sec);
    }
}
