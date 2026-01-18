package api.chatterbox.uz.entity;

import api.chatterbox.uz.enums.SmsType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Table(name = "sms_history")
@Entity
@Getter
@Setter
public class SmsHistoryEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "phone")
    private String phone;
    @Column(name = "message", columnDefinition = "text")
    private String message;
    @Column(name = "code")
    private String code;
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "sms_type")
    private SmsType smsType; // initially [REGISTRATION, RESET_PASSWORD, CONFIRM_RESET_PASSWORD,]
    @Column(name = "attempt_count")
    private Integer attemptCount = 0;
}
