package sip_allocation_engine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sip_allocation_engine.entity.InvestorSIP;
import sip_allocation_engine.entity.SIPAllocationRecord;

import java.time.LocalDate;
import java.util.Optional;

public interface SIPAllocationRecordRepository
        extends JpaRepository<SIPAllocationRecord, Long> {

    Optional<SIPAllocationRecord> findBySipAndAllocationDate(
            InvestorSIP sip,
            LocalDate allocationDate
    );
}