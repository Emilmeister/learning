package ru.emil.springwebapp.first.data.pojo;

import ru.emil.springwebapp.first.constants.StockLevel;
import ru.tinkoff.invest.openapi.model.rest.Currency;
import ru.tinkoff.invest.openapi.model.rest.InstrumentType;

import java.util.List;

public class Filter {

    List<StockLevel> levels;
    List<Currency> currencies;
    List<InstrumentType> instrumentTypes;
    Double deadCrossFrom;
    Double deadCrossTo;

    public Filter() {
    }

    public List<StockLevel> getLevels() {
        return levels;
    }

    public void setLevels(List<StockLevel> levels) {
        this.levels = levels;
    }

    public List<Currency> getCurrencies() {
        return currencies;
    }

    public void setCurrencies(List<Currency> currencies) {
        this.currencies = currencies;
    }

    public List<InstrumentType> getInstrumentTypes() {
        return instrumentTypes;
    }

    public void setInstrumentTypes(List<InstrumentType> instrumentTypes) {
        this.instrumentTypes = instrumentTypes;
    }

    public Double getDeadCrossFrom() {
        return deadCrossFrom;
    }

    public void setDeadCrossFrom(Double deadCrossFrom) {
        this.deadCrossFrom = deadCrossFrom;
    }

    public Double getDeadCrossTo() {
        return deadCrossTo;
    }

    public void setDeadCrossTo(Double deadCrossTo) {
        this.deadCrossTo = deadCrossTo;
    }
}
