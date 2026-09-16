package sip_allocation_engine.service;

import org.springframework.stereotype.Service;
import sip_allocation_engine.dto.CreateFundRequestDTO;
import sip_allocation_engine.dto.FundResponseDTO;
import sip_allocation_engine.entity.Fund;
import sip_allocation_engine.entity.FundNAV;
import sip_allocation_engine.exception.FundNotFoundException;
import sip_allocation_engine.repository.FundNAVRepository;
import sip_allocation_engine.repository.FundRepository;

import java.util.List;

@Service
public class FundService {


    private final FundRepository fundRepository;
    private final FundNAVRepository fundNAVRepository;

    public FundService(FundRepository fundRepository, FundNAVRepository fundNAVRepository) {
        this.fundRepository = fundRepository;
        this.fundNAVRepository = fundNAVRepository;
    }

    public List<FundResponseDTO> getAllFunds() {

        return fundRepository.findAll()
                .stream()
                .map(fund -> {

                    FundNAV latestNAV = fundNAVRepository
                            .findTopByFundOrderByNavDateDesc(fund)
                            .orElse(null);

                    return new FundResponseDTO(
                            fund.getId(),
                            fund.getSymbol(),
                            fund.getName(),
                            fund.getCategory(),
                            fund.isActive(),
                            latestNAV != null ? latestNAV.getNavPrice() : null
                    );
                })
                .toList();
    }

    public FundResponseDTO getFundBySymbol(String symbol) {

        Fund fund = fundRepository.findBySymbol(symbol)
                .orElseThrow(() ->
                        new FundNotFoundException("Fund not available")
                );

        FundNAV latestNAV = fundNAVRepository
                .findTopByFundOrderByNavDateDesc(fund)
                .orElse(null);

        return new FundResponseDTO(
                fund.getId(),
                fund.getSymbol(),
                fund.getName(),
                fund.getCategory(),
                fund.isActive(),
                latestNAV != null ? latestNAV.getNavPrice() : null
        );
    }

    public FundResponseDTO createFund(CreateFundRequestDTO request) {

        Fund fund = new Fund(
                request.getSymbol(),
                request.getName(),
                request.getCategory(),
                request.isActive()
        );

        Fund savedFund = fundRepository.save(fund);

        return new FundResponseDTO(
                savedFund.getId(),
                savedFund.getSymbol(),
                savedFund.getName(),
                savedFund.getCategory(),
                savedFund.isActive(),
                null
        );
    }


}
