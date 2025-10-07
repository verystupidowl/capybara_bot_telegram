package ru.tggc.capybaratelegrambot.config.sm;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import ru.tggc.capybaratelegrambot.domain.sm.event.BossFightEvents;
import ru.tggc.capybaratelegrambot.domain.sm.state.BossFightStates;

import java.util.EnumSet;

import static ru.tggc.capybaratelegrambot.domain.sm.event.BossFightEvents.BATTLE_FINISHED;
import static ru.tggc.capybaratelegrambot.domain.sm.event.BossFightEvents.BOSS_DONE;
import static ru.tggc.capybaratelegrambot.domain.sm.event.BossFightEvents.PLAYERS_CHOSE;
import static ru.tggc.capybaratelegrambot.domain.sm.event.BossFightEvents.PLAYERS_DONE;
import static ru.tggc.capybaratelegrambot.domain.sm.state.BossFightStates.BOSS_TURN;
import static ru.tggc.capybaratelegrambot.domain.sm.state.BossFightStates.END_BATTLE;
import static ru.tggc.capybaratelegrambot.domain.sm.state.BossFightStates.PLAYER_TURN;
import static ru.tggc.capybaratelegrambot.domain.sm.state.BossFightStates.SEND_MESSAGE;
import static ru.tggc.capybaratelegrambot.domain.sm.state.BossFightStates.WAITING_FOR_PLAYERS;

@Configuration
@EnableStateMachineFactory
@RequiredArgsConstructor
public class FightStateMachineConfig extends StateMachineConfigurerAdapter<BossFightStates, BossFightEvents> {
    private final FightStateMachineActionsConfig actions;

    @Override
    public void configure(StateMachineStateConfigurer<BossFightStates, BossFightEvents> states) throws Exception {
        states
                .withStates()
                .initial(WAITING_FOR_PLAYERS)
                .states(EnumSet.allOf(BossFightStates.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<BossFightStates, BossFightEvents> transitions) throws Exception {
        transitions
                .withExternal()
                .source(WAITING_FOR_PLAYERS)
                .target(PLAYER_TURN)
                .event(PLAYERS_CHOSE)
                .action(actions.playerTurnAction())
                .and()
                .withExternal()
                .source(PLAYER_TURN)
                .target(BOSS_TURN)
                .event(PLAYERS_DONE)
                .action(actions.bossTurnAction())
                .and()
                .withExternal()
                .source(PLAYER_TURN)
                .target(END_BATTLE)
                .event(BATTLE_FINISHED)
                .action(actions.finishBattleAction())
                .and()
                .withExternal()
                .source(BOSS_TURN)
                .target(SEND_MESSAGE)
                .event(BOSS_DONE)
                .action(actions.sendMessagesAction())
                .and()
                .withExternal()
                .source(SEND_MESSAGE)
                .target(END_BATTLE)
                .event(BATTLE_FINISHED)
                .action(actions.finishBattleAction())
                .and()
                .withExternal()
                .source(SEND_MESSAGE)
                .target(WAITING_FOR_PLAYERS)
                .event(BossFightEvents.TURN_FINISHED);
    }
}
