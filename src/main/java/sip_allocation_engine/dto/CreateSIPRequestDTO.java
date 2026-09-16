package sip_allocation_engine.dto;

import sip_allocation_engine.entity.Fund;

import java.math.BigDecimal;

public class CreateSIPRequestDTO {

    private Long userId;
    private String fundSymbol;
    private BigDecimal monthlyAmount;
    private int deductionDay;

    public CreateSIPRequestDTO() {
    }

    public CreateSIPRequestDTO(Long userId, String fundSymbol, BigDecimal monthlyAmount, int deductionDay) {
        this.userId = userId;
        this.fundSymbol = fundSymbol;
        this.monthlyAmount = monthlyAmount;
        this.deductionDay = deductionDay;
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

    @Override
    public String toString() {
        return "CreateSIPRequestDTO{" +
                "userId=" + userId +
                ", fundSymbol='" + fundSymbol + '\'' +
                ", monthlyAmount=" + monthlyAmount +
                ", deductionDay=" + deductionDay +
                '}';
    }
}
