package api.chatterbox.uz.service;

import api.chatterbox.uz.dto.sms.SmsAuthDTO;
import api.chatterbox.uz.dto.sms.SmsAuthResponseDTO;
import api.chatterbox.uz.dto.sms.SmsRequestDTO;
import api.chatterbox.uz.dto.sms.SmsSendResponseDTO;
import api.chatterbox.uz.entity.SmsProviderTokenHolderEntity;
import api.chatterbox.uz.enums.AppLanguage;
import api.chatterbox.uz.enums.SmsType;
import api.chatterbox.uz.exps.AppBadException;
import api.chatterbox.uz.repository.SmsProviderTokenHolderRepository;
import api.chatterbox.uz.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class SmsSendService {
    @Autowired
    private RestTemplate restTemplate;
    @Value("${eskiz.url}")
    private String smsURL;
    @Value("${eskiz.login}")
    private String accountLogin;
    @Value("${eskiz.password}")
    private String accountPassword;
    @Autowired
    private SmsProviderTokenHolderRepository smsProviderTokenHolderRepository;
    @Autowired
    private SmsHistoryService smsHistoryService;
    private Integer smsLimit = 3;
    @Autowired
    private ResourceBundleService bundleService;

    public void sendRegistrationSms(String phoneNumber, AppLanguage lang) {
        String code = RandomUtil.getRandomSmsCode();
        String message = bundleService.getMessage("sms.registration.confirm.code", lang);
        // "This is test from Eskiz";
        message = String.format(message, code);
        sendSms(phoneNumber, message, code, SmsType.REGISTRATION);
    }

    public void sendResetPasswordSms(String phoneNumber, AppLanguage lang) {
        String code = RandomUtil.getRandomSmsCode();
        String message = bundleService.getMessage("sms.reset.password.confirm", lang);
        // "This is test from Eskiz";
        message = String.format(message, code);
        sendSms(phoneNumber, message, code, SmsType.RESET_PASSWORD);
    }

    public void sendUsernameChangeConfirmSms(String phoneNumber, AppLanguage lang) {
        String code = RandomUtil.getRandomSmsCode();
        String message = bundleService.getMessage("sms.change.username.confirm", lang);
        // "This is test from Eskiz";
        message = String.format(message, code);
        sendSms(phoneNumber, message, code, SmsType.CHANGE_USERNAME_CONFIRM);
    }


    private SmsSendResponseDTO sendSms(String phoneNumber, String message, String code, SmsType smsType) {
        // check
        Long count = smsHistoryService.getSmsCount(phoneNumber);
        if (count >= smsLimit) {
            System.out.println(" --- Sms limit reached. Phone: " + phoneNumber);
            log.warn("Sms limit reached. Phone: {}", phoneNumber);
            throw new AppBadException("Sms limit reached.");
        }
        // send sms
        SmsSendResponseDTO result = sendSms(phoneNumber, message);
        // save
        smsHistoryService.create(phoneNumber, message, code, smsType);
        return result;
    }

    private SmsSendResponseDTO sendSms(String phoneNumber, String message) {
        // getToken
        String token = getToken();
        // header
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + token);
        // body
        SmsRequestDTO body = new SmsRequestDTO();
        body.setMobile_phone(phoneNumber);
        body.setMessage(message);
        body.setFrom("4546");
        // send request
        HttpEntity<SmsRequestDTO> entity = new HttpEntity<>(body, headers);
        try {
            ResponseEntity<SmsSendResponseDTO> response = restTemplate.exchange(
                    smsURL + "/message/sms/send",
                    HttpMethod.POST,
                    entity,
                    SmsSendResponseDTO.class);
            return response.getBody();
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error("Send sms. Phone: {}, message: {}, error {}", phoneNumber, message, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private String getToken() {
        Optional<SmsProviderTokenHolderEntity> optional = smsProviderTokenHolderRepository.findTop1By();
        if (optional.isEmpty()) { // if token not exists
            String token = getTokenFromProvider();
            SmsProviderTokenHolderEntity entity = new SmsProviderTokenHolderEntity();
            entity.setToken(token);
            entity.setCreatedDate(LocalDateTime.now());
            entity.setExpiredDate(LocalDateTime.now().plusMonths(1));
            smsProviderTokenHolderRepository.save(entity);
            return token;
        }
        // if token exists, check it
        SmsProviderTokenHolderEntity entity = optional.get();
        if (LocalDateTime.now().isBefore(entity.getExpiredDate())) { // if not expired
            return entity.getToken();
        }

        // get new token and update it
        String token = getTokenFromProvider();
        entity.setToken(token);
        entity.setCreatedDate(LocalDateTime.now());
        entity.setExpiredDate(LocalDateTime.now().plusMonths(1));
        smsProviderTokenHolderRepository.save(entity);
        return token;
    }

    private String getTokenFromProvider() {
        SmsAuthDTO smsAuthDTO = new SmsAuthDTO();
        smsAuthDTO.setEmail(accountLogin);
        smsAuthDTO.setPassword(accountPassword);
        try {
            System.out.println("---- SmsSender new Token is taken ----");
            SmsAuthResponseDTO response = restTemplate.postForObject(smsURL + "/auth/login", smsAuthDTO, SmsAuthResponseDTO.class);
            return response.getData().getToken();
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.error("Get Token. Account: {}, error {}", accountLogin, e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
