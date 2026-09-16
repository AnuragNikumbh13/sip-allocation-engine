package sip_allocation_engine.dto;

import java.math.BigDecimal;

public class FundResponseDTO {

    private Long id;
    private String symbol;
    private String name;
    private String category;
    private boolean active;
    private BigDecimal currentNav;

    public FundResponseDTO(Long id, String symbol, String name, String category, boolean active, BigDecimal currentNav) {
        this.id = id;
        this.symbol = symbol;
        this.name = name;
        this.category = category;
        this.active = active;
        this.currentNav = currentNav;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public BigDecimal getCurrentNav() {
        return currentNav;
    }

    public void setCurrentNav(BigDecimal currentNav) {
        this.currentNav = currentNav;
    }

    @Override
    public String toString() {
        return "FundResponseDTO{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", active=" + active +
                ", currentNav=" + currentNav +
                '}';
    }
}
