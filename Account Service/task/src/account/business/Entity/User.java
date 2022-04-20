package account.business.Entity;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotBlank
    private String name;

    @NotBlank
    private String lastname;

    @NotBlank
    @Email(regexp = "\\w+@acme\\.com")
    private String email;

    @NotBlank
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 12, message = "The password length must be at least 12 chars!")
    private String password;

    @ManyToMany(fetch = FetchType.EAGER, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "user_groups",
            joinColumns =@JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id"))
    @EqualsAndHashCode.Exclude
    private Set<Group> userGroups = new HashSet<>();

    public void addGroup(Group group){
        userGroups.add(group);
        group.getUsers().add(this);
    }

    public void removeGroup(Group group){
        userGroups.remove(group);
        group.getUsers().remove(this);
    }

    public boolean hasGroup(String groupName){
       return userGroups.stream()
               .anyMatch(group -> group.getName().equals(groupName));
    }

    @EqualsAndHashCode.Exclude
    @Column(name = "account_is_locked", columnDefinition = "boolean default false")
    private boolean isLocked = false;

    @EqualsAndHashCode.Exclude
    @Column(name = "failed_attempt", columnDefinition = "integer default 0")
    private int failedAttempt = 0;

    public User(String name, String lastname, String email, String password) {
        this.name = name;
        this.lastname = lastname;
        this.email = email;
        this.password = password;


    }
}
