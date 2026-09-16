package sip_allocation_engine.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"sip_id", "allocation_date"}
                )
        }
)
public class SIPAllocationRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "sip_id", nullable = false)
    private InvestorSIP sip;

    @Column(nullable = false)
    private LocalDate allocationDate;

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private BigDecimal nav;

    @Column(nullable = false, precision = 38, scale = 4)
    private BigDecimal unitsAllocated;

    public SIPAllocationRecord() {
    }

    public SIPAllocationRecord(
            InvestorSIP sip,
            LocalDate allocationDate,
            BigDecimal amount,
            BigDecimal nav,
            BigDecimal unitsAllocated
    ) {
        this.sip = sip;
        this.allocationDate = allocationDate;
        this.amount = amount;
        this.nav = nav;
        this.unitsAllocated = unitsAllocated;
    }

    public Long getId() {
        return id;
    }

    public InvestorSIP getSip() {
        return sip;
    }

    public void setSip(InvestorSIP sip) {
        this.sip = sip;
    }

    public LocalDate getAllocationDate() {
        return allocationDate;
    }

    public void setAllocationDate(LocalDate allocationDate) {
        this.allocationDate = allocationDate;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getNav() {
        return nav;
    }

    public void setNav(BigDecimal nav) {
        this.nav = nav;
    }

    public BigDecimal getUnitsAllocated() {
        return unitsAllocated;
    }

    public void setUnitsAllocated(BigDecimal unitsAllocated) {
        this.unitsAllocated = unitsAllocated;
    }

    @Override
    public String toString() {
        return "SIPAllocationRecord{" +
                "id=" + id +
                ", sip=" + sip +
                ", allocationDate=" + allocationDate +
                ", amount=" + amount +
                ", nav=" + nav +
                ", unitsAllocated=" + unitsAllocated +
                '}';
    }
}