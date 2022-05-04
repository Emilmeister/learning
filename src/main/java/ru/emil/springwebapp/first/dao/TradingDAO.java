package ru.emil.springwebapp.first.dao;
import org.springframework.stereotype.Component;
import ru.emil.springwebapp.first.constants.StockLevel;
import ru.emil.springwebapp.first.constants.Token;
import ru.emil.springwebapp.first.pojo.Filter;
import ru.emil.springwebapp.first.pojo.MyStock;
import ru.emil.springwebapp.first.pojo.StocksPagination;
import ru.tinkoff.invest.openapi.MarketContext;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.SandboxContext;
import ru.tinkoff.invest.openapi.model.rest.*;
import ru.tinkoff.invest.openapi.okhttp.OkHttpOpenApi;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


@Component
public class TradingDAO {

    private OpenApi api;
    private SandboxContext  sandboxContext;
    private SandboxAccount  sandboxAccount;
    private MarketContext   marketContext;
    private List<MarketInstrument> marketInstruments;
    private List<MyStock> myStocks;

    public List<MyStock> getMyStocks() {
        return myStocks;
    }

    public List<Candle> getCandels(String figi, String candleResolution){
        List<Candle> candles = null;
        try {
            CandleResolution resolution = CandleResolution.fromValue(candleResolution);
            candles = marketContext.getMarketCandles( figi,
                    normalOffsetDateTime(getOfsetDateTime(2010, 1, 1, 1, 1), resolution),
                    OffsetDateTime.now(),
                    resolution).get().get().getCandles();
        } catch (Exception e) {
            System.out.println("Не смог получить котировки для:" + figi + ' ' + candleResolution);
        }

        return candles;
    }

    public List<MyStock> getStocks(StocksPagination stocksPagination){
        List<MyStock> myStocks = this.myStocks;
        int from = (stocksPagination.getPage() -1 ) * stocksPagination.getLimit();
        int to = stocksPagination.getPage()* stocksPagination.getLimit();
        List<MyStock> myStocksFiltered = myStocks.stream()
                .filter(myStock -> applyFilter(stocksPagination.getFilter(), myStock))
                .collect(Collectors.toList());
        myStocksFiltered = myStocksFiltered.subList(Math.min(from,myStocksFiltered.size()), Math.min(to,myStocksFiltered.size()));
        return myStocksFiltered;
    }

    public String getFigiByTicker(String ticker) {
        try{
            return marketContext.searchMarketInstrumentsByTicker(ticker)
                    .get().getInstruments().stream().findAny().get().getFigi();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private void refresh(){

        Thread thread = new Thread(() -> {

            try {
                int i = 1;
                while (true) {
                    System.out.println("Stocks refreshed" + LocalDate.now());
                    Thread.sleep(1000*60*60*12);
                    setStocks();
                }
            }catch (InterruptedException e){
                System.out.println("Error to refresh stocks");
                e.printStackTrace();
            }
        });
        thread.start();

    }



    private void setStocks(){

//        List<MarketInstrument> mktInst = marketInstruments.subList(0,30);
        List<MarketInstrument> mktInst = marketInstruments;

        List<MyStock> myStocks = new LinkedList<>();

        for(int i = 0; i < mktInst.size();i++){

            try {
                MarketInstrument object = mktInst.get(i);
                List<Candle> candles = getCandels(object.getFigi(), "day");
                if(candles != null) {
                    MyStock stock = new MyStock( refactorMarketInstrument(object), null,
                            coridor(candles), 0.0);
                    stock.setDeadCross(deadCross(candles, stock));
                    myStocks.add(stock);
                }
            }catch (Exception e){
                System.out.println("Компания на каторой упала прога:\n" + mktInst.get(i));
            }

        }

        this.myStocks = myStocks;
    }

    public TradingDAO() {
        String token = Token.TOKEN;
        boolean sandboxMode = true;
        api = new OkHttpOpenApi(token, sandboxMode);
        if (api.isSandboxMode()) {
            sandboxContext = api.getSandboxContext();
            sandboxAccount = sandboxContext.performRegistration(new SandboxRegisterRequest()).join();
            marketContext  = api.getMarketContext();
        }

        try {
            marketInstruments = marketContext.getMarketStocks().get().getInstruments();
            setStocks();
            refresh();

        }catch (Exception e){
            //e.printStackTrace();
        }

    }

    private boolean applyFilter(Filter filter, MyStock myStock){
        boolean level = true;
        if (null != filter.getLevels()) level = filter.getLevels().contains(myStock.getLevel());
        boolean currency = true;
        if (null != filter.getCurrencies()) currency = filter.getCurrencies().contains(myStock.getSearchMarketInstrument().getCurrency());
        boolean type = true;
        if (null != filter.getInstrumentTypes()) type =  filter.getInstrumentTypes().contains(myStock.getSearchMarketInstrument().getType());

        boolean deadCross = true;
            boolean deadCrossFrom = null != filter.getDeadCrossFrom();
            boolean deadCrossTo = null != filter.getDeadCrossTo();
            deadCross = (!deadCrossFrom || filter.getDeadCrossFrom() <= myStock.getDeadCross() )
                     && (!deadCrossTo || filter.getDeadCrossTo() >= myStock.getDeadCross() );

        return level && deadCross && currency && type;
    }

    private SearchMarketInstrument refactorMarketInstrument(MarketInstrument marketInstrument){
        SearchMarketInstrument searchMarketInstrument = new SearchMarketInstrument();
        searchMarketInstrument.setCurrency(marketInstrument.getCurrency());
        searchMarketInstrument.setFigi(marketInstrument.getFigi());
        searchMarketInstrument.setIsin(marketInstrument.getIsin());
        searchMarketInstrument.setLot(marketInstrument.getLot());
        searchMarketInstrument.setName(marketInstrument.getName());
        searchMarketInstrument.setMinPriceIncrement(marketInstrument.getMinPriceIncrement());
        searchMarketInstrument.setTicker(marketInstrument.getTicker());
        searchMarketInstrument.setType(marketInstrument.getType());

        return searchMarketInstrument;
    }

    private MarketInstrument refactorSearchMarketInstrument(SearchMarketInstrument searchMarketInstrument){
        MarketInstrument marketInstrument = new MarketInstrument();
        marketInstrument.setCurrency(searchMarketInstrument.getCurrency());
        marketInstrument.setFigi(searchMarketInstrument.getFigi());
        marketInstrument.setIsin(searchMarketInstrument.getIsin());
        marketInstrument.setLot(searchMarketInstrument.getLot());
        marketInstrument.setName(searchMarketInstrument.getName());
        marketInstrument.setMinPriceIncrement(searchMarketInstrument.getMinPriceIncrement());
        marketInstrument.setTicker(searchMarketInstrument.getTicker());
        marketInstrument.setType(searchMarketInstrument.getType());

        return marketInstrument;
    }

    private double deadCross(List<Candle> candles, MyStock myStock){

        if(candles.size() < 200) return Double.MAX_VALUE;

        Double ma50 = 0.0;
        for(Candle c: candles.subList(0,50)){
            ma50 +=getAverage(c)/50.0;
        }

        Double ma200 = 0.0;
        for(Candle c: candles.subList(0,200)){
            ma200 +=getAverage(c)/200.0;
        }


        myStock.setMa50(ma50);
        myStock.setMa200(ma200);

        return (ma50-ma200)/ma50;
    }

    private StockLevel coridor(List<Candle> candles){

        int sumMax = candles.size();
        int sumMin = candles.size()*4/5;


        double valid = 0.92;
        double validNow = 0.85;
        StockLevel result = StockLevel.NONE;

        if(candles.size() < 3) return result;

        Candle now = candles.get(candles.size()-1);



        int between = Math.max(sumMax-sumMin,0);

        LinkedList<Pair> extrs = new LinkedList<>();


        //dobavil extremumi
        for(int i = 1; i < candles.size() - 1; i++){
            Candle before   = candles.get(i-1);
            Candle current  = candles.get(i);
            Candle after    = candles.get(i+1);

            if(     ( getMax(current)- getMax(before) )
                    *( getMax(after) - getMax(current) ) < 0 &&
                    ( getMax(current)- getMax(before) ) > 0){



                extrs.add(new Pair(i , getMax(current)));


            }else if(       ( getMin(current) - getMin(before) )
                    *( getMin(after) - getMin(current) ) < 0 &&
                    ( getMin(current) - getMin(before) ) < 0){

                extrs.add(new Pair(i , getMin(current)));


            }
        }

        int closes = Math.max(2*extrs.size()/100,2);/////////////////////////


        for(int k = 0; k < between; k++){
            int indMin = 0;
            int indMax = 0;

            //udalenie lishnih elementov
            for(int i = 0; i < extrs.size(); i++){
                if(extrs.get(i).getLeft() < k){
                    extrs.remove(i--);
                }

            }
            extrs.add(0,
                    new Pair(k, ( getMax(candles.get(k))+getMin(candles.get(k)) )/2.0)
            );

            //perezapis maksimumov i minimumov

            for (int i = 0; i < extrs.size(); i++) {
                if (getMax(candles.get(extrs.get(i).getLeft())) > getMax(candles.get(extrs.get(indMax).getLeft()))) {
                    indMax = i;
                }
                if (getMin(candles.get(extrs.get(i).getLeft())) < getMin(candles.get(extrs.get(indMin).getLeft()))) {
                    indMin = i;
                }
            }

            Candle candleMax   = candles.get(extrs.get(indMax).getLeft());
            Candle candleMin  = candles.get(extrs.get(indMin).getLeft());
            double width = getMax(candleMax) - getMin(candleMin);
            double max = getMax(candleMax);
            double min = getMin(candleMin);

            int sumOfCloseMaxs = 0;
            int sumOfCloseMins = 0;


            //blizost k max i mins
            for (Pair pair : extrs) {
                if ((pair.getRight() - min) / width > valid && (pair.getRight() - min) / width < 1.0) {
                    sumOfCloseMaxs++;
                }
                if ((max - pair.getRight()) / width > valid && (max - pair.getRight()) / width < 1.0) {
                    sumOfCloseMins++;
                }
            }

            boolean nowCloseToMax = (now.getC().doubleValue()-min)/width > validNow
                    && (now.getC().doubleValue() - min)/width < 1.0;
            boolean nowCloseToMin = (max - now.getC().doubleValue())/width > validNow
                    && (max - now.getC().doubleValue())/width < 1.0;
            if(sumOfCloseMaxs > closes && sumOfCloseMins > closes && (nowCloseToMax || nowCloseToMin)) {result = StockLevel.CORIDOR; break;}
            else if(sumOfCloseMaxs > closes && nowCloseToMax) {result = StockLevel.UPPER_LEVEL; break;}
            else if(sumOfCloseMins > closes && nowCloseToMin) {result = StockLevel.LOWER_LEVEL; break;}

        }



        return result;



    }

    private Double getMax(Candle c){
        return Math.max(c.getO().doubleValue(), c.getC().doubleValue());
    }

    private Double getMin(Candle c){
        return Math.min(c.getO().doubleValue(), c.getC().doubleValue());
    }

    private Double getAverage(Candle c){ return (c.getC().doubleValue()+c.getO().doubleValue())/2.0;}

    private OffsetDateTime normalOffsetDateTime(OffsetDateTime before, CandleResolution resolution){


        if (resolution.getValue().equals("hour")){
            if( before.isBefore( OffsetDateTime.now().minusWeeks(1) ) ) {
                before = OffsetDateTime.now().minusWeeks(1).plusHours(1);
            }
        }
        if (resolution.getValue().equals("day")){
            if( before.isBefore( OffsetDateTime.now().minusYears(1) ) ) {
                before = OffsetDateTime.now().minusDays(300);
            }
        }
        if (resolution.getValue().equals("week")){
            if( before.isBefore( OffsetDateTime.now().minusYears(2) ) ) {
                before = OffsetDateTime.now().minusYears(2).plusWeeks(1);
            }
        }
        if (resolution.getValue().equals("month")){
            if( before.isBefore( OffsetDateTime.now().minusYears(10) ) ) {
                before = OffsetDateTime.now().minusYears(10).plusMonths(1);
            }
        }
        return before;
    }

    private OffsetDateTime getOfsetDateTime(int year,
                                            int month,
                                            int dayOfMonth,
                                            int hour,
                                            int minute){
        int second = 0;
        int nanoOfSecond = 0;

        return OffsetDateTime.of(year,
                month,
                dayOfMonth,
                hour,
                minute,
                second,
                nanoOfSecond, ZoneOffset.of("+3"));
    }

    class Pair{

        private int left;
        private double right;

        public int getLeft() {
            return left;
        }

        public void setLeft(int left) {
            this.left = left;
        }

        public double getRight() {
            return right;
        }

        public void setRight(double right) {
            this.right = right;
        }

        public Pair(int left, double right) {
            this.left = left;
            this.right = right;
        }
    }
}
