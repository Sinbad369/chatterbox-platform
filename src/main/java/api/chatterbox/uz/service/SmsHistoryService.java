package api.chatterbox.uz.service;

import api.chatterbox.uz.entity.SmsHistoryEntity;
import api.chatterbox.uz.enums.AppLanguage;
import api.chatterbox.uz.enums.SmsType;
import api.chatterbox.uz.exps.AppBadException;
import api.chatterbox.uz.repository.SmsHistoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class SmsHistoryService {
    @Autowired
    private SmsHistoryRepository smsHistoryRepository;
    @Autowired
    private ResourceBundleService bundleService;

    public void create(String phoneNumber, String message, String code, SmsType smsType) {
        SmsHistoryEntity entity = new SmsHistoryEntity();
        entity.setPhone(phoneNumber);
        entity.setMessage(message);
        entity.setCode(code);
        entity.setSmsType(smsType);
        entity.setAttemptCount(0);
        entity.setCreatedDate(LocalDateTime.now());
        smsHistoryRepository.save(entity);
    }

    public Long getSmsCount(String phone) {
        LocalDateTime now = LocalDateTime.now();
        return smsHistoryRepository.countByPhoneAndCreatedDateBetween(phone, now.minusMinutes(1), now);
    }

    public void check(String phoneNumber, String code, AppLanguage lang) {
        // find the most recent sms by phoneNumber
        Optional<SmsHistoryEntity> optional = smsHistoryRepository.findTop1ByPhoneOrderByCreatedDateDesc(phoneNumber);
        if (optional.isEmpty()) {
            throw new AppBadException(bundleService.getMessage("verification.failed", lang));
        }

        SmsHistoryEntity entity = optional.get();
        // attempt count
        if (entity.getAttemptCount() >= 3) {
            log.warn("Attempt count limit reached. Phone: {}", phoneNumber);
            throw new AppBadException(bundleService.getMessage("verification.failed", lang)); // EN UZ RU do it yourself, attempts limit reached, try again later
        }

        // check code
        if (!entity.getCode().equals(code)) {
            smsHistoryRepository.updateAttemptCount(entity.getId()); // update attempt count
            throw new AppBadException(bundleService.getMessage("verification.failed", lang)); // profile not found, fix by adding internationalization stuff
        }
        // check time
        LocalDateTime expDate = entity.getCreatedDate().plusMinutes(2);
        if (LocalDateTime.now().isAfter(expDate)) {// not valid
            log.warn("Sms expired: {}", phoneNumber);
            throw new AppBadException(bundleService.getMessage("verification.failed", lang)); // verification time expired, fix by adding internationalization stuff
        }
    }
}
