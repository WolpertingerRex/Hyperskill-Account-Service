package account.presentation;

import account.business.Entity.LogEntry;
import account.business.Entity.Payment;
import account.business.Entity.User;
import account.business.service.LoggingService;
import account.business.service.PaymentService;
import account.business.service.UserService;
import account.security.ChangePasswordRequest;
import account.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private LoggingService loggingService;

    @PostMapping("/api/auth/signup")
    public UserDTO signUp(@Valid @RequestBody User user) {
        User newUser = userService.save(user);
        loggingService.saveEntry(new LogEntry(
                "CREATE_USER",
                "Anonymous",
                user.getEmail().toLowerCase(),
                "/api/auth/signup"));
        return new UserDTO(newUser);
    }

    @PostMapping("api/auth/changepass")
    public ResponseEntity<Map<String, String>> changePassword(@AuthenticationPrincipal UserDetailsImpl user,
                                                              @RequestBody ChangePasswordRequest request) {
        String email = userService.changePassword(user.getUsername(), request.getPassword());
        loggingService.saveEntry(new LogEntry(
                "CHANGE_PASSWORD",
                user.getUsername().toLowerCase(),
                user.getUsername().toLowerCase(),
                "/api/auth/changepass"));

        return new ResponseEntity<>(Map.of("email", email,
                "status", "The password has been updated successfully"), HttpStatus.OK);
    }

    @PostMapping("api/acct/payments")
    @Transactional
    public ResponseEntity<Map<String, String>> addPayments(@RequestBody @NotEmpty List<@Valid Payment> payments) {
        for (Payment payment : payments) {
            if (!userService.isExistingUser(payment.getEmployee()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Employee doesn't exist");
            paymentService.addSalary(payment);
        }
        return new ResponseEntity<>(Map.of("status", "Added successfully!"), HttpStatus.OK);
    }

    @PutMapping("api/acct/payments")
    @Transactional
    public ResponseEntity<Map<String, String>> updatePayment(@RequestBody @Valid Payment payment) {

        paymentService.updatePayment(payment);

        return new ResponseEntity<>(Map.of("status", "Updated successfully!"), HttpStatus.OK);

    }

    @GetMapping("api/empl/payment")
    public ResponseEntity getPayment(@AuthenticationPrincipal UserDetailsImpl user,
                                     @RequestParam(required = false) String period) {

        List<Payment> payments = paymentService.getAllPayments(user.getUsername());

        if (period != null) {
            if (period.matches("(0[1-9]|1[0-2])-[1-2]\\d{3}"))
                payments.removeIf(p -> !p.getPeriod().equals(period));
            else throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Wrong date!");
        }

        if (payments.isEmpty()) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }

        List<GetPaymentsResponse> responses = new ArrayList<>();
        for (Payment payment : payments) {
            GetPaymentsResponse response = new GetPaymentsResponse();
            response.setName(user.getUser().getName());
            response.setLastname(user.getUser().getLastname());
            response.setFormattedPeriod(payment.getPeriod());
            response.setFormattedSalary(payment.getSalary());
            responses.add(response);
        }
        if (responses.size() == 1) return new ResponseEntity<>(responses.get(0), HttpStatus.OK);

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
}
