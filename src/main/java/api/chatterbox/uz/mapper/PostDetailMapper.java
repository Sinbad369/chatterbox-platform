package api.chatterbox.uz.mapper;

import java.time.LocalDateTime;

public interface PostDetailMapper {
    String getPostId();

    String getPostTitle();

    String getPostPhotoId();

    LocalDateTime getPostCreatedDate();

    Integer getProfileId();

    String getProfilename();

    String getProfileUsername();

}
