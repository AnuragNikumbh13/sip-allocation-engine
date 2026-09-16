package sip_allocation_engine.dto;

import jakarta.persistence.Column;

import java.math.BigDecimal;
import java.time.LocalDate;

public class NAVResponseDTO {

    private Long id;
    private String fundSymbol;
    private LocalDate navDate;
    private BigDecimal navPrice;

    public NAVResponseDTO() {
    }

    public NAVResponseDTO(Long id, String fundSymbol, LocalDate navDate, BigDecimal navPrice) {
        this.id = id;
        this.fundSymbol = fundSymbol;
        this.navDate = navDate;
        this.navPrice = navPrice;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        return "NAVResponseDTO{" +
                "id=" + id +
                ", fundSymbol='" + fundSymbol + '\'' +
                ", navDate=" + navDate +
                ", navPrice=" + navPrice +
                '}';
    }
}
