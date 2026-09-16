package sip_allocation_engine.dto;

import jakarta.persistence.Column;

import java.math.BigDecimal;
import java.time.LocalDate;

public class NAVRequestDTO {
    private String fundSymbol;
    private LocalDate navDate;
    private BigDecimal navPrice;

    public NAVRequestDTO() {
    }

    public NAVRequestDTO(String fundSymbol, LocalDate navDate, BigDecimal navPrice) {
        this.fundSymbol = fundSymbol;
        this.navDate = navDate;
        this.navPrice = navPrice;
    }

    public String getFundSymbol() {
        return fundSymbol;
    }

    public void setFundSymbol(String fundSymbol) {
        this.fundSymbol = fundSymbol;
    }

    public LocalDate getNavDate() {
        return navDate;
    }

    public void setNavDate(LocalDate navDate) {
        this.navDate = navDate;
    }

    public BigDecimal getNavPrice() {
        return navPrice;
    }

    public void setNavPrice(BigDecimal navPrice) {
        this.navPrice = navPrice;
    }

    @Override
    public String toString() {
        return "NAVRequestDTO{" +
                "fundSymbol='" + fundSymbol + '\'' +
                ", navDate=" + navDate +
                ", navPrice=" + navPrice +
                '}';
    }
}
