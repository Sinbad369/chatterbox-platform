package api.chatterbox.uz.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttachCreateDTO {
    @NotBlank(message = "Id required")
    private String id;
}