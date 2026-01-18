package api.chatterbox.uz.dto.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileDetailUpdateDTO {
    @NotBlank(message = "Name Required")
    private String name;
}

