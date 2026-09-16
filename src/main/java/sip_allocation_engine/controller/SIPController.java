package sip_allocation_engine.controller;

import org.springframework.web.bind.annotation.*;
import sip_allocation_engine.dto.CreateSIPRequestDTO;
import sip_allocation_engine.dto.SIPResponseDTO;
import sip_allocation_engine.service.InvestorSIPService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sips")
public class SIPController {

    private final InvestorSIPService investorSIPService;

    public SIPController(InvestorSIPService investorSIPService) {
        this.investorSIPService = investorSIPService;
    }

    @PostMapping
    public SIPResponseDTO CreateSip(@RequestBody CreateSIPRequestDTO request){
        return investorSIPService.CreateSip(request);
    }

    @GetMapping("/user")
    public List<SIPResponseDTO> getAllSIPs() {
        return investorSIPService.getAllSIPs();
    }


    @GetMapping("/user/{userId}")
    public List<SIPResponseDTO> getSIPsByUserId(@PathVariable Long userId){
        return investorSIPService.getSIPsByUserId(userId);
    }
}
