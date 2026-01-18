package api.chatterbox.uz.dto.profile;

import api.chatterbox.uz.enums.GeneralStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileStatusDTO {
    private GeneralStatus status;
}
