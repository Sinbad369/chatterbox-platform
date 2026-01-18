package api.chatterbox.uz.service;

import api.chatterbox.uz.entity.EmailHistoryEntity;
import api.chatterbox.uz.enums.AppLanguage;
import api.chatterbox.uz.enums.SmsType;
import api.chatterbox.uz.exps.AppBadException;
import api.chatterbox.uz.repository.EmailHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class EmailHistoryService {
    @Autowired
    private EmailHistoryRepository emailHistoryRepository;
    @Autowired
    private ResourceBundleService bundleService;

    public void create(String email, String code, SmsType emailType) {
        EmailHistoryEntity entity = new EmailHistoryEntity();
        entity.setEmail(email);
        entity.setCode(code);
        entity.setEmailType(emailType);
        entity.setAttemptCount(0);
        entity.setCreatedDate(LocalDateTime.now());
        emailHistoryRepository.save(entity);
    }

    public Long getEmailCount(String email) {
        LocalDateTime now = LocalDateTime.now();
        return emailHistoryRepository.countByEmailAndCreatedDateBetween(email, now.minusMinutes(1), now);
    }

    public void check(String email, String code, AppLanguage lang) {
        // find the most recent sms by phoneNumber
        Optional<EmailHistoryEntity> optional = emailHistoryRepository.findTop1ByEmailOrderByCreatedDateDesc(email);
        if (optional.isEmpty()) {
            log.warn("No email history found for email: {}", email);
            throw new AppBadException(bundleService.getMessage("verification.failed", lang));
        }

        EmailHistoryEntity entity = optional.get();
        // attempt count
        if (entity.getAttemptCount() >= 3) {
            log.warn("Attempt count reached: {}", email);
            throw new AppBadException(bundleService.getMessage("verification.failed", lang)); // EN UZ RU do it yourself, attempts limit reached, try again later
        }

        // check code
        if (!entity.getCode().equals(code)) {
            emailHistoryRepository.updateAttemptCount(entity.getId()); // update attempt count
            log.warn("Wrong code: {}", email);
            throw new AppBadException(bundleService.getMessage("verification.failed", lang)); // profile not found, fix by adding internationalization stuff
        }
        // check time
        LocalDateTime expDate = entity.getCreatedDate().plusMinutes(2);
        if (LocalDateTime.now().isAfter(expDate)) {// not valid
            throw new AppBadException(bundleService.getMessage("verification.failed", lang)); // verification time expired, fix by adding internationalization stuff
        }
    }
}
