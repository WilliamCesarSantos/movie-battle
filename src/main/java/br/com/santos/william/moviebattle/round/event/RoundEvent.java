package br.com.santos.william.moviebattle.round.event;

import br.com.santos.william.moviebattle.round.RoundStatus;
import org.springframework.context.ApplicationEvent;

public class RoundEvent extends ApplicationEvent {

    private final RoundStatus oldStatus;
    private final RoundStatus newStatus;

    public RoundEvent(Object source, RoundStatus oldStatus, RoundStatus newStatus) {
        super(source);
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public RoundStatus getOldStatus() {
        return oldStatus;
    }

    public RoundStatus getNewStatus() {
        return newStatus;
    }
}
