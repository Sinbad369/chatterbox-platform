package api.chatterbox.uz.service;

import api.chatterbox.uz.dto.AppResponse;
import api.chatterbox.uz.dto.ProfileDTO;
import api.chatterbox.uz.dto.auth.AuthDTO;
import api.chatterbox.uz.dto.auth.RegistrationDTO;
import api.chatterbox.uz.dto.auth.ResetPasswordConfirmDTO;
import api.chatterbox.uz.dto.auth.ResetPasswordDTO;
import api.chatterbox.uz.dto.sms.SmsResendDTO;
import api.chatterbox.uz.dto.sms.SmsVerificationDTO;
import api.chatterbox.uz.entity.ProfileEntity;
import api.chatterbox.uz.enums.AppLanguage;
import api.chatterbox.uz.enums.GeneralStatus;
import api.chatterbox.uz.enums.ProfileRole;
import api.chatterbox.uz.exps.AppBadException;
import api.chatterbox.uz.repository.ProfileRepository;
import api.chatterbox.uz.repository.ProfileRoleRepository;
import api.chatterbox.uz.util.EmailUtil;
import api.chatterbox.uz.util.JwtUtil;
import api.chatterbox.uz.util.PhoneUtil;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Slf4j
public class AuthService {
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private ProfileRoleRepository profileRoleRepository;
    @Autowired
    private ProfileRoleService profileRoleService;
    @Autowired
    private EmailSendingService emailSendingService;
    @Autowired
    private ProfileService profileService;
    @Autowired
    private ResourceBundleService bundleService;
    @Autowired
    private SmsSendService smsSendService;
    @Autowired
    private SmsHistoryService smsHistoryService;
    @Autowired
    private EmailHistoryService emailHistoryService;
    @Autowired
    private AttachService attachService;

    public AppResponse<String> registration(RegistrationDTO dto, AppLanguage lang) {
        // 1. Validation
        // 2. lattabekov@gmail.com
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());
        if (optional.isPresent()) {
            ProfileEntity profile = optional.get();
            if (profile.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
                profileRoleService.deleteRoles(profile.getId());
                profileRepository.delete(profile); // deletes the profile in registration, so can be created again (Option 1)
                // send sms/email
            } else {
                log.warn("Profile already exists with name {}", dto.getUsername());
                throw new AppBadException(bundleService.getMessage("email.phone.exists", lang));
            }

        }
        ProfileEntity entity = new ProfileEntity();
        entity.setName(dto.getName());
        entity.setUsername(dto.getUsername());
        entity.setPassword(bCryptPasswordEncoder.encode(dto.getPassword()));
        entity.setStatus(GeneralStatus.IN_REGISTRATION);
        entity.setVisible(true);
        entity.setCreatedDate(LocalDateTime.now());
        //entity.setLanguage(lang);
        //Amaliyot
        profileRepository.save(entity); // save
        // Insert Roles
        profileRoleService.create(entity.getId(), ProfileRole.ROLE_USER);
        // send
        if (PhoneUtil.isPhone(dto.getUsername())) {
            smsSendService.sendRegistrationSms(dto.getUsername(), lang);
        } else if (EmailUtil.isEmail(dto.getUsername())) {
            emailSendingService.sendRegistrationEmail(dto.getUsername(), entity.getId(), lang);
        }
        return new AppResponse<>(bundleService.getMessage("email.confirm.send", lang));
    }

    public String registrationEmailVerifcation(String token, AppLanguage lang) {
        try {
            Integer profileId = JwtUtil.decodeRegVerToken(token);
            ProfileEntity profile = profileService.getById(profileId);
            if (profile.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
                profileRepository.changeStatus(profile.getId(), GeneralStatus.ACTIVE);
                return bundleService.getMessage("verification.finished", lang);
            }
        } catch (JwtException e) {
        }
        log.warn("Registration Email verification failed{}", token);
        throw new AppBadException(bundleService.getMessage("verification.failed", lang));
    }

    public ProfileDTO login(AuthDTO dto, AppLanguage lang) {
        // dto
        // check
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());
        if (optional.isEmpty()) {
            log.warn("Username or password wrong: {}", dto.getUsername());
            throw new AppBadException(bundleService.getMessage("username.password.wrong", lang));
        }
        ProfileEntity profile = optional.get();
        if (!bCryptPasswordEncoder.matches(dto.getPassword(), optional.get().getPassword())) {
            log.warn("Username or password wrong: {}", dto.getPassword());
            throw new AppBadException(bundleService.getMessage("username.password.wrong", lang));
        }
        if (!profile.getStatus().equals(GeneralStatus.ACTIVE)) {
            log.warn("Wrong status: {}", dto.getUsername());
            throw new AppBadException(bundleService.getMessage("wrong.status", lang));
        }
        // response
        return getLogInResponse(profile);
    }

    public ProfileDTO registrationSmsVerifcation(SmsVerificationDTO dto, AppLanguage lang) {
        // 998915721213
        // 12345
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getPhone());
        if (optional.isEmpty()) {
            log.warn("Verification failed: {}", dto.getPhone());
            throw new AppBadException(bundleService.getMessage("verification.failed", lang));
        }
        ProfileEntity profile = optional.get();
        if (!profile.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
            log.warn("Verification failed: {}", dto.getPhone());
            throw new AppBadException(bundleService.getMessage("verification.failed", lang)); //profile not found, fix by adding internationalization stuff
        }
        // code check
        smsHistoryService.check(dto.getPhone(), dto.getCode(), lang);
//        if (!smsHistoryService.check(dto.getPhone(), dto.getCode())) {
//            throw new AppBadException(bundleService.getMessage("verification.failed", lang)); // verification time expired, fix by adding internationalization stuff
//        }
        // ACTIVE
        profileRepository.changeStatus(profile.getId(), GeneralStatus.ACTIVE);
        // response
        return getLogInResponse(profile);
    }

    public AppResponse<String> registrationSmsVerifcationResend(@Valid SmsResendDTO dto, AppLanguage lang) {
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getPhone());
        if (optional.isEmpty()) {
            log.warn("Verification failed: {}", dto.getPhone());
            throw new AppBadException(bundleService.getMessage("verification.failed", lang));
        }
        ProfileEntity profile = optional.get();
        if (!profile.getStatus().equals(GeneralStatus.IN_REGISTRATION)) {
            log.warn("Verification failed: {}", dto.getPhone());
            throw new AppBadException(bundleService.getMessage("verification.failed", lang)); //profile not found, fix by adding internationalization stuff
        }
        // resend sms
        smsSendService.sendRegistrationSms(dto.getPhone(), lang);
        return new AppResponse<>(bundleService.getMessage("sms.resend", lang));
    }

    public AppResponse<String> resetPassword(@Valid ResetPasswordDTO dto, AppLanguage lang) {
        // check
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());
        if (optional.isEmpty()) {
            log.warn("Profile not found: {}", dto.getUsername());
            throw new AppBadException(bundleService.getMessage("profile.not.found", lang));
        }
        ProfileEntity profile = optional.get();
        if (!profile.getStatus().equals(GeneralStatus.ACTIVE)) {
            log.warn("Wrong status: {}", dto.getUsername());
            throw new AppBadException(bundleService.getMessage("wrong.status", lang));
        }
        // send
        if (PhoneUtil.isPhone(dto.getUsername())) {
            smsSendService.sendResetPasswordSms(dto.getUsername(), lang);
        } else if (EmailUtil.isEmail(dto.getUsername())) {
            emailSendingService.sendResetPasswordEmail(dto.getUsername(), lang);
        }
        String responseMessage = bundleService.getMessage("reset.password.response", lang);
        return new AppResponse<>(String.format(responseMessage, dto.getUsername()));
    }

    public AppResponse<String> resetPasswordConfirm(@Valid ResetPasswordConfirmDTO dto, AppLanguage lang) {
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());
        if (optional.isEmpty()) {
            log.warn("Verification failed: {}", dto.getUsername());
            throw new AppBadException(bundleService.getMessage("verification.failed", lang));
        }

        ProfileEntity profile = optional.get();
        if (!profile.getStatus().equals(GeneralStatus.ACTIVE)) {
            log.warn("Wrong status: {}", dto.getUsername());
            throw new AppBadException(bundleService.getMessage("wrong.status", lang)); //profile not found, fix by adding internationalization stuff
        }
        // check
        if (PhoneUtil.isPhone(dto.getUsername())) {
            smsHistoryService.check(dto.getUsername(), dto.getConfirmCode(), lang);
        } else if (EmailUtil.isEmail(dto.getUsername())) {
            emailHistoryService.check(dto.getUsername(), dto.getConfirmCode(), lang);
        }
        // update
        profileRepository.updatePassword(profile.getId(), bCryptPasswordEncoder.encode(dto.getPassword()));
        // return
        return new AppResponse<>(bundleService.getMessage("reset.password.success", lang));
    }

    public ProfileDTO getLogInResponse(ProfileEntity profile) {
        ProfileDTO response = new ProfileDTO();
        response.setName(profile.getName());
        response.setUsername(profile.getUsername());
        response.setRoleList(profileRoleRepository.getAllRolesListByProfileId(profile.getId()));
        response.setJwt(JwtUtil.encode(profile.getUsername(), profile.getId(), response.getRoleList())); // jwt
        // the below code is changed form, but working form
        // Photo (null-safe)
        if (profile.getPhoto() != null) {
            // If profile.getPhoto() is an AttachEntity, use its ID
            String photoId = profile.getPhoto().getId();
            response.setPhoto(attachService.attachDTO(photoId));
        } else {
            response.setPhoto(null);
        }

        return response;
    }


}
