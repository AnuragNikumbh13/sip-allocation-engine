package sip_allocation_engine.dto;

public class CreateFundRequestDTO {

    private String symbol;
    private String name;
    private String category;
    private boolean active;

    public CreateFundRequestDTO() {
    }

    public CreateFundRequestDTO(String symbol, String name, String category, boolean active) {
        this.symbol = symbol;
        this.name = name;
        this.category = category;
        this.active = active;
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

    @Override
    public String toString() {
        return "CreateFundRequestDTO{" +
                "symbol='" + symbol + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", active=" + active +
                '}';
    }
}