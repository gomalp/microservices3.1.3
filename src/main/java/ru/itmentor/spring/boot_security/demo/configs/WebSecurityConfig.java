package ru.itmentor.spring.boot_security.demo.configs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.access.AccessDeniedHandler;
import ru.itmentor.spring.boot_security.demo.service.UserService;

@Configuration // конфигурационный бин
@EnableWebSecurity //включает поддержку web security и обеспечивает интеграцию со Spring MVC
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private  SuccessUserHandler successUserHandler;
    private  CustomAccessDeniedHandler accessDeniedHandler;

    @Autowired
    public WebSecurityConfig(SuccessUserHandler successUserHandler,
                             UserService userService,
                             PasswordEncoder passwordEncoder, CustomAccessDeniedHandler accessDeniedHandler) {
        this.successUserHandler = successUserHandler;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Override
    //настраивает механизм аутентификации, используя ваш userDetailsService (например, userService) и указывая, какой PasswordEncoder
    // следует применять. тут указывается Spring Security, где и как искать данные о пользователях (userService)
    // и как шифровать/проверять пароли (passwordEncoder).
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder);
    }

    @Override
    //Этот метод настраивает правила безопасности для HTTP-запросов в  приложении посредством Spring Security.
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // Отключаем CSRF для простоты
                .authorizeRequests()
                .antMatchers("/", "/index").permitAll() // Публичные эндпоинты
                .antMatchers("/rest/user/**").hasAnyRole("USER", "ADMIN")
                .antMatchers("/rest/admin/**","/auth/**").hasRole("ADMIN")
                .antMatchers("/admin/**").hasRole("ADMIN") // Доступ только для ROLE_ADMIN
                .antMatchers("/user/**").hasAnyRole( "ADMIN","USER") // Доступ для USER и ADMIN
                .anyRequest().authenticated()
                .and()
                ////
                .formLogin()//Включает поддержку стандартной формы аутентификации Spring Security.
                    .successHandler(successUserHandler) //successHandler: Устанавливает кастомный обработчик успешной аутентификации (SuccessUserHandler),
                    .permitAll() // Разрешает доступ к странице логина всем пользователям
                // который определяет поведение после успешного входа (например, перенаправление пользователя).
                //permitAll(): Разрешает доступ к странице логина всем пользователям.
                .and()
                .logout().permitAll()
                .and()
                .httpBasic() // Используем HTTP Basic Authentication
                .and()
                .exceptionHandling()
                .accessDeniedHandler(accessDeniedHandler); // Используем кастомный Access Denied Handler
                ////

    }

    @Bean
    @Override
    //AuthenticationManager «отвечает» только за механизм проверки учетных данных
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}