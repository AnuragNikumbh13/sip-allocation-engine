package sip_allocation_engine.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sip_allocation_engine.entity.FundNAV;
import sip_allocation_engine.entity.InvestorSIP;
import sip_allocation_engine.entity.SIPAllocationRecord;
import sip_allocation_engine.repository.FundNAVRepository;
import sip_allocation_engine.repository.InvestorSIPRepository;
import sip_allocation_engine.repository.SIPAllocationRecordRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;

@Component
public class SIPExecutionScheduler {

    private final InvestorSIPRepository investorSIPRepository;
    private final FundNAVRepository fundNAVRepository;
    private final SIPAllocationRecordRepository allocationRecordRepository;

    public SIPExecutionScheduler(
            InvestorSIPRepository investorSIPRepository,
            FundNAVRepository fundNAVRepository,
            SIPAllocationRecordRepository allocationRecordRepository) {

        this.investorSIPRepository = investorSIPRepository;
        this.fundNAVRepository = fundNAVRepository;
        this.allocationRecordRepository = allocationRecordRepository;
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void executeSIPs() {

        System.out.println("===== SIP SCHEDULER STARTED =====");

        LocalDate today = LocalDate.now();

        System.out.println("Today: " + today);

        int deductionDay = today.getDayOfMonth();

        System.out.println("Deduction Day: " + deductionDay);

        List<InvestorSIP> dueSIPs =
                investorSIPRepository
                        .findByActiveTrueAndDeductionDay(deductionDay);

        System.out.println("Due SIPs: " + dueSIPs.size());

        for (InvestorSIP sip : dueSIPs) {

            System.out.println("Processing SIP ID: " + sip.getId());

            boolean alreadyProcessed =
                    allocationRecordRepository
                            .findBySipAndAllocationDate(sip, today)
                            .isPresent();

            System.out.println("Already processed: " + alreadyProcessed);

            if (alreadyProcessed) {
                continue;
            }

            FundNAV fundNAV =
                    fundNAVRepository
                            .findByFundAndNavDate(sip.getFund(), today)
                            .orElse(null);

            System.out.println("NAV: " + fundNAV);

            if (fundNAV == null) {
                continue;
            }

            BigDecimal unitsAllocated =
                    sip.getMonthlyAmount()
                            .divide(
                                    fundNAV.getNavPrice(),
                                    4,
                                    RoundingMode.HALF_UP
                            );

            System.out.println("Units allocated: " + unitsAllocated);

            SIPAllocationRecord allocationRecord =
                    new SIPAllocationRecord(
                            sip,
                            today,
                            sip.getMonthlyAmount(),
                            fundNAV.getNavPrice(),
                            unitsAllocated
                    );

            allocationRecordRepository.save(allocationRecord);

            System.out.println("===== ALLOCATION SAVED =====");
        }
    }
}