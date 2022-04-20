package account.security;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeUserRoleRequest {
    @NotBlank
    private String user;
    @NotBlank
    private String role;
    @Pattern(regexp = "GRANT|REMOVE")
    private String operation;
}
