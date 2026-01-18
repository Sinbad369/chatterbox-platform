package api.chatterbox.uz.controller;

import api.chatterbox.uz.dto.AppResponse;
import api.chatterbox.uz.dto.ProfileDTO;
import api.chatterbox.uz.dto.auth.AuthDTO;
import api.chatterbox.uz.dto.auth.RegistrationDTO;
import api.chatterbox.uz.dto.auth.ResetPasswordConfirmDTO;
import api.chatterbox.uz.dto.auth.ResetPasswordDTO;
import api.chatterbox.uz.dto.sms.SmsResendDTO;
import api.chatterbox.uz.dto.sms.SmsVerificationDTO;
import api.chatterbox.uz.enums.AppLanguage;
import api.chatterbox.uz.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "AuthController", description = "Controller for Authorization and authentication")
@Slf4j
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/registration")
    @Operation(summary = "Profile registration", description = "Api used for registration")
    public ResponseEntity<AppResponse<String>> registration(@Valid @RequestBody RegistrationDTO dto,
                                                            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {
        log.info("Registration: name: {}, username: {}", dto.getName(), dto.getUsername());
        return ResponseEntity.ok().body(authService.registration(dto, lang));
    }

    // http:localhost:8081/auth/registration/email-verification/{token}?lang=UZ
    @GetMapping("/registration/email-verification/{token}")
    @Operation(summary = "Email verification", description = "Api used for registration verification using Email")
    public ResponseEntity<String> emailVerifcation(@PathVariable("token") String token,
                                                   @RequestParam("lang") AppLanguage lang) {
        log.info("Registration email verificaiton: token: {}", token);
        return ResponseEntity.ok().body(authService.registrationEmailVerifcation(token, lang));
    }

    @PostMapping("/registration/sms-verification")
    @Operation(summary = "SMS verification", description = "Api used for registration verification using SMS")
    public ResponseEntity<ProfileDTO> smsVerification(@Valid @RequestBody SmsVerificationDTO dto,
                                                      @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {
        log.info("Sms verification: {}", dto);
        return ResponseEntity.ok().body(authService.registrationSmsVerifcation(dto, lang));
    }

    @PostMapping("/registration/sms-verification-resend")
    @Operation(summary = "SMS verification resend", description = "Api used for resend SMS verification code")
    public ResponseEntity<AppResponse<String>> smsVerificationResend(@Valid @RequestBody SmsResendDTO dto,
                                                                     @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {
        log.info("Sms verification resend: {}", dto.getPhone());
        return ResponseEntity.ok().body(authService.registrationSmsVerifcationResend(dto, lang));
    }

    @PostMapping("/login")
    @Operation(summary = "Login(Auth) api", description = "Api used for log-in to system")
    public ResponseEntity<ProfileDTO> login(@Valid @RequestBody AuthDTO dto,
                                            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {
        log.info("Login: {}", dto.getUsername());
        return ResponseEntity.ok().body(authService.login(dto, lang));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset Password", description = "Api used for password reset")
    public ResponseEntity<AppResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordDTO dto,
                                                             @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {
        log.info("Reset Password: {}", dto.getUsername());
        return ResponseEntity.ok().body(authService.resetPassword(dto, lang));
    }

    @PostMapping("/reset-password-confirm")
    @Operation(summary = "Reset Password Confirm", description = "Api used for password reset confirm")
    public ResponseEntity<AppResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordConfirmDTO dto,
                                                             @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {
        log.info("Reset Password Confirm: {}", dto.getUsername());
        return ResponseEntity.ok().body(authService.resetPasswordConfirm(dto, lang));
    }


}
