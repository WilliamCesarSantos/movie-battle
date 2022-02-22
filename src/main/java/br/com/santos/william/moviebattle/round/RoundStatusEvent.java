package br.com.santos.william.moviebattle.round;

import org.springframework.context.ApplicationEvent;

public class RoundStatusEvent extends ApplicationEvent {

    private final RoundStatus oldStatus;
    private final RoundStatus newStatus;

    public RoundStatusEvent(Object source, RoundStatus oldStatus, RoundStatus newStatus) {
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
