package api.chatterbox.uz.dto.post;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostAdminFilterDTO {
    private String profileQuery; // name, surname
    private String postQuery; // id, title
}
