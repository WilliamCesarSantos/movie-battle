package br.com.santos.william.moviebattle.user;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserWarmUp {

    private final UserService userService;

    public UserWarmUp(UserService userService) {
        this.userService = userService;
    }

    @EventListener
    public void warmUp(ApplicationReadyEvent event) {
        var user = new User();
        user.setPassword("123");
        user.setUsername("will");
        user.setName("will");
        user.setRoles(List.of("*"));
        userService.insert(user);

        user = new User();
        user.setPassword("123");
        user.setUsername("admin");
        user.setName("administrator");
        user.setRoles(List.of("*"));
        userService.insert(user);
    }
}
