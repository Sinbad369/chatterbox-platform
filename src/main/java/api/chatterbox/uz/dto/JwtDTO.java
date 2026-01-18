package api.chatterbox.uz.dto;

import api.chatterbox.uz.enums.ProfileRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class JwtDTO {
    private String username;
    private Integer id;
    private List<ProfileRole> roleList;
}
