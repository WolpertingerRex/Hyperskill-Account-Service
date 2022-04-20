package account.business.service;

import account.business.Entity.Group;
import account.business.Entity.User;
import account.security.UserDetailsImpl;
import account.persistance.GroupRepository;
import account.persistance.UserRepository;
import account.security.UserExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private PasswordEncoder encoder;

    public static final int MAX_FAILED_ATTEMPTS = 5;

    private final Set<String> breachedPasswords = Set.of("PasswordForJanuary", "PasswordForFebruary", "PasswordForMarch", "PasswordForApril",
            "PasswordForMay", "PasswordForJune", "PasswordForJuly", "PasswordForAugust",
            "PasswordForSeptember", "PasswordForOctober", "PasswordForNovember", "PasswordForDecember");


    public User save(User user) {
        if (breachedPasswords.contains(user.getPassword()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password is in the hacker's database!");

        if (isExistingUser(user.getEmail())) throw new UserExistException();
        Group group;
        if (userRepository.count() == 0) {
            group = groupRepository.findByName("ROLE_ADMINISTRATOR")
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found!"));

        } else {
            group = groupRepository.findByName("ROLE_USER")
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found!"));

        }
        user.addGroup(group);
        user.setPassword(encoder.encode(user.getPassword()));
        user.setFailedAttempt(0);
        user.setLocked(false);
        return userRepository.save(user);
    }

    public boolean isExistingUser(String email) {
        Optional<User> user = userRepository.findUserByEmailIgnoreCase(email);
        return user.isPresent();
    }

    public User getUser(String email) {
        Optional<User> user = userRepository.findUserByEmailIgnoreCase(email);

        if (user.isPresent()) {
            return user.get();
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found!");
        }
    }

    public String changePassword(String email, String newPassword) {
        if (newPassword.length() < 12)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password length must be 12 chars minimum!");

        if (breachedPasswords.contains(newPassword))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The password is in the hacker's database!");
        User user = getUser(email);
        String oldPassword = user.getPassword();
        if (encoder.matches(newPassword, oldPassword))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The passwords must be different!");

        user.setPassword(encoder.encode(newPassword));
        userRepository.save(user);
        return email.toLowerCase();
    }

    public List<User> getAllUsers() {
        return
                StreamSupport.stream(userRepository.findAll().spliterator(), false)
                        .collect(Collectors.toList());
    }

    public void deleteUser(UserDetailsImpl user, String email) {
        User toDelete = getUser(email);
        if (user.getUsername().equalsIgnoreCase(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");
        }
        userRepository.delete(toDelete);

    }

    public User grantRole(String email, String role) {
        User user = getUser(email);
        Group newGroup = groupRepository.findByName("ROLE_" + role)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!"));

        Set<Group> userGroups = user.getUserGroups();

        Optional<Group> adm = userGroups.stream().filter(Group::isAdministrative).findFirst();
        Optional<Group> bis = userGroups.stream().filter(Group::isBusiness).findFirst();
        if (newGroup.isAdministrative() && bis.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user cannot combine administrative and business roles!");
        else if (newGroup.isBusiness() && adm.isPresent())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user cannot combine administrative and business roles!");

        user.addGroup(newGroup);
        return userRepository.save(user);
    }

    public User removeRole(String email, String role) {
        User user = getUser(email);
        Group groupForDelete = groupRepository.findByName("ROLE_" + role)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Role not found!"));

        Set<Group> userGroups = user.getUserGroups();
        if (!userGroups.contains(groupForDelete))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user does not have a role!");

        if (groupForDelete.getName().equals("ROLE_ADMINISTRATOR"))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!");

        if (userGroups.size() < 2)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The user must have at least one role!");

        userGroups.remove(groupForDelete);
        return userRepository.save(user);
    }

    public User lockUser(String email) {
        User user = getUser(email);
        if (user.hasGroup("ROLE_ADMINISTRATOR")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't lock the ADMINISTRATOR!");
        }
        user.setLocked(true);
        return userRepository.save(user);
    }

    public User unlockUser(String email) {
        User user = getUser(email);
        user.setLocked(false);
        user.setFailedAttempt(0);
        return userRepository.save(user);
    }


    public void increaseFailedAttempts(User user) {
        int newFailAttempts = user.getFailedAttempt() + 1;
        user.setFailedAttempt(newFailAttempts);
        userRepository.save(user);
    }

    public void resetFailedAttempts(User user) {
        user.setFailedAttempt(0);
        userRepository.save(user);
    }
}
