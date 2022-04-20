package account.security;

import account.business.Entity.User;
import account.business.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;

import org.springframework.stereotype.Component;



@Component
public class CustomLoginSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {
    @Autowired
    private UserService userService;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        String email = event.getAuthentication().getName();
        User user = userService.getUser(email);
        if (user.getFailedAttempt() > 0) {
            userService.resetFailedAttempts(user);
        }
    }


}
