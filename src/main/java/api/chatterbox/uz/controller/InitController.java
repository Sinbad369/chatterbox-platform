//package api.chatterbox.uz.controller;
//
//import api.chatterbox.uz.entity.ProfileEntity;
//import api.chatterbox.uz.enums.GeneralStatus;
//import api.chatterbox.uz.enums.ProfileRole;
//import api.chatterbox.uz.repository.ProfileRepository;
//import api.chatterbox.uz.service.ProfileRoleService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.time.LocalDateTime;
//import java.util.Optional;
//
//@RestController
//@RequestMapping("/init")
//public class InitController {
//    @Autowired
//    private BCryptPasswordEncoder bCryptPasswordEncoder;
//    @Autowired
//    private ProfileRepository profileRepository;
//    @Autowired
//    private ProfileRoleService profileRoleService;
//
//    @GetMapping("/all")
//    public String updateDetail() {
//        // adminjon@gmail.com
//        Optional<ProfileEntity> exists = profileRepository.findByUsernameAndVisibleTrue("adminjon@gmail.com");
//        if (exists.isPresent()) {
//            return "Present";
//        }
//        ProfileEntity profile = new ProfileEntity();
//        profile.setName("Admin");
//        profile.setUsername("adminjon@gmail.com");
//        profile.setVisible(true);
//        profile.setPassword(bCryptPasswordEncoder.encode("123456"));
//        profile.setStatus(GeneralStatus.ACTIVE);
//        profile.setCreatedDate(LocalDateTime.now());
//
//        profileRepository.save(profile); // save
//        profileRoleService.create(profile.getId(), ProfileRole.ROLE_USER);
//        profileRoleService.create(profile.getId(), ProfileRole.ROLE_ADMIN);
//        return "DONE";
//    }
//}
//
