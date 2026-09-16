package sip_allocation_engine.service;

import org.springframework.stereotype.Service;
import sip_allocation_engine.dto.CreateSIPRequestDTO;
import sip_allocation_engine.dto.SIPResponseDTO;
import sip_allocation_engine.entity.Fund;
import sip_allocation_engine.entity.InvestorSIP;
import sip_allocation_engine.exception.FundNotFoundException;
import sip_allocation_engine.repository.FundRepository;
import sip_allocation_engine.repository.InvestorSIPRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InvestorSIPService {

    private final FundRepository fundRepository;
    private final InvestorSIPRepository investorSIPRepository;

    public InvestorSIPService(FundRepository fundRepository, InvestorSIPRepository investorSIPRepository) {
        this.fundRepository = fundRepository;
        this.investorSIPRepository = investorSIPRepository;
    }

    public SIPResponseDTO CreateSip(CreateSIPRequestDTO request) {
        Fund fund = fundRepository.findBySymbol(request.getFundSymbol())
                .orElseThrow(() -> new FundNotFoundException("Fund not found"));

        InvestorSIP sip = new InvestorSIP(
                request.getUserId(),
                fund,
                request.getMonthlyAmount(),
                request.getDeductionDay(),
                true,
                LocalDateTime.now()
        );
        InvestorSIP savedSip = investorSIPRepository.save(sip);
        return new SIPResponseDTO(
                savedSip.getId(),
                savedSip.getUserId(),
                savedSip.getFund().getSymbol(),
                savedSip.getMonthlyAmount(),
                savedSip.getDeductionDay(),
                savedSip.isActive(),
                savedSip.getCreatedAt()
        );
    }

    public List<SIPResponseDTO> getAllSIPs() {
        return investorSIPRepository.findAll()
                .stream()
                .map(sip -> new SIPResponseDTO(
                        sip.getId(),
                        sip.getUserId(),
                        sip.getFund().getSymbol(),
                        sip.getMonthlyAmount(),
                        sip.getDeductionDay(),
                        sip.isActive(),
                        sip.getCreatedAt()
                ))
                .toList();
    }

    public List<SIPResponseDTO> getSIPsByUserId(Long userId) {

        return investorSIPRepository.findByUserId(userId)
                .stream()
                .map(savedSip -> new SIPResponseDTO(
                        savedSip.getId(),
                        savedSip.getUserId(),
                        savedSip.getFund().getSymbol(),
                        savedSip.getMonthlyAmount(),
                        savedSip.getDeductionDay(),
                        savedSip.isActive(),
                        savedSip.getCreatedAt()
                ))
                .toList();
    }
}

