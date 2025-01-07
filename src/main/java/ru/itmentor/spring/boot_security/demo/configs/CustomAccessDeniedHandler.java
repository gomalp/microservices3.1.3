package ru.itmentor.spring.boot_security.demo.configs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import ru.itmentor.spring.boot_security.demo.controller.AdminController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private static final Logger logger = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);
    private static final String ACCESS_DENIED_URL = "/access-denied";

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {


        if (isRestRequest(request)) {
            logger.info(" *** CustomAccessDeniedHandler JSON response");
            response.setContentType("application/json");
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.getWriter().write("{\"error\": \"Access is denied.\"}");
        } else {
            logger.info(" *** CustomAccessDeniedHandler HTML response");
            response.sendRedirect(request.getContextPath() + ACCESS_DENIED_URL);
        }
        //System.out.println(" ******* Доступ запрещен: " + accessDeniedException.getMessage());
       // response.sendRedirect(request.getContextPath() + ACCESS_DENIED_URL);
    }
    private boolean isRestRequest(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/rest/");
    }
}