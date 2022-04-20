package account.business.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "groups")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToMany(mappedBy = "userGroups")
    @EqualsAndHashCode.Exclude
    private Set<User> users;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private boolean isBusiness;
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    private boolean isAdministrative;

    public Group(String name) {
        this.name = name;
        users = new HashSet<>();
        if (name.equals("ROLE_ADMINISTRATOR")) {
            isBusiness = false;
            isAdministrative = true;
        }
        if (name.equals("ROLE_USER") || name.equals ("ROLE_ACCOUNTANT") || name.equals("ROLE_AUDITOR")) {
            isBusiness = true;
            isAdministrative = false;
        }
    }

}
