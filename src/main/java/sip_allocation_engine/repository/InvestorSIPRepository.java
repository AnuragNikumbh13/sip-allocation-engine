package sip_allocation_engine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sip_allocation_engine.entity.InvestorSIP;

import java.util.List;
import java.util.Optional;

public interface InvestorSIPRepository extends JpaRepository<InvestorSIP,Long> {
    
    List<InvestorSIP> findByUserId(Long userId);
    List<InvestorSIP> findByActiveTrueAndDeductionDay(int DeductionDay);
}
