package sip_allocation_engine.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity

public class InvestorSIP {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    @ManyToOne
    @JoinColumn(name = "fund_id", nullable = false)
    private Fund fund;
    private BigDecimal monthlyAmount;
    private int deductionDay;
    private boolean active;
    private LocalDateTime createdAt;

    public InvestorSIP() {
    }

    public InvestorSIP( Long userId, Fund fund, BigDecimal monthlyAmount, int deductionDay, boolean active, LocalDateTime createdAt) {
        this.userId = userId;
        this.fund = fund;
        this.monthlyAmount = monthlyAmount;
        this.deductionDay = deductionDay;
        this.active = active;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Fund getFund() {
        return fund;
    }

    public void setFund(Fund fund) {
        this.fund = fund;
    }

    public BigDecimal getMonthlyAmount() {
        return monthlyAmount;
    }

    public void setMonthlyAmount(BigDecimal monthlyAmount) {
        this.monthlyAmount = monthlyAmount;
    }

    public int getDeductionDay() {
        return deductionDay;
    }

    public void setDeductionDay(int deductionDay) {
        this.deductionDay = deductionDay;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "InvestorSIP{" +
                "id=" + id +
                ", userId=" + userId +
                ", fund=" + fund +
                ", monthlyAmount=" + monthlyAmount +
                ", deductionDay=" + deductionDay +
                ", active=" + active +
                ", createdAt=" + createdAt +
                '}';
    }
}
