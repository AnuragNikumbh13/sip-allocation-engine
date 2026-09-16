package sip_allocation_engine.service;

import org.springframework.stereotype.Service;
import sip_allocation_engine.dto.NAVRequestDTO;
import sip_allocation_engine.dto.NAVResponseDTO;
import sip_allocation_engine.entity.Fund;
import sip_allocation_engine.entity.FundNAV;
import sip_allocation_engine.exception.FundNotFoundException;
import sip_allocation_engine.exception.NAVAlreadyExistsException;
import sip_allocation_engine.repository.FundNAVRepository;
import sip_allocation_engine.repository.FundRepository;

@Service
public class NAVService {

    private final FundRepository fundRepository;
    private final FundNAVRepository fundNAVRepository;

    public NAVService(FundRepository fundRepository,
                      FundNAVRepository fundNAVRepository) {
        this.fundRepository = fundRepository;
        this.fundNAVRepository = fundNAVRepository;
    }

    public NAVResponseDTO createNAV(NAVRequestDTO request) {

        Fund fund = fundRepository.findBySymbol(request.getFundSymbol())
                .orElseThrow(() ->
                        new FundNotFoundException("Fund not available"));

        if (fundNAVRepository
                .findByFundAndNavDate(fund, request.getNavDate())
                .isPresent()) {

            throw new NAVAlreadyExistsException(
                    "NAV already exists for this fund and date");
        }

        FundNAV fundNAV = new FundNAV(
                fund,
                request.getNavDate(),
                request.getNavPrice()
        );

        FundNAV savedNAV = fundNAVRepository.save(fundNAV);

        return new NAVResponseDTO(
                savedNAV.getId(),
                savedNAV.getFund().getSymbol(),
                savedNAV.getNavDate(),
                savedNAV.getNavPrice()
        );
    }
}