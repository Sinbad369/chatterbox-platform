package api.chatterbox.uz.service;

import api.chatterbox.uz.dto.AppResponse;
import api.chatterbox.uz.dto.FilterResultDTO;
import api.chatterbox.uz.dto.ProfileDTO;
import api.chatterbox.uz.dto.post.*;
import api.chatterbox.uz.entity.PostEntity;
import api.chatterbox.uz.enums.GeneralStatus;
import api.chatterbox.uz.enums.ProfileRole;
import api.chatterbox.uz.exps.AppBadException;
import api.chatterbox.uz.repository.CustomPostRepository;
import api.chatterbox.uz.repository.PostRepository;
import api.chatterbox.uz.util.SpringSecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private AttachService attachService;

    @Autowired
    private CustomPostRepository customPostRepository;

    public PostDTO create(PostCreateDTO dto) {
        PostEntity entity = new PostEntity();
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setPhotoId(dto.getPhoto().getId());
        entity.setVisible(true);
        entity.setCreatedDate(LocalDateTime.now());
        entity.setProfileId(SpringSecurityUtil.getCurrentUserId());
        entity.setStatus(GeneralStatus.BLOCK); // or GeneralStatus.IN_REGISTRATION
        postRepository.save(entity);
        return toDTO(entity);
    }

    // Change Status method for Admin
    public String changeStatus(String id, GeneralStatus status) {
        PostEntity entity = get(id);
        entity.setStatus(status);
        postRepository.save(entity);
        return "Status changed to " + status;
    }

    public Page<PostDTO> getProfilePostList(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        Integer prtId = SpringSecurityUtil.getCurrentUserId();
        Page<PostEntity> result = postRepository.getAllByProfileIdAndVisibleTrueOrderByCreatedDateDesc(prtId, pageRequest);

        List<PostDTO> dtoList = result.getContent().stream()
                .map(dto -> toInfoDTO(dto))
                .toList();


        return new PageImpl<PostDTO>(dtoList, pageRequest, result.getTotalElements());
    }

    public PostDTO getById(String id) {
        PostEntity entity = get(id);
        return toDTO(entity);
    }

    public PostDTO update(String id, PostCreateDTO dto) {
        PostEntity entity = get(id);
        Integer profileId = SpringSecurityUtil.getCurrentUserId();
        if (!SpringSecurityUtil.hasRole(ProfileRole.ROLE_ADMIN) && !entity.getProfileId().equals(profileId)) {
            throw new AppBadException("You do not have permission to update this post");
        }
        String deletePhotoId = null;
        if (!dto.getPhoto().getId().equals(entity.getPhotoId())) { // delete old image
            deletePhotoId = entity.getPhotoId();
        }
        entity.setTitle(dto.getTitle());
        entity.setContent(dto.getContent());
        entity.setPhotoId(dto.getPhoto().getId());
        postRepository.save(entity);
        if (deletePhotoId != null) {
            attachService.delete(deletePhotoId);
        }
        return toInfoDTO(entity);
    }

    public AppResponse<String> delete(String id) {
        PostEntity entity = get(id);
        entity.setCreatedDate(LocalDateTime.now());
        Integer profileId = SpringSecurityUtil.getCurrentUserId();
        if (!SpringSecurityUtil.hasRole(ProfileRole.ROLE_ADMIN) && !entity.getProfileId().equals(profileId)) {
            throw new AppBadException("You do not have permission to update/delete this post");
        }

        postRepository.delete(id);
        return new AppResponse("Success");
    }

    public Page<PostDTO> filter(PostFilterDTO filterDTO, int page, int size) {
        FilterResultDTO<PostEntity> resultDTO = customPostRepository.filter(filterDTO, page, size);
        List<PostDTO> dtoList = resultDTO.getList().stream()
                .map(this::toDTO).toList();
        return new PageImpl<>(dtoList, PageRequest.of(page, size), resultDTO.getTotalCount());
    }

    public PageImpl<PostDTO> adminFilter(PostAdminFilterDTO dto, int page, int size) {
        FilterResultDTO<Object[]> resultDTO = customPostRepository.filter(dto, page, size);
        List<PostDTO> dtoList = resultDTO.getList().stream()
                .map(this::toDTO).toList();
        return new PageImpl<>(dtoList, PageRequest.of(page, size), resultDTO.getTotalCount());
    }

    public List<PostDTO> getSimilarPostList(SimilarPostListDTO dto) {
        List<PostEntity> postEntityList = postRepository.getSimilarPostList(dto.getExceptId());
        List<PostDTO> dtoList = postEntityList.stream()
                .map(this::toInfoDTO).toList();
        return dtoList;
    }

    public PostDTO toDTO(PostEntity entity) {
        PostDTO dto = new PostDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setContent(entity.getContent());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setPhoto(attachService.attachDTO(entity.getPhotoId()));
        return dto;
    }

    public PostDTO toDTO(Object[] obj) {
        PostDTO post = new PostDTO();
        post.setId((String) obj[0]);
        post.setTitle((String) obj[1]);
        if (obj[2] != null) {
            post.setPhoto(attachService.attachDTO((String) obj[2]));
        }
        post.setCreatedDate((LocalDateTime) obj[3]);

        ProfileDTO profile = new ProfileDTO();
        profile.setId((Integer) obj[4]);
        profile.setName((String) obj[5]);
        profile.setUsername((String) obj[6]);
        post.setProfile(profile);
        if (obj.length > 7 && obj[7] != null) {
            post.setStatus((GeneralStatus) obj[7]);
        }
        return post;
    }

    public PostDTO toInfoDTO(PostEntity entity) {
        PostDTO dto = new PostDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setPhoto(attachService.attachDTO(entity.getPhotoId()));
        return dto;
    }

    public PostEntity get(String id) {
        return postRepository.findById(id).orElseThrow(() -> {
            throw new AppBadException("Post not found: " + id);
        });
    }
}
