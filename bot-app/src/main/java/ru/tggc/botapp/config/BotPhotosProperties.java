package ru.tggc.botapp.config;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import ru.tggc.telegrambotcore.dto.FileType;

import java.util.ArrayList;
import java.util.List;

@Component
@Data
@ConfigurationProperties(prefix = "bot.photos")
public class BotPhotosProperties {
    private WorkProperty work = new WorkProperty();
    private String feed;
    private String fatten;
    private String tea;
    private String newLevel;
    private String newType;
    private String start;
    private CasinoProperty casino = new CasinoProperty();
    private String wedding;
    private FightProperty fight = new FightProperty();
    private List<String> defaultPhotos = new ArrayList<>();
    private RaceProperty race;

    @Getter
    @Setter
    public static class RaceProperty {
        private List<RacePhoto> racePhotos = new ArrayList<>();
        private ImprovementProperty improvement;

        @Getter
        @Setter
        public static class ImprovementProperty {
            private String boots;
            private String watermelon;
            private String antiLose;
        }
    }

    @Getter
    @Setter
    public static class WorkProperty {
        private WorkSetterProperty setter = new WorkSetterProperty();
        private GoWorkProperty goWork = new GoWorkProperty();
    }

    @Getter
    @Setter
    public static class GoWorkProperty {
        private String itGoWork;
        private String cashierGoWork;
        private String criminalGoWork;
    }

    @Getter
    @Setter
    public static class WorkSetterProperty {
        private String cashier;
        private String criminal;
        private String it;
    }

    @Getter
    @Setter
    public static class CasinoProperty {
        private String win;
        private String lose;
        private String setBet;
    }

    @Getter
    @Setter
    public static class FightProperty {
        private String animation;
    }

    @Getter
    @Setter
    public static class RacePhoto {
        private String url;
        private FileType type;
    }
}
