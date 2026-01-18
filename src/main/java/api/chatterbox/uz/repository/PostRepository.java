package api.chatterbox.uz.repository;

import api.chatterbox.uz.entity.PostEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PostRepository extends CrudRepository<PostEntity, String>, PagingAndSortingRepository<PostEntity, String> {
    // select * from post where profile_id = ? visible = true order by createdDate des limit 12
    // select count(*) .. .
    Page<PostEntity> getAllByProfileIdAndVisibleTrueOrderByCreatedDateDesc(Integer profileId, Pageable pageable);

    @Query("from PostEntity where id <> ?1 and visible = true and status = 'ACTIVE' order by createdDate desc limit 3")
    List<PostEntity> getSimilarPostList(String exceptId);

    @Transactional
    @Modifying
    @Query("update PostEntity set visible = false where id = ?1")
    void delete(String id);

    @Query("SELECT p FROM PostEntity p WHERE p.status = 'ACTIVE' AND p.visible = true")
    Page<PostEntity> findAllActive(Pageable pageable);

}
