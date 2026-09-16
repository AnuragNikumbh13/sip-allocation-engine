package sip_allocation_engine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sip_allocation_engine.entity.Fund;

import java.util.Optional;

public interface FundRepository extends JpaRepository<Fund,Long> {
    Optional<Fund> findBySymbol(String symbol);

}
