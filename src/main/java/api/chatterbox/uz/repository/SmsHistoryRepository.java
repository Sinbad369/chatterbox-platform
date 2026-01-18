package api.chatterbox.uz.repository;

import api.chatterbox.uz.entity.SmsHistoryEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface SmsHistoryRepository extends CrudRepository<SmsHistoryEntity, String> {

    // select count(*) from sms_history where phone = ? and created_date between ? and ?
    Long countByPhoneAndCreatedDateBetween(String phone, LocalDateTime from, LocalDateTime to);

    // select count(*) from sms_history where phone = ? order by created_date desc limit 1
    Optional<SmsHistoryEntity> findTop1ByPhoneOrderByCreatedDateDesc(String phone);

    @Modifying
    @Transactional
    @Query("update SmsHistoryEntity set attemptCount = coalesce(attemptCount, 0)  + 1 where id = ?1")
    void updateAttemptCount(String id);

}