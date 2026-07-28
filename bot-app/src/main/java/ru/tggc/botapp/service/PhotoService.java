package ru.tggc.botapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.tggc.botapp.config.BotPhotosProperties;
import ru.tggc.botapp.domain.model.Photo;
import ru.tggc.botapp.domain.model.enums.ImprovementValue;
import ru.tggc.botapp.domain.model.enums.WorkType;
import ru.tggc.botapp.util.RandomUtils;
import ru.tggc.telegrambotcore.dto.FileDto;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PhotoService {
    private final BotPhotosProperties botPhotosProperties;

    public String getRandomGoTeaPhoto() {
        List<String> values = botPhotosProperties.getTea().getDoTea();
        return values.get(RandomUtils.getRandomInt(values.size()));
    }

    public Photo getRandomDefaultPhoto() {
        List<String> values = botPhotosProperties.getDefaultPhotos();
        String url = values.get(RandomUtils.getRandomInt(values.size()));
        return Photo.builder()
                .url(url)
                .build();
    }

    public FileDto getRandomRacePhoto() {
        List<BotPhotosProperties.RacePhoto> values = botPhotosProperties.getRace().getRacePhotos();
        return values.stream()
                .map(rp -> new FileDto(rp.getUrl(), rp.getType()))
                .toList()
                .get(RandomUtils.getRandomInt(values.size()));
    }

    public String getImprovementPhoto(ImprovementValue improvementValue) {
        BotPhotosProperties.RaceProperty.ImprovementProperty improvement = botPhotosProperties.getRace().getImprovement();
        return switch (improvementValue) {
            case NONE -> "";
            case BOOTS -> improvement.getBoots();
            case WATERMELON -> improvement.getWatermelon();
            case ANTI_LOSE -> improvement.getAntiLose();
        };
    }

    public String getGoWorkPhoto(WorkType workType) {
        BotPhotosProperties.GoWorkProperty goWork = botPhotosProperties.getWork().getGoWork();
        return switch (workType) {
            case NONE -> "";
            case CASHIER -> goWork.getCashierGoWork();
            case CRIMINAL -> goWork.getCriminalGoWork();
            case IT -> goWork.getItGoWork();
        };
    }
}
