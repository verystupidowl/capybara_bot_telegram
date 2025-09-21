package ru.tggc.capybaratelegrambot.aop;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.CallbackQuery;
import com.pengrad.telegrambot.request.SendMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import ru.tggc.capybaratelegrambot.aop.annotation.CheckCapybara;
import ru.tggc.capybaratelegrambot.aop.annotation.CheckType;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraAlreadyExistsException;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraException;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraHasNoMoneyException;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraNotFoundException;
import ru.tggc.capybaratelegrambot.repository.CapybaraRepository;
import ru.tggc.capybaratelegrambot.repository.UserRepository;
import ru.tggc.capybaratelegrambot.utils.Text;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
@Deprecated
public class CheckCapybaraAspect {
    private final TelegramBot bot;
    CapybaraRepository capybaraRepository;
    UserRepository userRepository;

    @Around("@annotation(ru.tggc.capybaratelegrambot.aop.annotation.CheckCapybara)")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        CheckCapybara annotation = method.getAnnotation(CheckCapybara.class);
        CheckType checkType = annotation.checkType();
        boolean needSearch = annotation.needSearch();
        if (!needSearch) {
            return handleCheckWithoutSearch(joinPoint, checkType);
        }
        return handleCheckWithSearch(joinPoint, checkType);
    }

    private Object handleCheckWithoutSearch(ProceedingJoinPoint joinPoint, CheckType checkType) throws Throwable {
        try {
            return joinPoint.proceed(joinPoint.getArgs());
        } catch (CapybaraNotFoundException e) {
            if (checkType == CheckType.CHECK_NOT_EXISTS) {
                log.info(e.getMessage(), e.getChatId());
                bot.execute(new SendMessage(e.getChatId(), Text.DONT_HAVE_CAPYBARA));
            }
        } catch (CapybaraAlreadyExistsException e) {
            if (checkType == CheckType.CHECK_EXISTS) {
                log.info(e.getMessage(), e.getChatId());
                bot.execute(new SendMessage(e.getChatId(), Text.ALREADY_HAVE_CAPYBARA));
            }
        } catch (CapybaraHasNoMoneyException e) {
            String messageToSend = "ur capy has no money(";
            String chatId = e.getChatId();
            if (chatId != null) {
                bot.execute(new SendMessage(chatId, messageToSend));
            }
        } catch (CapybaraException e) {
            log.info(e.getMessage(), e.getChatId());
            String messageToSend = e.getMessageToSend();
            String chatId = e.getChatId();
            if (chatId != null && messageToSend != null) {
                bot.execute(new SendMessage(chatId, messageToSend));
            }
        }
        return null;
    }

    private Object handleCheckWithSearch(ProceedingJoinPoint joinPoint, CheckType checkType) throws Throwable {
        CallbackQuery query = (CallbackQuery) joinPoint.getArgs()[0];
        String userId = query.from().id().toString();
        String chatId = query.maybeInaccessibleMessage().chat().id().toString();

        boolean hasCapybara = capybaraRepository.findByUserIdAndChatId(userId, chatId).isPresent();
        if ((checkType == CheckType.CHECK_NOT_EXISTS && hasCapybara) ||
                (checkType == CheckType.CHECK_EXISTS && !hasCapybara)) {
            return joinPoint.proceed();
        }
        bot.execute(new SendMessage(chatId, Text.DONT_HAVE_CAPYBARA));
        return null;
    }
}
