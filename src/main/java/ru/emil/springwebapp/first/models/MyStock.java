package ru.emil.springwebapp.first.models;

import ru.tinkoff.invest.openapi.model.rest.Candle;
import ru.tinkoff.invest.openapi.model.rest.MarketInstrument;
import ru.tinkoff.invest.openapi.model.rest.SearchMarketInstrument;

import java.util.List;

public class MyStock{

    SearchMarketInstrument searchMarketInstrument;

    ru.tinkoff.invest.openapi.model.rest.MarketInstrument MarketInstrument;

    List<Candle> candles;

    String level;

    String deadCross;

    public String getDeadCross() {
        return deadCross;
    }

    public void setDeadCross(String deadCross) {
        this.deadCross = deadCross;
    }

    public MarketInstrument getMarketInstrument() {
        return MarketInstrument;
    }

    public void setMarketInstrument(MarketInstrument marketInstrument) {
        MarketInstrument = marketInstrument;
    }

    public SearchMarketInstrument getSearchMarketInstrument() {
        return searchMarketInstrument;
    }

    public void setSearchMarketInstrument(SearchMarketInstrument searchMarketInstrument) {
        this.searchMarketInstrument = searchMarketInstrument;
    }

    public List<Candle> getCandles() {
        return candles;
    }

    public void setCandles(List<Candle> candles) {
        this.candles = candles;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public MyStock(SearchMarketInstrument searchMarketInstrument,
                   MarketInstrument marketInstrument,
                   List<Candle> candles,
                   String level,
                   String deadCross) {
        this.searchMarketInstrument = searchMarketInstrument;
        MarketInstrument = marketInstrument;
        this.candles = candles;
        this.level = level;
        this.deadCross = deadCross;
    }
    public MyStock(SearchMarketInstrument searchMarketInstrument,
                   MarketInstrument marketInstrument,
                   List<Candle> candles,
                   String level) {
        this.searchMarketInstrument = searchMarketInstrument;
        MarketInstrument = marketInstrument;
        this.candles = candles;
        this.level = level;
    }
}
