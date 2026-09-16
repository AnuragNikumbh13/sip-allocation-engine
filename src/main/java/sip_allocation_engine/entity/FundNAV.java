package sip_allocation_engine.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"fund_id", "nav_date"})
        }
)

public class FundNAV {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate navDate;

    private BigDecimal navPrice;

    @ManyToOne
    @JoinColumn(name="fund_id",nullable = false)
    private Fund fund;

    protected FundNAV(){

    }
    public FundNAV( Fund fund,LocalDate navDate, BigDecimal navPrice) {
        this.fund = fund;
        this.navDate = navDate;
        this.navPrice = navPrice;
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

    public Fund getFund() {
        return fund;
    }

    public void setFund(Fund fund) {
        this.fund = fund;
    }
    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "FundNAV{" +
                "id=" + id +
                ", navDate=" + navDate +
                ", navPrice=" + navPrice +
                ", fund=" + (fund != null ? fund.getSymbol() : null) +
                '}';
    }


}
