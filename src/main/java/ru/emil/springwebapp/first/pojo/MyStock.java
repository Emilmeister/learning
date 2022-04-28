package ru.emil.springwebapp.first.pojo;

import ru.emil.springwebapp.first.constants.StockLevel;
import ru.tinkoff.invest.openapi.model.rest.Candle;
import ru.tinkoff.invest.openapi.model.rest.MarketInstrument;
import ru.tinkoff.invest.openapi.model.rest.SearchMarketInstrument;

import java.util.List;

public class MyStock{

    SearchMarketInstrument searchMarketInstrument;

    List<Candle> candles;

    StockLevel level;

    double deadCross;

    double ma50;

    double ma200;

    public MyStock(SearchMarketInstrument searchMarketInstrument,
                   List<Candle> candles,
                   StockLevel level,
                   double deadCross) {
        this.searchMarketInstrument = searchMarketInstrument;
        this.candles = candles;
        this.level = level;
        this.deadCross = deadCross;
    }
    public MyStock(SearchMarketInstrument searchMarketInstrument,
                   List<Candle> candles,
                   StockLevel level) {
        this.searchMarketInstrument = searchMarketInstrument;
        this.candles = candles;
        this.level = level;
    }


    public double getMa50() {
        return ma50;
    }

    public void setMa50(double ma50) {
        this.ma50 = ma50;
    }

    public double getMa200() {
        return ma200;
    }

    public void setMa200(double ma200) {
        this.ma200 = ma200;
    }

    public StockLevel getLevel() {
        return level;
    }

    public void setLevel(StockLevel level) {
        this.level = level;
    }

    public double getDeadCross() {
        return deadCross;
    }

    public void setDeadCross(double deadCross) {
        this.deadCross = deadCross;
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
}
