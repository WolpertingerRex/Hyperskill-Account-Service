package account.security;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePasswordRequest {

   @NotBlank
   @Size(min = 12, message = "Password length must be 12 chars minimum!")
   @JsonProperty("new_password")
   private String password;

}
