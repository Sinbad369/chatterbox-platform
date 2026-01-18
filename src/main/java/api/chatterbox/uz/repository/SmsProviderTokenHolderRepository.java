package api.chatterbox.uz.repository;

import api.chatterbox.uz.entity.SmsProviderTokenHolderEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface SmsProviderTokenHolderRepository extends CrudRepository<SmsProviderTokenHolderEntity, Integer> {
    Optional<SmsProviderTokenHolderEntity> findTop1By();
}
