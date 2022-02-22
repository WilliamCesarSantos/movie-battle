package br.com.santos.william.moviebattle.user;

import br.com.santos.william.moviebattle.commons.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping(produces = {APPLICATION_JSON_VALUE})
    public Page<User> list(Pageable pageable) {
        return service.list(pageable);
    }

    @GetMapping(value = "/{id", produces = {APPLICATION_JSON_VALUE})
    public User findById(@PathVariable("id") Long id) {
        return service.findById(id)
                .orElseThrow(ResourceNotFoundException::new);
    }

    @PostMapping(consumes = {APPLICATION_JSON_VALUE}, produces = {APPLICATION_JSON_VALUE})
    public User insert(@Valid @RequestBody User user) {
        return service.insert(user);
    }

}
