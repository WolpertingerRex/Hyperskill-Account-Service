package account.presentation;

import account.business.Entity.LogEntry;
import account.business.Entity.User;
import account.security.UserDetailsImpl;
import account.business.service.LoggingService;
import account.business.service.UserService;
import account.security.ChangeUserRoleRequest;
import account.security.LockUnlockRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/admin")
public class AdminController {
    @Autowired
    private UserService userService;
    @Autowired
    private LoggingService loggingService;

    @PutMapping("/user/role")
    public ResponseEntity setRole(@RequestBody(required = false) ChangeUserRoleRequest request,
                                  @AuthenticationPrincipal UserDetailsImpl admin) {
        String operation = request.getOperation();
        User user;
        switch (operation) {
            case "GRANT":
                user = userService.grantRole(request.getUser(), request.getRole());
                loggingService.saveEntry(new LogEntry(
                        "GRANT_ROLE",
                        admin.getUsername().toLowerCase(),
                        String.format("Grant role %s to %s", request.getRole(), request.getUser().toLowerCase()),
                        "api/admin/user/role"

                ));
                break;
            case "REMOVE":
                user = userService.removeRole(request.getUser(), request.getRole());
                loggingService.saveEntry(new LogEntry(
                        "REMOVE_ROLE",
                        admin.getUsername().toLowerCase(),
                        String.format("Remove role %s from %s", request.getRole(), request.getUser().toLowerCase()),
                        "api/admin/user/role"

                ));
                break;
            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operation must be only GRANT or REMOVE!");
        }
        return new ResponseEntity<>(new UserDTO(user), HttpStatus.OK);
    }

    @GetMapping("/user")
    public ResponseEntity getUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()) return new ResponseEntity<>(Collections.EMPTY_LIST, HttpStatus.OK);
        List<UserDTO> prepared = users.stream().map(UserDTO::new).collect(Collectors.toList());
        return new ResponseEntity<>(prepared, HttpStatus.OK);
    }

    @DeleteMapping("/user/{username}")
    @Transactional
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable("username") String username,
                                                          @AuthenticationPrincipal UserDetailsImpl admin) {

        userService.deleteUser(admin, username);
        loggingService.saveEntry(new LogEntry(
                "DELETE_USER",
                admin.getUsername().toLowerCase(),
                username.toLowerCase(),
                "api/admin/user"
        ));
        return new ResponseEntity<>(Map.of("user", username, "status", "Deleted successfully!"), HttpStatus.OK);
    }

    @PutMapping("/user/access")
    public ResponseEntity<Map<String, String>> lockUser(@RequestBody @Valid LockUnlockRequest request,
                                                        @AuthenticationPrincipal UserDetailsImpl admin) {
        String operation = request.getOperation();
        User user;
        switch (operation) {
            case "LOCK":

                user = userService.lockUser(request.getUser());
                loggingService.saveEntry(new LogEntry(
                        "LOCK_USER",
                        admin.getUsername().toLowerCase(),
                        String.format("Lock user %s", request.getUser().toLowerCase()),
                        "api/admin/user/access"

                ));
                return new ResponseEntity<>(Map.of("status",
                        String.format("User %s locked!", user.getEmail().toLowerCase())), HttpStatus.OK);


            case "UNLOCK":
                user = userService.unlockUser(request.getUser());
                loggingService.saveEntry(new LogEntry(
                        "UNLOCK_USER",
                        admin.getUsername().toLowerCase(),
                        String.format("Unlock user %s", request.getUser().toLowerCase()),
                        "api/admin/user/access"
                ));

                return new ResponseEntity<>(Map.of("status",
                        String.format("User %s unlocked!", user.getEmail())), HttpStatus.OK);

            default:
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Operation must be only GRANT or REMOVE!");
        }

    }
}
