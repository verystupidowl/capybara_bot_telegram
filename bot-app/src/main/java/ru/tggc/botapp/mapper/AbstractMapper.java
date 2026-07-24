package ru.tggc.botapp.mapper;

import lombok.RequiredArgsConstructor;
import ru.tggc.botapp.domain.dto.info.ActionInfo;
import ru.tggc.botapp.domain.dto.info.LongActionInfo;
import ru.tggc.botapp.domain.model.timedaction.LongTimedAction;
import ru.tggc.botapp.domain.model.timedaction.TimedAction;
import ru.tggc.botapp.service.TimedActionService;

import java.util.function.Consumer;
import java.util.function.Supplier;

@RequiredArgsConstructor
public abstract class AbstractMapper<E, D> implements Mappable<E, D> {
    private final TimedActionService timedActionService;

    protected <T extends ActionInfo> T mapActionInfo(TimedAction action, Supplier<T> factory, Consumer<T> customizer) {
        T actionInfo = factory.get();

        actionInfo.setCanAct(action.canPerform());
        actionInfo.setTimeToAct(timedActionService.getStatus(action));

        customizer.accept(actionInfo);

        return actionInfo;
    }

    protected <T extends ActionInfo> T mapActionInfo(TimedAction action, Supplier<T> factory) {
        return mapActionInfo(action, factory, _ -> {
        });
    }

    protected <T extends LongActionInfo> T mapLongAction(LongTimedAction action, Supplier<T> factory, Consumer<T> customizer) {
        T actionInfo = factory.get();
        actionInfo.setActing(action.isInProgress());
        actionInfo.setCanAct(action.canPerform());
        actionInfo.setTimeToAct(timedActionService.getStatus(action));

        if (action.isInProgress()) {
            actionInfo.setTimeToTake(timedActionService.getStatus(action));
            actionInfo.setCanTakeFrom(action.canTakeFrom());
        }

        customizer.accept(actionInfo);

        return actionInfo;
    }

    protected <T extends LongActionInfo> T mapLongAction(LongTimedAction action, Supplier<T> factory) {
        return mapLongAction(action, factory, _ -> {
        });
    }
}
