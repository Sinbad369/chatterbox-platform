package api.chatterbox.uz.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordConfirmDTO {
    @NotBlank(message = "Username required")
    private String username; // phone/email
    @NotBlank(message = "Confirm Code required")
    private String confirmCode; // phone/email
    @NotBlank(message = "Password required")
    private String password; // phone/email
}
