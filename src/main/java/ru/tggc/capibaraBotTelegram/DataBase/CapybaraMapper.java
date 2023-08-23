package ru.tggc.capibaraBotTelegram.DataBase;


import com.pengrad.telegrambot.model.Message;
import org.springframework.jdbc.core.RowMapper;
import ru.tggc.capibaraBotTelegram.Bot;
import ru.tggc.capibaraBotTelegram.capybara.Capybara;
import ru.tggc.capibaraBotTelegram.capybara.CapybaraPhoto;
import ru.tggc.capibaraBotTelegram.capybara.Username;
import ru.tggc.capibaraBotTelegram.capybara.job.Job;
import ru.tggc.capibaraBotTelegram.capybara.job.Jobs;
import ru.tggc.capibaraBotTelegram.capybara.job.MainJob;
import ru.tggc.capibaraBotTelegram.capybara.properties.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class CapybaraMapper implements RowMapper<Capybara> {

    @Override
    public Capybara mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        Capybara capybara = new Capybara();
        Username username = new Username();
        MainJob mainJob = new MainJob();
        CapybaraJob capybaraJob;
        CapybaraBigJob capybaraBigJob;
        CapybaraPreparation capybaraPreparation;
        int major;
        int minor;
        int rise;

        username.setUserID(resultSet.getString("user_id"));
        username.setPeerID(resultSet.getString("user_peer_id"));
        capybara.setUsername(username);
        capybara.setName(resultSet.getString("capibara_name"));
        capybara.setIndexOfType(Integer.parseInt(resultSet.getString("capibara_type")));
        capybara.setCapybaraPhoto(new CapybaraPhoto(resultSet.getString("capibara_photo_url")));
        capybara.setSatiety(new CapybaraSatiety(Integer.parseInt(resultSet.getString("satiety_date")),
                Integer.parseInt(resultSet.getString("capibara_satiety"))));
        capybara.setHappiness(new CapybaraHappiness(Integer.parseInt(resultSet.getString("happiness_data"))
                , Integer.parseInt(resultSet.getString("capibara_happiness"))));
        capybara.setTeaTime(new CapybaraTea(Integer.parseInt(resultSet.getString("tea_time")),
                Integer.parseInt(resultSet.getString("wants_tea"))));
        capybara.setLevel(resultSet.getInt("capibara_level"));
        capybara.setWedding(resultSet.getString("wedding"));
        capybara.setWantsWedding(Integer.parseInt(resultSet.getString("wants_wedding")));
        capybara.setRace(new CapybaraRace(resultSet.getInt("race_time")
                , Integer.parseInt(resultSet.getString("race")),
                resultSet.getString("wants_race"), resultSet.getInt("started_race")));
        capybara.setWins(Integer.parseInt(resultSet.getString("wins")));
        capybara.setDefeats(Integer.parseInt(resultSet.getString("defeats")));
        capybara.setCurrency(resultSet.getInt("currency"));
        CapybaraImprovement capybaraImprovement = new CapybaraImprovement();
        capybaraImprovement.setLevel(resultSet.getInt("improvement"));
        capybara.setImprovement(capybaraImprovement);
        capybara.setIsWedding(resultSet.getInt("is_wedding"));
        capybara.setWeddingGiftDate(new WeddingGiftDate(resultSet.getInt("wedding_gift_date")));
        capybara.setRsp(resultSet.getInt("r_s_p"));
        capybara.setRspId(resultSet.getInt("r_s_p_id"));
        int job = resultSet.getInt("job");
        capybaraBigJob = new CapybaraBigJob(resultSet.getInt("big_job_timer"),
                resultSet.getInt("on_big_job"), resultSet.getInt("next_big_job"));
        capybara.setCapybaraBigJob(capybaraBigJob);
        capybaraPreparation = new CapybaraPreparation(resultSet.getInt("preparation_improvement"),
                resultSet.getInt("on_preparation"), resultSet.getInt("prepared"));
        capybara.setCapybaraPreparation(capybaraPreparation);
        if (job != 0) {
            major = job / 100;
            minor = (job / 10) % 10;
            rise = resultSet.getInt("job_rise");
            switch (major) {
                case 1 -> capybara.setJob(mainJob.mainJob(Jobs.PROGRAMMING, minor));
                case 2 -> capybara.setJob(mainJob.mainJob(Jobs.CASHIER, minor));
                case 3 -> capybara.setJob(mainJob.mainJob(Jobs.CRIMINAL, minor));
            }
            capybaraJob = new CapybaraJob(resultSet.getInt("job_time_remaining"),
                    resultSet.getInt("on_job"), resultSet.getInt("job_time"));
            capybara.getJob().setRise(rise);
            capybara.getJob().setJobTimer(capybaraJob);

        } else {
            capybara.setJob(new Job() {
                @Override
                public Capybara goToWork(Message message, Capybara capybara1) {
                    return null;
                }

                @Override
                public Capybara getFromWork(Message message, Capybara capybara1, Bot bot) {
                    return null;
                }

                @Override
                public Integer getIndex() {
                    return 0;
                }

                @Override
                public void setIndex(Integer index) {

                }

                @Override
                public List<Job> getList() {
                    return null;
                }

                @Override
                public CapybaraJob getJobTimer() {
                    return new CapybaraJob();
                }

                @Override
                public void setJobTimer(CapybaraJob capybaraJob) {

                }

                @Override
                public Integer getRise() {
                    return 0;
                }

                @Override
                public void setRise(Integer rise) {

                }

                @Override
                public String toString() {
                    return "null";
                }

                @Override
                public String getStringJob(Capybara capybara) {
                    return "null";
                }

                @Override
                public Jobs getEnum() {
                    return null;
                }

            });
        }
        return capybara;
    }
}
