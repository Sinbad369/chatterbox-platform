package api.chatterbox.uz.repository;

import api.chatterbox.uz.entity.ProfileEntity;
import api.chatterbox.uz.entity.ProfileRoleEntity;
import api.chatterbox.uz.enums.ProfileRole;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProfileRoleRepository extends CrudRepository<ProfileRoleEntity, Integer> {

    @Transactional
    @Modifying
    void deleteByProfileId(Integer profileId);

    @Query("select p.roles From ProfileRoleEntity p where p.profileId =?1")
    List<ProfileRole> getAllRolesListByProfileId(Integer profileId);
}
