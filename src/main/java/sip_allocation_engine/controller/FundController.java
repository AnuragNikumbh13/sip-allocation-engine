package sip_allocation_engine.controller;

import org.springframework.web.bind.annotation.*;
import sip_allocation_engine.dto.CreateFundRequestDTO;
import sip_allocation_engine.dto.FundResponseDTO;
import sip_allocation_engine.service.FundService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/funds")
public class FundController {
    private final FundService fundService;

    public FundController(FundService fundService) {
        this.fundService = fundService;
    }

    @GetMapping
    public List<FundResponseDTO> getAllFunds(){
        return fundService.getAllFunds();
    }

    @GetMapping("/{symbol}")
    public FundResponseDTO getFundBySymbol(@PathVariable String symbol){
        return fundService.getFundBySymbol(symbol);
    }

    @PostMapping
    public FundResponseDTO createFund(@RequestBody CreateFundRequestDTO request) {
        return fundService.createFund(request);
    }

}
