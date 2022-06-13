package br.com.santos.william.moviebattle.battle.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class BattleException extends RuntimeException {

    public BattleException(String msg) {
        super(msg);
    }
}
