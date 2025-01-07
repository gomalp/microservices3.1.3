package ru.itmentor.spring.boot_security.demo.RestControllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itmentor.spring.boot_security.demo.model.User;
import ru.itmentor.spring.boot_security.demo.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/rest/admin")
public class AdminRestController {
    private static final Logger logger= LoggerFactory.getLogger(AdminRestController.class);
    private final UserService userService;

    public AdminRestController(UserService userService) {
        this.userService = userService;
    }

    // Получение списка всех пользователей
    @GetMapping()
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    // Получение конкретного пользователя по id
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    // Создание нового пользователя
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody User userRequest) {
        if (userService.existsByUserName(userRequest.getUsername())) {
             return ResponseEntity.badRequest().body("Username is already taken!"); // Возвращаем сообщение об ошибке
        }
        userService.saveUser(userRequest);
       return ResponseEntity.ok(userRequest);
    }

    // Редактирование пользователя
    @PutMapping("/users/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userRequest) {
        // userRequest так же должен содержать username/password/roles в JSON.
        userRequest.setId(id);
        userService.saveUser(userRequest);
        return ResponseEntity.ok(userRequest);
    }

    // Удаление пользователя
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        logger.info(" ***1* AdminRestController delete {}",id);

        // Проверяем, существует ли пользователь с указанным id
        if (!userService.existsById(id)) {
            // Пользователь не найден, возвращаем JSON с сообщением об ошибке
            String jsonResponse = "{\"error\": \"Пользователь с id=" + id + " не найден\"}";
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonResponse);
        }
            userService.deleteUserById(id);
        logger.info(" ***2* AdminRestController delete {}",id);
        String jsonResponse = "{\"message\": \"пользователь с id=" + id + " успешно удален\"}";
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(jsonResponse);
    }
}