package api.chatterbox.uz.dto.post;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SimilarPostListDTO {
    @NotBlank(message = "ExceptId Required")
    private String exceptId;

}
