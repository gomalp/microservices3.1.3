package ru.itmentor.spring.boot_security.demo.RestControllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.itmentor.spring.boot_security.demo.controller.AdminController;
import ru.itmentor.spring.boot_security.demo.model.User;
import ru.itmentor.spring.boot_security.demo.service.UserService;

@RestController
@RequestMapping("/rest/user")
public class UserRestController {
    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserRestController.class);
    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    // Проверка, является ли пользователь администратором или запрашивает свои данные
    private boolean authenticationUser(Long id){
        logger.info("@UserRestController I need user with id={}", id);
        //это интерфейс в Spring Security, который представляет результат аутентификации пользователя.
        // Он содержит информацию о пользователе и его аутентификации и предоставляет методы для работы с этой информацией.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        //getPrincipal() - Метод, который возвращает объект, представляющий аутентифицированного пользователя. Обычно это объект,
        // реализующий интерфейс UserDetails. Однако, в зависимости от конфигурации, это может быть, например, имя пользователя или другой объект.
        Long currentUserId = ((User)(authentication.getPrincipal())).getId();
        logger.info("@UserRestController I am currentUserId={}", currentUserId);
        return authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"))
                || currentUserId.equals(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
       // logger.info("UserRestController @GetMapping id={}", id);
        if (authenticationUser(id)) {
                User user = userService.getUserById(id);
                return user != null ?ResponseEntity.ok(user): ResponseEntity.notFound().build();} // Возврат 404 если пользователь не найден
            else return ResponseEntity.status(403).build();// Доступ запрещен

    }

    // редактирование user
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User userRequest) {
        logger.info("UserRestController @PuttMapping userRequest={}", userRequest);
        User existingUser = userService.getUserById(id);
        if (authenticationUser(id)) {
            if (existingUser == null) {
                return ResponseEntity.notFound().build(); // Возврат 404, если пользователь не найден
            }
            // Устанавливаем только разрешенные поля
            existingUser.setFirstName(userRequest.getFirstName());
            existingUser.setLastName(userRequest.getLastName());
            existingUser.setBirthDay(userRequest.getBirthDay());
            existingUser.setHomeAdress(userRequest.getHomeAdress());
            existingUser.setPassword(userRequest.getPassword());

            // Сохраняем обновленного пользователя
            userService.saveUser(existingUser);
        } else return ResponseEntity.status(403).build();// Доступ запрещен
        return ResponseEntity.ok(existingUser);
    }
}

