package org.samsung.api.repository;

import org.samsung.api.dto.Collection;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CollectionRedisRepository extends CrudRepository<Collection, String> {
    Optional<Collection> findByGuid(String guid);
    Optional<Collection> findByVisitorId(String visitorId);
}
