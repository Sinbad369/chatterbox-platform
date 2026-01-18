package api.chatterbox.uz.repository;

import api.chatterbox.uz.entity.ProfileEntity;
import api.chatterbox.uz.enums.GeneralStatus;
import api.chatterbox.uz.mapper.ProfileDetailMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface ProfileRepository extends CrudRepository<ProfileEntity, Integer>, PagingAndSortingRepository<ProfileEntity, Integer> {

    // select * from profile where username =? and visiible=true;
    Optional<ProfileEntity> findByUsernameAndVisibleTrue(String username);

    Optional<ProfileEntity> findByIdAndVisibleTrue(Integer id);

    @Modifying
    @Transactional
    @Query("update ProfileEntity set status = ?2 where id = ?1")
    void changeStatus(Integer id, GeneralStatus status);


    @Modifying
    @Transactional
    @Query("update ProfileEntity set password = ?2 where id = ?1")
    void updatePassword(Integer id, String password);

    @Modifying
    @Transactional
    @Query("update ProfileEntity set name = ?2 where id = ?1")
    void updateDetail(Integer id, String name);

    @Modifying
    @Transactional
    @Query("update ProfileEntity set tempUsername = ?2 where id = ?1")
    void updateTempUsername(Integer id, String tempUsername);

    @Modifying
    @Transactional
    @Query("update ProfileEntity set username = tempUsername where id = ?1")
    void updateUsername(Integer id, String username);

    @Modifying
    @Transactional
    @Query("update ProfileEntity set photoId =?2 where id = ?1")
    void updatePhoto(Integer id, String photoId);

    // select * from profile order by created_date desc offset ? limit ?
    @Query(value = "select p.id as id, p.name as name, p.username as username, p.photo_id as photoId, p.status as status, p.created_date as createdDate," +
            " (select count (post) from post as post where post.profile_id = p.id) as postCount," +
            " (select string_agg(roles, ',') from profile_role as pr where pr.profile_id = p.id) as roles" +
            " from profile as p " +
            "where p.visible = true order by p.created_date desc ", nativeQuery = true,
            countQuery = "select count(*) from profile where visible = true")
    Page<ProfileDetailMapper> filter(PageRequest pageRequest);

    @Query(value = "select p.id as id, p.name as name, p.username as username, p.photo_id as photoId, p.status as status, p.created_date as createdDate," +
            " (select count (post) from post as post where post.profile_id = p.id) as postCount," +
            " (select string_agg(roles, ',') from profile_role as pr where pr.profile_id = p.id) as roles" +
            " from profile as p " +
            "where (lower(p.username) like ?1 or lower(p.name) like ?1) and p.visible = true order by p.created_date desc ", nativeQuery = true,
            countQuery = "select count(*) from profile p where(lower(p.username) like ?1 or lower(p.name) like ?1) and visible = true")
    Page<ProfileDetailMapper> filter(String query, PageRequest pageRequest);

    /*@Query("From ProfileEntity as p inner join fetch p.roleList " +
            "where (lower(p.username) like ?1 or lower(p.name) like ?1) and p.visible=true order by p.createdDate desc")
    Page<ProfileEntity> filter(String query, PageRequest pageRequest);*/

    @Modifying
    @Transactional
    @Query("update ProfileEntity set visible =false where id = ?1")
    void delete(Integer id);
}
