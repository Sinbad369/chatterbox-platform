package api.chatterbox.uz.repository;

import api.chatterbox.uz.entity.AttachEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;


@Repository
public interface AttachRepository extends CrudRepository<AttachEntity, String> {
    @Transactional
    @Modifying
    @Query("update AttachEntity set visible = false where id = ?1")
    void delete(String id);

}