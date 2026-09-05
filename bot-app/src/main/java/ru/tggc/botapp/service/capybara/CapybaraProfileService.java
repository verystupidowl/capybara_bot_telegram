package ru.tggc.botapp.service.capybara;

import com.pengrad.telegrambot.model.Animation;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.PhotoSize;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tggc.botapp.domain.dto.MyCapybaraDto;
import ru.tggc.botapp.domain.dto.info.CapybaraInfoDto;
import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.domain.model.Chat;
import ru.tggc.botapp.domain.model.Photo;
import ru.tggc.botapp.domain.model.User;
import ru.tggc.botapp.exceptions.CapybaraException;
import ru.tggc.botapp.formatter.msgkey.CommonMsgKey;
import ru.tggc.botapp.formatter.msgkey.ErrorMsgKey;
import ru.tggc.botapp.keyboard.KeyboardType;
import ru.tggc.botapp.mapper.CapybaraInfoMapper;
import ru.tggc.botapp.mapper.MyCapybaraMapper;
import ru.tggc.botapp.repository.ChatRepository;
import ru.tggc.botapp.service.PhotoService;
import ru.tggc.botapp.service.impl.UserServiceImpl;
import ru.tggc.botapp.util.CapybaraBuilder;
import ru.tggc.telegrambotcore.dto.FileType;
import ru.tggc.telegrambotcore.dto.PhotoDto;
import ru.tggc.telegrambotcore.dto.UpdateContext;
import ru.tggc.telegrambotcore.ext.TelegramMessageUtils;
import ru.tggc.telegrambotcore.formatter.FormatService;
import ru.tggc.telegrambotcore.keyboard.KeyboardFactory;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CapybaraProfileService {
    private final CapybaraQueryService queryService;
    private final PhotoService photoService;
    private final FormatService formatService;
    private final MyCapybaraMapper myCapybaraMapper;
    private final CapybaraInfoMapper capybaraInfoMapper;
    private final UserServiceImpl userService;
    private final ChatRepository chatRepository;
    private final KeyboardFactory keyboardFactory;

    @Transactional
    public void deleteCapybara(UpdateContext ctx) {
        Capybara capybara = queryService.getCapybaraByContext(ctx);
        queryService.delete(capybara);
    }

    @Transactional
    public String setDefaultPhoto(UpdateContext ctx) {
        Capybara capybara = queryService.getCapybaraByContext(ctx);
        Photo photo = photoService.getRandomDefaultPhoto();

        capybara.decreaseMoney(25);
        capybara.setPhoto(photo);

        queryService.save(capybara);
        return formatService.get(CommonMsgKey.CHOSEN_RANDOM_PHOTO);
    }

    @Transactional(readOnly = true)
    public MyCapybaraDto getMyCapybara(UpdateContext ctx) {
        Capybara capybara = queryService.getMyCapybara(ctx);
        return myCapybaraMapper.toDto(capybara);
    }

    @Transactional(readOnly = true)
    public CapybaraInfoDto getInfo(UpdateContext ctx) {
        Capybara capybara = queryService.getInfoCapybara(ctx);
        return capybaraInfoMapper.toDto(capybara);
    }

    @Transactional
    public void setPhoto(UpdateContext ctx, Message message) {
        Capybara capybara = queryService.getCapybaraByContext(ctx);
        capybara.decreaseMoney(50);
        if (TelegramMessageUtils.hasPhoto(message)) {
            PhotoSize photoSize = message.photo()[0];
            Photo photo = capybara.getPhoto();
            photo.setFileId(photoSize.fileId());
            photo.setFileSize(photoSize.fileSize());
            photo.setFileUniqueId(photo.getFileUniqueId());
            photo.setType(FileType.PHOTO);
        } else if (message.animation() != null) {
            Animation animation = message.animation();
            Photo photo = capybara.getPhoto();
            photo.setFileId(animation.fileId());
            photo.setFileSize(animation.fileSize());
            photo.setFileUniqueId(photo.getFileUniqueId());
            photo.setType(FileType.DOC);
        }
        queryService.save(capybara);
    }

    @Transactional
    public PhotoDto saveCapybara(UpdateContext ctx) {
        long chatId = ctx.chatId();
        long userId = ctx.userId();

        queryService.checkExists(ctx);

        User user = userService.getUserById(userId);
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(IllegalArgumentException::new);

        int size = queryService.getCountByChatId(chatId);
        Photo photo = photoService.getRandomDefaultPhoto();
        Capybara capybara = CapybaraBuilder.buildCapybara(size, chat, user, photo);
        queryService.save(capybara);
        String caption = formatService.get(CommonMsgKey.CAPYBARA_CREATED, capybara.getName());
        return new PhotoDto(
                capybara.getPhoto().getUrl(),
                caption,
                chatId,
                keyboardFactory.getKeyboardInline(KeyboardType.TO_MAIN_MENU)
        );
    }

    @Transactional
    public void transferMoney(UpdateContext ctx, String targetUsername, Integer amount) {
        Capybara sourcecapybara = queryService.getCapybaraByContext(ctx);

        User user = userService.getUserByUsername(targetUsername);
        Capybara targetCapybara = queryService.getCapybaraByUserId(user.getId(), ctx.chatId());

        sourcecapybara.decreaseMoney(amount);
        targetCapybara.increaseMoney(amount);

        queryService.saveAll(List.of(sourcecapybara, targetCapybara)); //todo: проверка на отрицательное число
    }

    @Transactional
    public void changeName(UpdateContext ctx, String newName) {
        if (newName.length() > 25 || newName.isEmpty()) {
            throw new CapybaraException(formatService.get(ErrorMsgKey.CAPYBARA_NAME_TOO_LONG));
        }
        Capybara capybara = queryService.getCapybaraByContext(ctx);
        capybara.setName(newName);
        queryService.save(capybara);
    }
}
