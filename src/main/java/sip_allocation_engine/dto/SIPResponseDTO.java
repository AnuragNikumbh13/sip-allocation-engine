package sip_allocation_engine.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SIPResponseDTO {

    private Long id;
    private Long userId;
    private String fundSymbol;
    private BigDecimal monthlyAmount;
    private int deductionDay;
    private boolean active;
    private LocalDateTime createdAt;

    public SIPResponseDTO() {
    }

    public SIPResponseDTO(Long id, Long userId, String fundSymbol, BigDecimal monthlyAmount, int deductionDay, boolean active, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.fundSymbol = fundSymbol;
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

    public String getFundSymbol() {
        return fundSymbol;
    }

    public void setFundSymbol(String fundSymbol) {
        this.fundSymbol = fundSymbol;
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
    public String  toString() {
        return "SIPResponseDTO{" +
                "id=" + id +
                ", userId=" + userId +
                ", fundSymbol='" + fundSymbol + '\'' +
                ", monthlyAmount=" + monthlyAmount +
                ", deductionDay=" + deductionDay +
                ", active=" + active +
                ", createdAt=" + createdAt +
                '}';
    }
}