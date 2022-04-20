package account.security;

import account.business.Entity.LogEntry;
import account.business.Entity.User;
import account.business.service.LoggingService;
import account.business.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;


import javax.servlet.http.HttpServletRequest;


@Component
public class CustomLoginFailureListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {
    @Autowired
    private UserService userService;
    @Autowired
    private LoggingService loggingService;
    @Autowired
    private HttpServletRequest request;

    @Override
    public void onApplicationEvent(AuthenticationFailureBadCredentialsEvent event) {
        try {
            String email = event.getAuthentication().getName();
            loggingService.saveEntry(new LogEntry(
                    "LOGIN_FAILED",
                    email.toLowerCase(),
                    request.getRequestURI(),
                    request.getRequestURI()

            ));
            User user = userService.getUser(email);
            if (user != null && !user.hasGroup("ROLE_ADMINISTRATOR")) {
                if (!user.isLocked()) {
                    if (user.getFailedAttempt() < UserService.MAX_FAILED_ATTEMPTS - 1) {
                        userService.increaseFailedAttempts(user);

                    } else {
                        loggingService.saveEntry(new LogEntry(
                                "BRUTE_FORCE",
                                email.toLowerCase(),
                                request.getRequestURI(),
                                request.getRequestURI()

                        ));
                        userService.lockUser(email);
                        loggingService.saveEntry(new LogEntry(
                                "LOCK_USER",
                                email.toLowerCase(),
                                String.format("Lock user %s", email.toLowerCase()),
                                request.getRequestURI()
                        ));
                    }
                }
            }
        } catch (Exception e) {

        }

    }
}
