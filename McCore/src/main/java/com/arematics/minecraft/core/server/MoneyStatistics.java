package com.arematics.minecraft.core.server;

import com.arematics.minecraft.data.mode.model.GlobalStatisticData;
import com.arematics.minecraft.data.service.GlobalStatsService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Getter
public class MoneyStatistics {
    private static final String MONEY_GENERATED = "money-generated";
    private static final String MONEY_REMOVED = "money-removed";

    private final GlobalStatsService globalStatsService;

    private double moneyGenerate;
    private double moneyRemoved;
    private double moneyExists;

    @Autowired
    public MoneyStatistics(GlobalStatsService globalStatsService){
        this.globalStatsService = globalStatsService;
        try{
            this.moneyGenerate = Double.parseDouble(globalStatsService.findById(MoneyStatistics.MONEY_GENERATED).getValue());
            this.moneyRemoved = Double.parseDouble(globalStatsService.findById(MoneyStatistics.MONEY_REMOVED).getValue());
            this.moneyExists = moneyGenerate - moneyRemoved;
        }catch (Exception ignore){}
    }

    public void setMoneyGenerate(double moneyGenerate) {
        GlobalStatisticData data = new GlobalStatisticData(MoneyStatistics.MONEY_GENERATED, String.valueOf(moneyGenerate));
        this.globalStatsService.update(data);
        this.moneyGenerate = moneyGenerate;
        this.moneyExists = this.moneyGenerate - this.moneyRemoved;
    }

    public void setMoneyRemoved(double moneyRemoved) {
        GlobalStatisticData data = new GlobalStatisticData(MoneyStatistics.MONEY_REMOVED, String.valueOf(moneyRemoved));
        this.globalStatsService.update(data);
        this.moneyRemoved = moneyRemoved;
        this.moneyExists = this.moneyGenerate - this.moneyRemoved;
    }

    public void addMoneyGenerate(double amount){
        setMoneyGenerate(this.moneyGenerate + amount);
    }

    public void addMoneyRemoved(double amount){
        setMoneyRemoved(this.moneyRemoved + amount);
    }
}
