package ru.tggc.botapp.service.factory;

import lombok.RequiredArgsConstructor;
import ru.tggc.botapp.domain.model.Capybara;
import ru.tggc.botapp.domain.model.User;
import ru.tggc.botapp.exceptions.CapybaraException;
import ru.tggc.botapp.service.RequestService;
import ru.tggc.botapp.service.capybara.CapybaraQueryService;
import ru.tggc.botapp.service.impl.UserServiceImpl;
import ru.tggc.telegrambotcore.dto.UpdateContext;

import static ru.tggc.telegrambotcore.util.Utils.throwIf;

@RequiredArgsConstructor
public abstract class AbstractRequestService<Rq> implements RequestService {
    private final UserServiceImpl userService;
    private final CapybaraQueryService queryService;

    @Override
    public void sendRequest(String opponentUsername, UpdateContext ctx) {
        Capybara challenger = queryService.getCapybaraByContext(ctx);
        User user = userService.getUserByUsername(opponentUsername);
        Capybara opponent = queryService.getCapybaraByUserId(user.getId(), ctx.chatId());
        throwIf(challenger.equals(opponent), () -> new CapybaraException("u cant challenge urself!"));

        challenge(challenger, opponent);
    }

    protected void challenge(Capybara challenger, Capybara opponent) {
        Rq request = getRequest(challenger, opponent);

        saveRequest(challenger, opponent, request);
    }

    protected abstract void saveRequest(Capybara challenger, Capybara opponent, Rq request);

    protected abstract Rq getRequest(Capybara challenger, Capybara opponent);
}
