package ru.tggc.capybaratelegrambot.service.factory;

import lombok.RequiredArgsConstructor;
import ru.tggc.capybaratelegrambot.domain.dto.CapybaraContext;
import ru.tggc.capybaratelegrambot.domain.model.Capybara;
import ru.tggc.capybaratelegrambot.domain.model.User;
import ru.tggc.capybaratelegrambot.exceptions.CapybaraException;
import ru.tggc.capybaratelegrambot.service.RequestService;
import ru.tggc.capybaratelegrambot.service.CapybaraService;
import ru.tggc.capybaratelegrambot.service.UserService;

@RequiredArgsConstructor
public abstract class AbstractRequestService<Rq> implements RequestService {
    private final CapybaraService capybaraService;
    private final UserService userService;

    @Override
    public void sendRequest(String opponentUsername, CapybaraContext ctx) {
        Capybara challenger = capybaraService.getCapybaraByContext(ctx);
        User user = userService.getUserByUsername(opponentUsername);
        Capybara opponent = capybaraService.getCapybaraByUserId(user.getId().toString(), ctx.chatId());

        if (challenger.equals(opponent)) {
            throw new CapybaraException("u cant challenge urself!", ctx.chatId());
        }

        challenge(challenger, opponent);
    }

    protected void challenge(Capybara challenger, Capybara opponent) {
        if (challenger.getRaceRequest() != null) {
            throw new CapybaraException("You already have an active challenge!");
        }
        if (opponent.getRaceRequest() != null) {
            throw new CapybaraException("Opponent is busy with another challenge!");
        }

        Rq request = getRequest(challenger, opponent);

        saveRequest(challenger, opponent, request);
    }

    protected abstract void saveRequest(Capybara challenger, Capybara opponent, Rq request);

    protected abstract Rq getRequest(Capybara challenger, Capybara opponent);
}
