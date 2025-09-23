package ru.tggc.capybaratelegrambot.utils;

import lombok.experimental.UtilityClass;
import ru.tggc.capybaratelegrambot.capybara.Capybara;
import ru.tggc.capybaratelegrambot.capybara.properties.CapybaraHappiness;
import ru.tggc.capybaratelegrambot.capybara.properties.CapybaraSatiety;

import java.time.Duration;
import java.util.Random;

@UtilityClass
public class Utils {

    public static String timeToString(Integer secs) {
        long hour = secs / 3600,
                min = secs / 60 % 60,
                sec = secs % 60;
        return String.format("%02d:%02d:%02d", hour, min, sec);
    }

    public static String formatDuration(Duration duration) {
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();
        if (hours > 0) return hours + "ч " + minutes + "м";
        if (minutes > 0) return minutes + "м " + seconds + "с";
        return seconds + "с";
    }

    public static String levelUp(Capybara capybara) {
        Random random = new Random();
        String returnString = "";
        if (capybara.getSatiety().getLevel() >= 100 + ((capybara.getLevel() / 10) * 10 * 2)) {
            capybara.setSatiety(new CapybaraSatiety(capybara.getSatiety().getTimeRemaining(), 0));
            capybara.setLevel(capybara.getLevel() + 1);
            capybara.setCurrency(capybara.getCurrency() + 50);
            returnString = "[id" + capybara.getUsername().getUserID() + "|" + capybara.getName() +
                    "],твоя капибара достигла нового уровня и заработала 50 арбузных долек! Поздравляю!";
            if (capybara.getLevel() >= 10 && capybara.getLevel() < 20) {
                if (capybara.getIndexOfType() != 1) {
                    capybara.setIndexOfType(1);
                    capybara.setCurrency(capybara.getCurrency() + 100);
                    return returnString + "[id" + capybara.getUsername().getUserID() + "|" + capybara.getName() +
                            "],твоя капибара достигла нового типа и получила дополнительно 100 арбузных долек! Поздравляю!";
                }
            } else if (capybara.getLevel() >= 20 && capybara.getLevel() < 30) {
                if (capybara.getIndexOfType() != 2) {
                    capybara.setIndexOfType(2);
                    capybara.setCurrency(capybara.getCurrency() + 100);
                    return returnString + "[id" + capybara.getUsername().getUserID() + "|" + capybara.getName() +
                            "],твоя капибара достигла нового типа и получила дополнительно 100 арбузных долек! Поздравляю!";
                }
            } else if (capybara.getLevel() >= 30 && capybara.getLevel() < 40) {
                if (capybara.getIndexOfType() != 3) {
                    capybara.setIndexOfType(3);
                    capybara.setCurrency(capybara.getCurrency() + 100);
                    return returnString + "[id" + capybara.getUsername().getUserID() + "|" + capybara.getName() +
                            "],твоя капибара достигла нового типа и получила дополнительно 100 арбузных долек! Поздравляю!";
                }
            } else if (capybara.getLevel() >= 40 && capybara.getLevel() < 50) {
                if (capybara.getIndexOfType() != 4) {
                    capybara.setIndexOfType(4);
                    capybara.setCurrency(capybara.getCurrency() + 100);
                    return returnString + "[id" + capybara.getUsername().getUserID() + "|" + capybara.getName() +
                            "],твоя капибара достигла нового типа и получила дополнительно 100 арбузных долек! Поздравляю!";
                }
            } else if (capybara.getLevel() >= 50 && capybara.getLevel() < 60) {
                if (capybara.getIndexOfType() != 5) {
                    capybara.setIndexOfType(5);
                    capybara.setCurrency(capybara.getCurrency() + 100);
                    return returnString + "[id" + capybara.getUsername().getUserID() + "|" + capybara.getName() +
                            "],твоя капибара достигла нового типа и получила дополнительно 100 арбузных долек! Поздравляю!";
                }
            } else if (capybara.getLevel() >= 60 && capybara.getLevel() < 70) {
                if (capybara.getIndexOfType() != 6) {
                    capybara.setIndexOfType(6);
                    capybara.setCurrency(capybara.getCurrency() + 100);
                    return returnString + "[id" + capybara.getUsername().getUserID() + "|" + capybara.getName() +
                            "],твоя капибара достигла нового типа и получила дополнительно 100 арбузных долек! Поздравляю!";
                }
            } else if (capybara.getLevel() >= 70 && capybara.getLevel() < 80) {
                if (capybara.getIndexOfType() != 7) {
                    capybara.setIndexOfType(7);
                    capybara.setCurrency(capybara.getCurrency() + 500);
                    return returnString + "[id" + capybara.getUsername().getUserID() + "|" + capybara.getName() +
                            "],твоя капибара достигла нового типа и получила дополнительно 100 арбузных долек! Поздравляю!";
                }
            } else if (capybara.getLevel() >= 80 && capybara.getLevel() < 90) {
                if (capybara.getIndexOfType() != 8) {
                    capybara.setIndexOfType(8);
                    capybara.setCurrency(capybara.getCurrency() + 100);
                    return returnString + "[id" + capybara.getUsername().getUserID() + "|" + capybara.getName() +
                            "],твоя капибара достигла нового типа и получила дополнительно 100 арбузных долек! Поздравляю!";
                }
            }
        } else if (capybara.getLevel() >= 90 && capybara.getLevel() < 100) {
            if (capybara.getIndexOfType() != 9) {
                capybara.setIndexOfType(9);
                capybara.setCurrency(capybara.getCurrency() + 100);
                return returnString + "[id" + capybara.getUsername().getUserID() + "|" + capybara.getName() +
                        "],твоя капибара достигла нового типа и получила дополнительно 100 арбузных долек! Поздравляю!";
            }
        } else if (capybara.getLevel() >= 100 && capybara.getLevel() < 150) {
            if (capybara.getIndexOfType() != 10) {
                capybara.setIndexOfType(10);
                capybara.setCurrency(capybara.getCurrency() + 500);
                return returnString + "[id" + capybara.getUsername().getUserID() + "|" + capybara.getName() +
                        "],твоя капибара достигла нового типа и получила дополнительно 100 арбузных долек! Поздравляю!";
            }
        } else if (capybara.getLevel() >= 150) {
            if (capybara.getIndexOfType() != 11) {
                capybara.setIndexOfType(11);
                capybara.setCurrency(capybara.getCurrency() + 500);
                return returnString + "[id" + capybara.getUsername().getUserID() + "|" + capybara.getName() +
                        "],твоя капибара достигла МАКСИМАЛЬНОГО типа! Вот это да!\n За такое и 500 арбузных долек не жалко!" +
                        "\nПродолжай играть, может есть типы выше...\uD83E\uDD14";
            }
        }
        if (capybara.getHappiness().getLevel() >= 100 + ((capybara.getLevel() / 10) * 10 * 2)) {
            capybara.setHappiness(new CapybaraHappiness(capybara.getHappiness().getTimeRemaining(), 0));
            capybara.setLevel(capybara.getLevel() + 1);
            capybara.setCurrency(capybara.getCurrency() + 50);
            returnString = "[id" + capybara.getUsername().getUserID() + "|" + capybara.getName() +
                    "],твоя капибара достигла нового уровня и заработала 50 арбузных долек! Поздравляю!";
            if (capybara.getLevel() >= 10 && capybara.getLevel() < 20) {
                if (capybara.getIndexOfType() != 1) {
                    capybara.setIndexOfType(1);
                    return returnString + "[id" + capybara.getUsername().getUserID() + "|" + capybara.getName() +
                            "],твоя капибара достигла нового типа и получила дополнительно 100 арбузных долек! Поздравляю!";
                }
            } else if (capybara.getLevel() >= 20 && capybara.getLevel() < 30) {
                if (capybara.getIndexOfType() != 2) {
                    capybara.setIndexOfType(2);
                    return returnString + "[id" + capybara.getUsername().getUserID() + "|" + capybara.getName() +
                            "],твоя капибара достигла нового типа и получила дополнительно 100 арбузных долек! Поздравляю!";
                }
            } else if (capybara.getLevel() >= 30 && capybara.getLevel() < 40) {
                if (capybara.getIndexOfType() != 3) {
                    capybara.setIndexOfType(3);
                    return returnString + "[id" + capybara.getUsername().getUserID() + "|" + capybara.getName() +
                            "],твоя капибара достигла нового типа и получила дополнительно 100 арбузных долек! Поздравляю!";
                }
            } else if (capybara.getLevel() >= 40 && capybara.getLevel() < 50) {
                if (capybara.getIndexOfType() != 4) {
                    capybara.setIndexOfType(4);
                    return returnString + "[id" + capybara.getUsername().getUserID() + "|" + capybara.getName() +
                            "],твоя капибара достигла нового типа и получила дополнительно 100 арбузных долек! Поздравляю!";
                }
            } else if (capybara.getLevel() >= 50 && capybara.getLevel() < 60) {
                if (capybara.getIndexOfType() != 5) {
                    capybara.setIndexOfType(5);
                    capybara.setCurrency(capybara.getCurrency() + 100);
                    return returnString + "[id" + capybara.getUsername().getUserID() + "|" + capybara.getName() +
                            "],твоя капибара достигла нового типа и получила дополнительно 100 арбузных долек! Поздравляю!";
                }
            } else if (capybara.getLevel() >= 60 && capybara.getLevel() < 70) {
                if (capybara.getIndexOfType() != 6) {
                    capybara.setIndexOfType(6);
                    capybara.setCurrency(capybara.getCurrency() + 100);
                    return returnString + "[id" + capybara.getUsername().getUserID() + "|" + capybara.getName() +
                            "],твоя капибара достигла нового типа и получила дополнительно 100 арбузных долек! Поздравляю!";
                }
            } else if (capybara.getLevel() >= 70 && capybara.getLevel() < 80) {
                if (capybara.getIndexOfType() != 7) {
                    capybara.setIndexOfType(7);
                    capybara.setCurrency(capybara.getCurrency() + 500);
                    return returnString + "[id" + capybara.getUsername().getUserID() + "|" + capybara.getName() +
                            "],твоя капибара достигла нового типа и получила дополнительно 100 арбузных долек! Поздравляю!";
                }
            } else if (capybara.getLevel() >= 80 && capybara.getLevel() < 90) {
                if (capybara.getIndexOfType() != 8) {
                    capybara.setIndexOfType(8);
                    capybara.setCurrency(capybara.getCurrency() + 100);
                    return returnString + "[id" + capybara.getUsername().getUserID() + "|" + capybara.getName() +
                            "],твоя капибара достигла нового типа и получила дополнительно 100 арбузных долек! Поздравляю!";
                }
            } else if (capybara.getLevel() >= 90 && capybara.getLevel() < 100) {
                if (capybara.getIndexOfType() != 9) {
                    capybara.setIndexOfType(9);
                    capybara.setCurrency(capybara.getCurrency() + 500);
                    return returnString + "[id" + capybara.getUsername().getUserID() + "|" + capybara.getName() +
                            "],твоя капибара достигла нового типа и получила дополнительно 100 арбузных долек! Поздравляю!";
                }
            } else if (capybara.getLevel() >= 100 && capybara.getLevel() < 150) {
                if (capybara.getIndexOfType() != 10) {
                    capybara.setIndexOfType(10);
                    capybara.setCurrency(capybara.getCurrency() + 500);
                    return returnString + "[id" + capybara.getUsername().getUserID() + "|" + capybara.getName() +
                            "],твоя капибара достигла нового типа и получила дополнительно 100 арбузных долек! Поздравляю!";
                } else if (capybara.getLevel() >= 150) {
                    if (capybara.getIndexOfType() != 11) {
                        capybara.setIndexOfType(11);
                        capybara.setCurrency(capybara.getCurrency() + 500);
                        return returnString + "[id" + capybara.getUsername().getUserID() + "|" + capybara.getName() +
                                "],твоя капибара достигла МАКСИМАЛЬНОГО типа! Вот это да!\n За такое и 500 арбузных долек не жалко!" +
                                "\nПродолжай играть, может есть типы выше...\uD83E\uDD14";
                    }
                }
            }
        }
        return "";
    }
}
