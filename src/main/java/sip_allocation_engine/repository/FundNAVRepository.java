package sip_allocation_engine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sip_allocation_engine.entity.Fund;
import sip_allocation_engine.entity.FundNAV;

import java.time.LocalDate;
import java.util.Optional;

public interface FundNAVRepository extends JpaRepository<FundNAV,Long> {

    Optional<FundNAV> findByFundAndNavDate(
            Fund fund,
            LocalDate navDate
    );
    Optional<FundNAV> findTopByFundOrderByNavDateDesc(Fund fund);

}
