package br.com.santos.william.moviebattle.battle.event;

import br.com.santos.william.moviebattle.battle.BattleStatus;
import org.springframework.context.ApplicationEvent;

public class BattleStatusEvent extends ApplicationEvent {

    private final BattleStatus oldStatus;
    private final BattleStatus newStatus;

    public BattleStatusEvent(Object source, final BattleStatus oldStatus, final BattleStatus newStatus) {
        super(source);
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public BattleStatus getOldStatus() {
        return oldStatus;
    }

    public BattleStatus getNewStatus() {
        return newStatus;
    }
}
