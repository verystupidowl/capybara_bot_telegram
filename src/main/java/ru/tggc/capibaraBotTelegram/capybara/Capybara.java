package ru.tggc.capibaraBotTelegram.capybara;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import ru.tggc.capibaraBotTelegram.capybara.job.Job;
import ru.tggc.capibaraBotTelegram.capybara.properties.*;
import ru.tggc.capibaraBotTelegram.exceptions.CapybaraNullException;

import java.util.Random;

@Component
@Scope("prototype")
public class Capybara extends AbstractCapybara {

    private String name;
    private int indexOfType;
    private int level;
    private String wedding;
    private int wantsWedding;
    private int wins;
    private int defeats;
    private int currency;
    private int isWedding;
    private int rsp;
    private int rspId;

    private int timeZone;

    //Constructors

    @Autowired
    public Capybara(CapybaraSatiety satiety, CapybaraHappiness capybaraHappiness, CapybaraTea teaTime, CapybaraRace race,
                    CapybaraImprovement capybaraImprovement) {
        this.satiety = satiety;
        this.capybaraHappiness = capybaraHappiness;
        this.teaTime = teaTime;
        this.race = race;
        this.capybaraImprovement = capybaraImprovement;
    }

    public Capybara(Username username, String name, int timeZone) {
        this.username = username;
        this.name = name;
        this.capybaraPhoto = CapybaraPhoto.getDefaultPhoto();
        this.indexOfType = 0;
        this.teaTime = CapybaraTea.getNewTimer();
        this.satiety = CapybaraSatiety.getNewTimer();
        this.race = CapybaraRace.getNewTimer();
        this.capybaraHappiness = CapybaraHappiness.getNewTimer();
        this.level = 0;
        this.wedding = "0";
        this.wantsWedding = 0;
        this.wins = 0;
        this.defeats = 0;
        this.currency = 300;
        this.isWedding = 0;
        this.timeZone = timeZone;
    }

    public Capybara() {

    }

    public Capybara(String err) {
        if (err.equals("err"))
            this.name = "null";
        else
            this.name = err;
    }

    //Setters

    public void setCapybaraPreparation(CapybaraPreparation capybaraPreparation) {
        this.capybaraPreparation = capybaraPreparation;
    }

    public void setIndexOfType(int indexOfType) {
        this.indexOfType = indexOfType;
    }

    public int getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(int timeZone) {
        this.timeZone = timeZone;
    }

    public void setCapybaraBigJob(CapybaraBigJob capybaraBigJob) {
        this.capybaraBigJob = capybaraBigJob;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public void setDefeats(int defeats) {
        this.defeats = defeats;
    }

    public void setRace(CapybaraRace race) {
        this.race = race;
    }

    public void setWeddingGiftDate(WeddingGiftDate weddingGiftDate) {
        this.weddingGiftDate = weddingGiftDate;
    }

    public void setImprovement(CapybaraImprovement capybaraImprovement) {
        this.capybaraImprovement = capybaraImprovement;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setTeaTime(CapybaraTea tea) {
        this.teaTime = tea;
    }

    public void setWedding(String wedding) {
        this.wedding = wedding;
    }

    public void setWantsWedding(int wantsWedding) {
        this.wantsWedding = wantsWedding;
    }

    public void setIsWedding(int isWedding) {
        this.isWedding = isWedding;
    }

    public void setUsername(Username username) {
        this.username = username;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setSatiety(CapybaraSatiety satiety) {
        this.satiety = satiety;
    }


    public void setHappiness(CapybaraHappiness capybaraHappiness) {
        this.capybaraHappiness = capybaraHappiness;
    }

    //Getters

    public CapybaraPreparation getCapybaraPreparation() {
        return capybaraPreparation;
    }

    public CapybaraBigJob getCapybaraBigJob() {
        return capybaraBigJob;
    }

    public WeddingGiftDate getWeddingGiftDate() {
        return weddingGiftDate;
    }

    public CapybaraImprovement getImprovement() {
        return capybaraImprovement;
    }

    public CapybaraRace getRace() {
        return race;
    }

    public Integer getCurrency() {
        return currency;
    }

    public Integer getWins() {
        return wins;
    }

    public Integer getDefeats() {
        return defeats;
    }

    public Integer getLevel() {
        return level;
    }

    public CapybaraTea getTea() {
        return teaTime;
    }

    public static CapybaraPhoto racePhoto() {
        Random random = new Random();
        return racePhoto.get(random.nextInt(9));
    }


    public CapybaraHappiness getHappiness() {
        return capybaraHappiness;
    }

    public CapybaraSatiety getSatiety() {
        return satiety;
    }

    public String getWedding() {
        return wedding;
    }

    public Integer getWantsWedding() {
        return wantsWedding;
    }

    public int getIsWedding() {
        return isWedding;
    }

    public Username getUsername() {
        return username;
    }


    public String getType(int indexOfType) {
        return types.get(indexOfType);
    }

    public String getName() {
        return name;
    }

    public Integer getIndexOfType() {
        return indexOfType;
    }

    public void setIndexOfType(Integer indexOfType) {
        this.indexOfType = indexOfType;
    }

    public CapybaraPhoto getCapybaraPhoto() {
        return capybaraPhoto;
    }

    public void setCapybaraPhoto(CapybaraPhoto capybaraPhoto) {
        this.capybaraPhoto = capybaraPhoto;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    //Other methods


    public void checkNotNull() throws CapybaraNullException {
        if (("" + name).equals("null"))
            throw new CapybaraNullException("Capybara doesn't exist");
    }

    @Override
    public String toString() {
        return "Capybara\n[username = " + this.username.getUserID() +
                "\nname = " + this.name +
                "\nlevel = " + this.level +
                "\nindex = " + this.indexOfType +
                "\ntype = " + types.get(this.indexOfType) +
                "\nphoto = " + capybaraPhoto.toString() +
                "\nsatiety = " + this.satiety +
                "\nhappiness = " + this.capybaraHappiness +
                "\njob = " + this.getJob() +
                "\nbigJob = " + this.getCapybaraBigJob() + "]";
    }

    public boolean hasWork() {
        return !job.toString().equals("null");
    }

    public int getRsp() {
        return rsp;
    }

    public void setRsp(int rsp) {
        this.rsp = rsp;
    }

    public int getRspId() {
        return rspId;
    }

    public void setRspId(int rspId) {
        this.rspId = rspId;
    }
}
