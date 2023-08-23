package ru.tggc.capibaraBotTelegram.capybara;


import ru.tggc.capibaraBotTelegram.capybara.job.Job;
import ru.tggc.capibaraBotTelegram.capybara.properties.*;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCapybara {

    protected CapybaraSatiety satiety;
    protected CapybaraHappiness capybaraHappiness;
    protected CapybaraTea teaTime;
    protected CapybaraRace race;
    protected Job job;
    protected CapybaraImprovement capybaraImprovement;
    protected CapybaraPhoto capybaraPhoto;
    protected List<String> types;
    protected WeddingGiftDate weddingGiftDate;
    protected Username username;
    protected static List<CapybaraPhoto> racePhoto;
    protected CapybaraBigJob capybaraBigJob;
    protected CapybaraPreparation capybaraPreparation;

    {
        this.types = new ArrayList<>();
        types.add("Обыкновенная капибара");
        types.add("Прикольная капибара");
        types.add("Крутая капибара");
        types.add("Невероятная капибара");
        types.add("Королевская капибара");
        types.add("Волшебная капибара");
        types.add("Капибара планетарного масштаба");
        types.add("Космическая капибара");
        types.add("Межгалактическая капибара");
        types.add("Капибара вселенского масштаба");
        types.add("Капибара пространства и времени");
        types.add("КапибараБог");


        racePhoto = new ArrayList<>();
        racePhoto.add(new CapybaraPhoto("photo", -209917797, 457241919));
        racePhoto.add(new CapybaraPhoto("photo", -209917797, 457241924));
        racePhoto.add(new CapybaraPhoto("photo", -209917797, 457241923));
        racePhoto.add(new CapybaraPhoto("photo", -209917797, 457241922));
        racePhoto.add(new CapybaraPhoto("photo", -209917797, 457241921));
        racePhoto.add(new CapybaraPhoto("photo", -209917797, 457241920));
        racePhoto.add(new CapybaraPhoto("doc", -210742067, 624812466));
        racePhoto.add(new CapybaraPhoto("doc", 178675218, 627719215));
        racePhoto.add(new CapybaraPhoto("doc", 178675218, 627720028));
    }


}
