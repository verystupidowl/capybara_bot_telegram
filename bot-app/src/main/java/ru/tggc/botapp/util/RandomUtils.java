package ru.tggc.botapp.util;

import lombok.experimental.UtilityClass;
import ru.tggc.botapp.fight.enums.BossAction;
import ru.tggc.botapp.fight.enums.BossType;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import static ru.tggc.botapp.util.CasinoTargetType.BLACK;
import static ru.tggc.botapp.util.CasinoTargetType.RED;
import static ru.tggc.botapp.util.CasinoTargetType.ZERO;

@UtilityClass
public class RandomUtils {
    private static final Random RANDOM = ThreadLocalRandom.current();

    public BossType getRandomBoss() {
        BossType[] values = BossType.values();
        return values[RANDOM.nextInt(values.length)];
    }

    public BossAction getRandomBossAction(BossType bossType) {
        List<BossAction> bossActions = bossType.getBossActions();
        return bossActions.get(RANDOM.nextInt(bossActions.size()));
    }

    public int getRandomInt(int maxValue) {
        return RANDOM.nextInt(maxValue);
    }

    public <T> T getRandomFromList(List<T> list) {
        int randomIndex = getRandomInt(list.size());
        return list.get(randomIndex);
    }

    public static CasinoTargetType randomWeighted() {
        double r = RANDOM.nextDouble();
        return Map.of(
                        ZERO, 1.0 / 37,
                        RED, (1.0 / 37) + (18.0 / 37),
                        BLACK, 1.0
                )
                .entrySet()
                .stream()
                .filter(entry -> r < entry.getValue())
                .findFirst()
                .map(Map.Entry::getKey)
                .orElseThrow(IllegalArgumentException::new);
    }

    public static boolean chance(double v) {
        return RANDOM.nextDouble(100) < v;
    }

    public static double getRandomStat(double baseStat) {
        double min = baseStat * 0.7;
        return min + (RANDOM.nextDouble() * (baseStat - min)) / 100;
    }
}
