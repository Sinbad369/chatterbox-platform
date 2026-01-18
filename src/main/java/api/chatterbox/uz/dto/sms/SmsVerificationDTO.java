package api.chatterbox.uz.dto.sms;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SmsVerificationDTO {
    @NotBlank(message = "Phone required")
    private String phone;
    @NotBlank(message = "Code required")
    private String code;

    @Override
    public String toString() {
        return "SmsVerificationDTO{" +
                "code='" + code + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
