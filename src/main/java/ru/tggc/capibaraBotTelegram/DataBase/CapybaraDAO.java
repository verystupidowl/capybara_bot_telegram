package ru.tggc.capibaraBotTelegram.DataBase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.tggc.capibaraBotTelegram.capybara.Capybara;
import ru.tggc.capibaraBotTelegram.capybara.CapybaraPhoto;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class CapybaraDAO {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CapybaraDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean checkChangePhoto(Long id, Long peerId) {
        String sql = "SELECT * FROM tg_bot WHERE change_photo=1 AND user_id=? AND user_peer_id=?";
        return jdbcTemplate.query(sql, new Object[]{id.toString(), peerId.toString()}, new CapybaraMapper()).stream().findAny().orElse(null) != null;
    }

    public void changePhotoFirst(Long id, Long peerId) {
        String sql = "UPDATE tg_bot SET change_photo=1 WHERE user_id=?  AND user_peer_id=?";
        jdbcTemplate.update(sql, id.toString(), peerId.toString());
    }

    public void changePhoto(CapybaraPhoto capybaraPhoto, Long id, Long peerId) {
        String sql = "UPDATE tg_bot SET capibara_photo_url=?, change_photo=0 WHERE user_id=? AND user_peer_id=?";
        jdbcTemplate.update(sql, capybaraPhoto.toUrl(), id.toString(), peerId.toString());
    }

    public boolean checkChangeName(Long id, Long peerId) {
        String sql = "SELECT * FROM tg_bot WHERE change_name=1 AND user_id=? AND user_peer_id=?";
        return jdbcTemplate.query(sql, new Object[]{id.toString(), peerId.toString()}, new CapybaraMapper()).stream().findAny().orElse(null) != null;
    }

    public void changeNameFirst(Long id, Long peerId) {
        String sql = "UPDATE tg_bot SET change_name=1 WHERE user_id=?  AND user_peer_id=?";
        jdbcTemplate.update(sql, id.toString(), peerId.toString());
    }

    public void checkOriginalName(String name, Long id, Long peerId) {
        String sql = "UPDATE tg_bot SET capibara_name=?, change_name=0 WHERE user_id=? AND user_peer_id=?";
        jdbcTemplate.update(sql, name, id.toString(), peerId.toString());
    }

    public void notChange(Long id, Long peerId) {
        String sql = "UPDATE tg_bot SET change_name=0, change_photo=0 WHERE user_id=? AND user_peer_id=?";
        jdbcTemplate.update(sql, id.toString(), peerId.toString());
    }


    public void addCapybaraToDB(Request request) {
        String sql = "INSERT INTO tg_bot (user_id, capibara_name, capibara_type, capibara_photo_url, capibara_satiety, " +
                "capibara_happiness, satiety_date, happiness_data, wants_tea, tea_time, user_peer_id, capibara_level, wedding, wants_wedding, race, wants_race, wins, defeats" +
                ", currency, race_time, change_name, change_photo, job, job_time_remaining, on_job, job_time, improvement, job_rise, big_job_timer, " +
                "next_big_job, on_big_job, preparation_improvement, on_preparation, prepared, needed_happiness, r_s_p, r_s_p_id, started_race) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0, 0, 0, 0, 0, 0, 0 ,0, 0, ?, 0, 0, 0, 0, 0, 0, 0, 0, 0)";
        jdbcTemplate.update(sql, request.getCapybara().getUsername().getUserID(),
                request.getCapybara().getName(), request.getCapybara().getIndexOfType().toString(),
                request.getCapybara().getCapybaraPhoto().toString(),
                request.getCapybara().getSatiety().getTimeRemaining().toString(),
                request.getCapybara().getHappiness().getLevel().toString(),
                request.getCapybara().getSatiety().getLevel().toString(),
                request.getCapybara().getHappiness().getTimeRemaining().toString(),
                request.getCapybara().getTea().getLevel().toString(),
                request.getCapybara().getTea().getTimeRemaining().toString(),
                request.getCapybara().getUsername().getPeerID(),
                request.getCapybara().getLevel(),
                request.getCapybara().getWedding(),
                request.getCapybara().getWantsWedding().toString(),
                request.getCapybara().getRace().getLevel(),
                request.getCapybara().getRace().getWantsRace(),
                request.getCapybara().getWins().toString(),
                request.getCapybara().getDefeats().toString(),
                request.getCapybara().getCurrency(),
                request.getCapybara().getRace().getTimeRemaining());
    }

    public void updateImprovement(Capybara capybara) {
        String sql = "UPDATE tg_bot SET improvement=?, currency=? WHERE user_id=? AND user_peer_id=?";
        jdbcTemplate.update(sql, capybara.getImprovement().getLevel(), capybara.getCurrency(), capybara.getUsername().getUserID(),
                capybara.getUsername().getPeerID());
    }


    public void deleteCapybara(String userId, String peerId) {
        String sql = "DELETE FROM tg_bot WHERE user_id=? AND user_peer_id=?";
        jdbcTemplate.update(sql, userId, peerId);
    }

    public Capybara getCapybaraFromDB(String userId, String peerId) {
        String sql = "SELECT * FROM tg_bot WHERE user_id=? AND user_peer_id=?";
        Capybara err = jdbcTemplate.query(sql, new Object[]{userId, peerId}, new CapybaraMapper()).stream().findAny().orElse(new Capybara("err"));
        return err;
    }

    public Capybara getTeaCapybara(String userID, String peerId) {
        Capybara capybara;
        String sql = "SELECT * FROM tg_bot WHERE wants_tea = '1' AND (user_id<>? OR user_peer_id<>?)";
        capybara = jdbcTemplate.query(sql, new Object[]{userID, peerId}, new CapybaraMapper()).stream().findAny().orElse(new Capybara("err"));
        return capybara;
    }

    public void updateDB(Request request) {
        String sql = "UPDATE tg_bot SET capibara_name = ?, capibara_type = ?, capibara_photo_url = ?, capibara_satiety = ?, " +
                "capibara_happiness = ?, satiety_date = ?, happiness_data = ?, wants_tea = ?, tea_time = ?, user_peer_id = ?, capibara_level = ?, wedding = ?, wants_wedding = ?, " +
                "race = ?, wants_race = ?, wins = ?, defeats = ?, currency = ?, race_time = ?, improvement=?, is_wedding=?, wedding_gift_date=?, big_job_timer=?, next_big_job=?, on_big_job=?," +
                "preparation_improvement=?, on_preparation=?, prepared=?, started_race=?" +
                "WHERE user_id = ? AND user_peer_id = ?";
        jdbcTemplate.update(sql, request.getCapybara().getName(), request.getCapybara().getIndexOfType().toString(), request.getCapybara().getCapybaraPhoto().toString(),
                request.getCapybara().getSatiety().getLevel().toString(), request.getCapybara().getHappiness().getLevel().toString(), request.getCapybara().getSatiety().getTimeRemaining(), request.getCapybara().getHappiness().getTimeRemaining().toString(),
                request.getCapybara().getTea().getLevel().toString(), request.getCapybara().getTea().getTimeRemaining().toString(), request.getCapybara().getUsername().getPeerID(), request.getCapybara().getLevel(),
                request.getCapybara().getWedding(), request.getCapybara().getWantsWedding(), request.getCapybara().getRace().getLevel(), request.getCapybara().getRace().getWantsRace(),
                request.getCapybara().getWins().toString(), request.getCapybara().getDefeats().toString(), request.getCapybara().getCurrency(), request.getCapybara().getRace().getTimeRemaining(),
                request.getCapybara().getImprovement().getLevel(), request.getCapybara().getIsWedding(), request.getCapybara().getWeddingGiftDate().getTimeRemaining(),
                request.getCapybara().getCapybaraBigJob().getTimeRemaining(), request.getCapybara().getCapybaraBigJob().getNextJob(), request.getCapybara().getCapybaraBigJob().getLevel(),
                request.getCapybara().getCapybaraPreparation().getImprovement(), request.getCapybara().getCapybaraPreparation().getOnJob(), request.getCapybara().getCapybaraPreparation().getPrepared(),
                request.getCapybara().getRace().getStartedRace(),
                request.getCapybara().getUsername().getUserID(), request.getCapybara().getUsername().getPeerID());
    }

    public List<Capybara> topCapybara() {

        String sql = "SELECT * FROM tg_bot ORDER BY capibara_level DESC";

        return jdbcTemplate.query(sql, new CapybaraMapper()).stream().limit(10).collect(Collectors.toList());
    }

    public void newJob(Capybara capybara) {
        String sql = "UPDATE tg_bot SET job=?, job_rise=? WHERE user_id=? AND user_peer_id=?";
        jdbcTemplate.update(sql, Integer.parseInt(capybara.getJob().toString()),
                0,
                capybara.getUsername().getUserID(), capybara.getUsername().getPeerID());
    }

    public void updateJob(Capybara capybara) {
        String sql = "UPDATE tg_bot SET job=?, job_time_remaining=?, on_job=?, job_time=?, currency=?, job_rise=? WHERE user_id=? AND user_peer_id=?";
        jdbcTemplate.update(sql, Integer.parseInt(capybara.getJob().toString()), capybara.getJob().getJobTimer().getTimeRemaining(),
                capybara.getJob().getJobTimer().getLevel(), capybara.getJob().getJobTimer().getNextJob(), capybara.getCurrency(),
                capybara.getJob().getRise(),
                capybara.getUsername().getUserID(), capybara.getUsername().getPeerID());
    }

    public void deleteJob(Capybara capybara) {
        String sql = "UPDATE tg_bot SET job=0, on_job=0 WHERE user_id=? AND user_peer_id=?";
        jdbcTemplate.update(sql, capybara.getUsername().getUserID(), capybara.getUsername().getPeerID());
    }

    public void deleteChatCapybara(String peerId) {
        String sql = "DELETE FROM tg_bot WHERE user_peer_id=?";
        jdbcTemplate.update(sql, peerId);
    }

    public boolean checkOriginalName(String peerId, String name) {
        String sql = "SELECT * FROM tg_bot WHERE user_peer_id=? AND capibara_name=?";
        List<Capybara> capybaras = jdbcTemplate.query(sql, new Object[]{peerId, name}, new CapybaraMapper());
        return capybaras.isEmpty();
    }

    public void setRSP(Capybara capybara, int choose, int messageId) {
        jdbcTemplate.update("UPDATE tg_bot SET r_s_p=?, r_s_p_id=? WHERE user_id=? AND user_peer_id=?",
                choose, messageId, capybara.getUsername().getUserID(), capybara.getUsername().getPeerID());
    }

    public List<Capybara> getRSP(Capybara capybara) {
        return jdbcTemplate.query("SELECT * FROM tg_bot WHERE user_peer_id=? AND r_s_p <> 0",
                new Object[]{capybara.getUsername().getPeerID()}, new CapybaraMapper());
    }
}
