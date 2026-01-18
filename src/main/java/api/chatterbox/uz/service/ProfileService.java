package api.chatterbox.uz.service;

import api.chatterbox.uz.dto.AppResponse;
import api.chatterbox.uz.dto.CodeConfirmDTO;
import api.chatterbox.uz.dto.ProfileDTO;
import api.chatterbox.uz.dto.profile.ProfileDetailUpdateDTO;
import api.chatterbox.uz.dto.profile.ProfileFilterDTO;
import api.chatterbox.uz.dto.profile.ProfilePasswordUpdateDTO;
import api.chatterbox.uz.dto.profile.ProfileUsernameUpdateDTO;
import api.chatterbox.uz.entity.ProfileEntity;
import api.chatterbox.uz.entity.ProfileRoleEntity;
import api.chatterbox.uz.enums.AppLanguage;
import api.chatterbox.uz.enums.GeneralStatus;
import api.chatterbox.uz.enums.ProfileRole;
import api.chatterbox.uz.exps.AppBadException;
import api.chatterbox.uz.mapper.ProfileDetailMapper;
import api.chatterbox.uz.repository.ProfileRepository;
import api.chatterbox.uz.repository.ProfileRoleRepository;
import api.chatterbox.uz.util.EmailUtil;
import api.chatterbox.uz.util.JwtUtil;
import api.chatterbox.uz.util.PhoneUtil;
import api.chatterbox.uz.util.SpringSecurityUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProfileService {

    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private ResourceBundleService bundleService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private SmsSendService smsSendService;
    @Autowired
    private EmailSendingService emailSendingService;
    @Autowired
    private SmsHistoryService smsHistoryService;
    @Autowired
    private EmailHistoryService emailHistoryService;
    @Autowired
    private ProfileRoleRepository profileRoleRepository;
    @Autowired
    private AttachService attachService;

    public AppResponse<String> updateDetail(ProfileDetailUpdateDTO dto, AppLanguage lang) {
        Integer profileId = SpringSecurityUtil.getCurrentUserId();
        profileRepository.updateDetail(profileId, dto.getName());
        return new AppResponse<>(bundleService.getMessage("profile.detail.update.success", lang));
    }

    public AppResponse<String> updatePassword(@Valid ProfilePasswordUpdateDTO dto, AppLanguage lang) {
        Integer profileId = SpringSecurityUtil.getCurrentUserId();
        ProfileEntity profile = getById(profileId);
        if (!bCryptPasswordEncoder.matches(dto.getCurrentPswd(), profile.getPassword())) {
            log.warn("Wrong password. ProfileId: {}", profileId);
            throw new AppBadException(bundleService.getMessage("wrong.password", lang));
        }
        profileRepository.updatePassword(profileId, bCryptPasswordEncoder.encode(dto.getNewPswd()));
        return new AppResponse<>(bundleService.getMessage("profile.password.update.success", lang));
    }

    public AppResponse<String> updateUsername(@Valid ProfileUsernameUpdateDTO dto, AppLanguage lang) {
        // check
        Optional<ProfileEntity> optional = profileRepository.findByUsernameAndVisibleTrue(dto.getUsername());
        if (optional.isPresent()) {
            log.warn("Email of Phone already exists: {}", dto.getUsername());
            throw new AppBadException(bundleService.getMessage("email.phone.exists", lang));
        }

        // send
        if (PhoneUtil.isPhone(dto.getUsername())) {
            smsSendService.sendUsernameChangeConfirmSms(dto.getUsername(), lang);
        } else if (EmailUtil.isEmail(dto.getUsername())) {
            emailSendingService.sendChangeUsernameEmail(dto.getUsername(), lang);
        }
        // save KISS
        Integer profileId = SpringSecurityUtil.getCurrentUserId();
        profileRepository.updateTempUsername(profileId, dto.getUsername());
        String responseText = bundleService.getMessage("reset.password.response", lang);
        return new AppResponse<>(String.format(responseText, dto.getUsername()));
    }

    public AppResponse<String> updateUsernameConfirm(@Valid CodeConfirmDTO dto, AppLanguage lang) {
        Integer profileId = SpringSecurityUtil.getCurrentUserId();
        ProfileEntity profile = getById(profileId);
        String tempUsername = profile.getTempUsername();
        // check
        if (PhoneUtil.isPhone(tempUsername)) {
            smsHistoryService.check(tempUsername, dto.getCode(), lang);
        } else if (EmailUtil.isEmail(tempUsername)) {
            emailHistoryService.check(tempUsername, dto.getCode(), lang);
        }
        // update username
        profileRepository.updateTempUsername(profileId, tempUsername);
        // response
        List<ProfileRole> roles = profileRoleRepository.getAllRolesListByProfileId(profile.getId());
        String jwt = JwtUtil.encode(profile.getUsername(), profile.getId(), roles);
        return new AppResponse<>(jwt, bundleService.getMessage("change.username.success", lang));
    }

    public AppResponse<String> updatePhoto(String photoId, AppLanguage lang) {
        Integer profileId = SpringSecurityUtil.getCurrentUserId();
        ProfileEntity profile = getById(profileId);
        profileRepository.updatePhoto(profileId, photoId);

        if (profile.getPhotoId() != null && !profile.getPhotoId().equals(photoId)) {
            attachService.delete(profile.getPhotoId()); // delete old image
        }
        return new AppResponse<>(bundleService.getMessage("profile.photo.update.success", lang));
    }

    public ProfileEntity getById(int id) {
        return profileRepository.findByIdAndVisibleTrue(id).orElseThrow(() -> {
            log.error("Profile not found. ID: {}", id);
            throw new AppBadException("Profile not found");
        });
    }

    public PageImpl<ProfileDTO> filter(ProfileFilterDTO dto, int page, int size, AppLanguage lang) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<ProfileDetailMapper> filterResult = null;
        if (dto.getQuery() == null) {
            filterResult = profileRepository.filter(pageRequest);
        } else {
            filterResult = profileRepository.filter("%" + dto.getQuery().toLowerCase() + "%", pageRequest);
        }

        List<ProfileDTO> resultList = filterResult.stream().map(this::toDTO).toList();
        return new PageImpl<>(resultList, pageRequest, filterResult.getTotalElements());
    }

    public AppResponse<String> changeStatus(Integer id, GeneralStatus status, AppLanguage lang) {
        profileRepository.changeStatus(id, status);
        return new AppResponse<>(bundleService.getMessage("profile.status.update.success", lang));
    }

    public AppResponse<String> delete(Integer id, AppLanguage lang) {
        profileRepository.delete(id);
        return new AppResponse<>(bundleService.getMessage("profile.delete.success", lang));
    }

    public ProfileDTO toDTO(ProfileEntity entity) {
        ProfileDTO dto = new ProfileDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setUsername(entity.getUsername());
        if (entity.getRoleList() != null) {
            List<ProfileRole> roleList = entity.getRoleList().stream()
                    .map(ProfileRoleEntity::getRoles)
                    .toList();
            dto.setRoleList(roleList);
        }
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setPhoto(attachService.attachDTO(entity.getPhotoId()));
        dto.setStatus(entity.getStatus());
        return dto;
    }

    public ProfileDTO toDTO(ProfileDetailMapper mapper) {
        ProfileDTO dto = new ProfileDTO();
        dto.setId(mapper.getId());
        dto.setName(mapper.getName());
        dto.setUsername(mapper.getUsername());
        if (mapper.getRoles() != null) {
            List<ProfileRole> roleList = Arrays.stream(mapper.getRoles().split(","))
                    .map(roleName -> ProfileRole.valueOf(roleName))
                    .toList();
            dto.setRoleList(roleList);
        }
        dto.setCreatedDate(mapper.getCreatedDate());
        dto.setPhoto(attachService.attachDTO(mapper.getPhotoId()));
        dto.setStatus(mapper.getStatus());
        dto.setPostCount(mapper.getPostCount());
        return dto;
    }

}
