package api.chatterbox.uz.dto.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileUsernameUpdateDTO {
    @NotBlank(message = "Username Required")
    private String username;
}

