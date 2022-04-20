package account.security;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LockUnlockRequest {
    @NotBlank
    private String user;
    @Pattern(regexp = "LOCK|UNLOCK")
    private String operation;
}
