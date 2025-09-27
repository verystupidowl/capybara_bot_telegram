package ru.tggc.capybaratelegrambot.domain.model.timedaction;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraTiredException;

import java.time.Duration;
import java.time.LocalDateTime;

import static ru.tggc.capybaratelegrambot.utils.Utils.formatDuration;

@Embeddable
@NoArgsConstructor
@Setter
public class RaceAction implements TimedAction {
    private int charges;
    @Getter
    private int maxCharges;
    private LocalDateTime lastSpent;

    @Transient
    private final Duration rechargeTime = Duration.ofMinutes(1);

    public RaceAction(int maxCharges) {
        this.maxCharges = maxCharges;
        this.charges = maxCharges;
    }

    @Override
    public boolean canPerform() {
        refresh();
        return charges > 0;
    }

    @Override
    public Duration timeUntilNext() {
        refresh();
        if (charges >= maxCharges) {
            return Duration.ZERO;
        }
        Duration since = Duration.between(lastSpent, LocalDateTime.now());
        long missing = rechargeTime.minus(since).toSeconds();
        return Duration.ofSeconds(Math.max(0, missing));
    }

    public void recordRace() {
        refresh();
        if (charges <= 0) {
            throw new CapybaraTiredException(formatDuration(timeUntilNext()));
        }
        charges--;
        lastSpent = LocalDateTime.now();
    }

    private void refresh() {
        if (lastSpent == null || charges >= maxCharges) {
            return;
        }
        Duration since = Duration.between(lastSpent, LocalDateTime.now());
        long restored = since.toMinutes() / rechargeTime.toMinutes();
        if (restored > 0) {
            charges = Math.min(maxCharges, charges + (int) restored);
            lastSpent = lastSpent.plus(rechargeTime.multipliedBy(restored));
        }
    }

    public double getStaminaPercent() {
        if (charges >= maxCharges) {
            return 100;
        }
        if (lastSpent == null) {
            return (int) Math.round((charges * 100.0) / maxCharges);
        }

        Duration since = Duration.between(lastSpent, LocalDateTime.now());
        double fraction = (double) since.toMillis() / rechargeTime.toMillis();
        fraction = Math.min(1.0, fraction);

        double effectiveCharges = charges + fraction;

        return (int) Math.round((effectiveCharges * 100.0) / maxCharges);
    }

}
