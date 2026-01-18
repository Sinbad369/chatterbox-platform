package api.chatterbox.uz.dto.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfilePasswordUpdateDTO {
    @NotBlank(message = "CurrentPswd Required")
    private String currentPswd;
    @NotBlank(message = "NewPswd Required")
    private String newPswd;
}
