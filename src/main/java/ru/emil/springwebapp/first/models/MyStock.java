package ru.emil.springwebapp.first.models;

import ru.tinkoff.invest.openapi.model.rest.Candle;
import ru.tinkoff.invest.openapi.model.rest.MarketInstrument;
import ru.tinkoff.invest.openapi.model.rest.SearchMarketInstrument;

import java.util.List;

public class MyStock{

    SearchMarketInstrument searchMarketInstrument;

    ru.tinkoff.invest.openapi.model.rest.MarketInstrument marketInstrument;

    List<Candle> candles;

    String level;

    String deadCross;

    double currentPrice;

    double ma50;

    double ma200;


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

    public double getCurrentPrice() {
        return currentPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }


    public String getDeadCross() {
        return deadCross;
    }

    public void setDeadCross(String deadCross) {
        this.deadCross = deadCross;
    }

    public MarketInstrument getMarketInstrument() {
        return marketInstrument;
    }

    public void setMarketInstrument(MarketInstrument marketInstrument) {
        this.marketInstrument = marketInstrument;
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
        this.marketInstrument = marketInstrument;
        this.candles = candles;
        this.level = level;
        this.deadCross = deadCross;
    }
    public MyStock(SearchMarketInstrument searchMarketInstrument,
                   MarketInstrument marketInstrument,
                   List<Candle> candles,
                   String level) {
        this.searchMarketInstrument = searchMarketInstrument;
        this.marketInstrument = marketInstrument;
        this.candles = candles;
        this.level = level;
    }
}
