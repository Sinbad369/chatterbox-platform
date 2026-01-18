package api.chatterbox.uz.controller;

import api.chatterbox.uz.dto.AppResponse;
import api.chatterbox.uz.dto.CodeConfirmDTO;
import api.chatterbox.uz.dto.ProfileDTO;
import api.chatterbox.uz.dto.profile.*;
import api.chatterbox.uz.enums.AppLanguage;
import api.chatterbox.uz.service.ProfileService;
import api.chatterbox.uz.util.PageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/profile")
@Tag(name = "ProfileController", description = "API set for working with Profile")
public class ProfileController {
    @Autowired
    private ProfileService profileService;

    @PutMapping("/detail")
    @Operation(summary = "Update profile details",
            description = "Updates general profile information such as name, surname, and other personal details.")
    public ResponseEntity<AppResponse<String>> updateDetail(@Valid @RequestBody ProfileDetailUpdateDTO dto,
                                                            @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {
        AppResponse<String> response = profileService.updateDetail(dto, lang);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/photo")
    @Operation(summary = "Update profile photo",
            description = "Updates the user's profile photo using the provided photo ID.")
    public ResponseEntity<AppResponse<String>> updatePhoto(@Valid @RequestBody ProfilePhotoUpdateDTO dto,
                                                           @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {
        AppResponse<String> response = profileService.updatePhoto(dto.getPhotoId(), lang);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/password")
    @Operation(summary = "Update profile password",
            description = "Changes the user's profile password. Requires the old and new password in the request.")
    public ResponseEntity<AppResponse<String>> updatePassword(@Valid @RequestBody ProfilePasswordUpdateDTO dto,
                                                              @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {
        AppResponse<String> response = profileService.updatePassword(dto, lang);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/username")
    @Operation(summary = "Request username update",
            description = "Starts the username update process by submitting a new username. A confirmation code will be sent to verify the change.")
    public ResponseEntity<AppResponse<String>> updateUsername(@Valid @RequestBody ProfileUsernameUpdateDTO dto,
                                                              @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {
        AppResponse<String> response = profileService.updateUsername(dto, lang);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/username/confirm")
    @Operation(summary = "Confirm username update",
            description = "Confirms the username change by submitting the verification code sent to the user.")
    public ResponseEntity<AppResponse<String>> updateUsernameConfirm(@Valid @RequestBody CodeConfirmDTO dto,
                                                                     @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {
        AppResponse<String> response = profileService.updateUsernameConfirm(dto, lang);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/filter")
    @Operation(summary = "Profile filter",
            description = "Api used for filtering profile list")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<PageImpl<ProfileDTO>> filter(@RequestBody ProfileFilterDTO dto,
                                                       @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang,
                                                       @RequestParam(value = "page", defaultValue = "1") int page,
                                                       @RequestParam(value = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(profileService.filter(dto, PageUtil.page(page), size, lang));
    }

    @PutMapping("/status/{id}")
    @Operation(summary = "Change profile status",
            description = "Api used for changing profile status")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AppResponse<String>> status(@PathVariable("id") Integer id,
                                                      @RequestBody ProfileStatusDTO dto,
                                                      @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {
        return ResponseEntity.ok(profileService.changeStatus(id, dto.getStatus(), lang));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete profile",
            description = "Api used for deleting profile")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AppResponse<String>> delete(@PathVariable("id") Integer id,
                                                      @RequestHeader(value = "Accept-Language", defaultValue = "UZ") AppLanguage lang) {
        return ResponseEntity.ok(profileService.delete(id, lang));
    }
}

