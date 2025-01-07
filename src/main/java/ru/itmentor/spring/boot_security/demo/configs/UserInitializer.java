package ru.itmentor.spring.boot_security.demo.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.itmentor.spring.boot_security.demo.controller.AdminController;
import ru.itmentor.spring.boot_security.demo.model.Role;
import ru.itmentor.spring.boot_security.demo.model.User;
import ru.itmentor.spring.boot_security.demo.repository.RoleRepository;
import ru.itmentor.spring.boot_security.demo.repository.UserRepository;
import org.springframework.stereotype.Component;
import ru.itmentor.spring.boot_security.demo.service.RoleService;
import ru.itmentor.spring.boot_security.demo.service.UserService;


import java.sql.Date;

@Component
public class UserInitializer implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(UserInitializer.class);
    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public UserInitializer(UserService userService,
                           RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @Override
    public void run(ApplicationArguments args) {
        logger.info("++++++++++++++++++++++++++++++++++ UserInitializer.run()");

        Role adminRole=new Role("ADMIN");
        Role userRole=new Role("USER");
        if(!roleService.existsByRole("ADMIN")) roleService.saveRole(adminRole);
        if(!roleService.existsByRole("USER")) roleService.saveRole(new Role("USER"));


        // 2) Если пользователей ещё нет, создаём:
        if (userService.count() == 0) {
            logger.info("Инициализация таблицы users начальными данными...");

            // Пример пользователя admin c ролью ADMIN:
            User adminUser = new User(
                    "admin",
                    "admin",       // пароль зашифруется в saveUser
                    "Serg",
                    "Doe",
                    Date.valueOf("1990-01-15"),
                    "123 Main St"
            );
            adminUser.addRole(adminRole);
            userService.saveUser(adminUser);

            // Пример пользователя с ролью USER:
            User sssUser = new User(
                    "sss",
                    "222",
                    "Jane",
                    "Smith",
                    Date.valueOf("1985-05-22"),
                    "456 Oak Ave"
            );
            sssUser.addRole(userRole);
            userService.saveUser(sssUser);

            // И т.п.
            User aaaUser = new User(
                    "aaa",
                    "333",
                    "Alice",
                    "Johnson",
                    Date.valueOf("1992-12-02"),
                    "789 Pine Rd"
            );
            aaaUser.addRole(userRole);
            userService.saveUser(aaaUser);

            logger.info("Начальные данные добавлены.");
        } else {
            logger.info("Данные уже существуют. Пропускаем инициализацию.");
        }
    }
}