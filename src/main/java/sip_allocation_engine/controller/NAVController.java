package sip_allocation_engine.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sip_allocation_engine.dto.NAVRequestDTO;
import sip_allocation_engine.dto.NAVResponseDTO;
import sip_allocation_engine.service.NAVService;

@RestController
@RequestMapping("/api/v1/nav")
public class NAVController {
    private final NAVService navService;

    public NAVController(NAVService navService) {
        this.navService = navService;
    }

    @PostMapping
    public NAVResponseDTO createNAV(@RequestBody NAVRequestDTO request){
        return navService.createNAV(request);

    }
}
