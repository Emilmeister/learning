package ru.emil.springwebapp.first.dao;
import org.springframework.stereotype.Component;
import ru.emil.springwebapp.first.models.MyStock;
import ru.tinkoff.invest.openapi.MarketContext;
import ru.tinkoff.invest.openapi.OpenApi;
import ru.tinkoff.invest.openapi.SandboxContext;
import ru.tinkoff.invest.openapi.model.rest.*;
import ru.tinkoff.invest.openapi.okhttp.OkHttpOpenApi;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


////
@Component
public class TradingDAO {

    private OpenApi api;
    private SandboxContext  sandboxContext;
    private SandboxAccount  sandboxAccount;
    private MarketContext   marketContext;
    private List<MarketInstrument> marketInstruments;
    private List<MyStock> myStocks;


    public MyStock getCandels(String figi, String candleResolution){
        MyStock myStock = null;
        try {
            CandleResolution resolution = CandleResolution.fromValue(candleResolution);

            SearchMarketInstrument searchMarketInstrument = marketContext.searchMarketInstrumentByFigi(figi).get().get();
            List<Candle> candles = marketContext.getMarketCandles( figi,
                    normalOffsetDateTime(getOfsetDateTime(2010, 1, 1, 1, 1), resolution),
                    OffsetDateTime.now(),
                    resolution).get().get().getCandles();
            myStock = new MyStock(searchMarketInstrument, null, candles, coridor(candles));

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
           //e.printStackTrace();
        }

        return myStock;
    }



    public List<MyStock> getStocks(int from, int to, boolean withLevel){
        //System.out.println(myStocks.size() + "----------");

        List<MyStock> myStocks = this.myStocks;

        if(!withLevel){
            if (from >= 0 && to <= myStocks.size() && from <= to)
                myStocks = myStocks.subList(from, to);
            else if (from >= 0 && from <= myStocks.size())
                myStocks = myStocks.subList(from, myStocks.size());
            else
                myStocks = myStocks.subList(0, Math.min(10, myStocks.size()));
        }else if(withLevel) {

            List<MyStock> myStocksFiltered = myStocks.stream().filter(myStock -> !myStock.getLevel().equals("None")).collect(Collectors.toList());

            if (from >= 0 && to <= myStocksFiltered.size() && from <= to)
                myStocks = myStocksFiltered.subList(from, to);
            else if (from >= 0 && from <= myStocksFiltered.size()){
                myStocks = myStocksFiltered.subList(from, myStocksFiltered.size());
            }
            else{
                myStocks = myStocksFiltered.subList(0, Math.min(10, myStocksFiltered.size()));
            }


        }

        return myStocks;
    }


    public void setStocks(){

        List<MarketInstrument> mktInst = marketInstruments.subList(0,100);
        //List<MarketInstrument> mktInst = marketInstruments;

        List<MyStock> myStocks = new LinkedList<>();

        for(int i = 0; i < mktInst.size();i++){////////////////////

            try {
                MarketInstrument x = mktInst.get(i);
                if(x.getType() == InstrumentType.STOCK) {
                    MyStock temp = getCandels(x.getFigi(), "day");
                    if(temp != null) {
                        MyStock stock = new MyStock(null, x, null,
                                coridor(temp.getCandles()));
                        myStocks.add(stock);
                    }
                }
            }catch (Exception e){
                MarketInstrument x = mktInst.get(i);
                System.out.println("Номер компаннии на каторой упала прога:");
                System.out.println(mktInst.get(i));
                e.printStackTrace();
            }

        }

        this.myStocks = myStocks;
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

    public TradingDAO() {
        String token = "t.sHnKPgnhx9cVAxeBj1HGALH2pGGaIM8AEaMGqnc05EEtDMXwnmCTJVqulVb03UjRU4W8jFL6_YPZn2mvQIdr6w"; // токен авторизации
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


        }catch (Exception e){
            //e.printStackTrace();
        }

    }

    Double getMax(Candle c){
        return Math.max(c.getO().doubleValue(), c.getC().doubleValue());
    }

    Double getMin(Candle c){
        return Math.min(c.getO().doubleValue(), c.getC().doubleValue());
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


    private String coridor(List<Candle> candles){

        int sumMax = candles.size();
        int sumMin = candles.size()*4/5;


        double valid = 0.92;
        double validNow = 0.85;
        String result = "None";

        if(candles.size() < 3) return result;

        Candle now = candles.get(candles.size()-1);



        //candles = candles.subList(Math.max(candles.size()-sumMax, 0), candles.size());
        int between = Math.max(sumMax-sumMin,0);

        LinkedList<Pair> extrs = new LinkedList<>();


        //добавили экстремумы
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

//                if(current.getC().doubleValue() < candles.get(indMin).getC().doubleValue()){
//                    indMin = i;
//                }
                extrs.add(new Pair(i , getMin(current)));


            }
        }

        //for(int i = 0; i < extrs.size(); i++) System.out.println(extrs.get(i).getLeft() +":"+extrs.get(i).getRight());

        int closes = Math.max(2*extrs.size()/100,2);/////////////////////////


        for(int k = 0; k < between; k++){
            int indMin = 0;
            int indMax = 0;

            //удаление лишних элементов
            for(int i = 0; i < extrs.size(); i++){
                if(extrs.get(i).getLeft() < k){
                    extrs.remove(i--);
                }

            }
            extrs.add(0,
                    new Pair(k, ( getMax(candles.get(k))+getMin(candles.get(k)) )/2.0)
            );

            //перезапись максимумов и минимумов

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


            //System.out.println("width " + width);

            //близость к макс и мин
            for (Pair pair : extrs) {
                if ((pair.getRight() - min) / width > valid && (pair.getRight() - min) / width < 1.0) {
                    //System.out.println("raznitza "+ (pair.getRight() - min));
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


            //System.out.println(sumOfCloseMaxs+":"+sumOfCloseMins);
            //System.out.println(max+":"+min);
            //System.out.println(max+":"+min);

            //System.out.println(now.getC().doubleValue());
            if(sumOfCloseMaxs > closes && sumOfCloseMins > closes && (nowCloseToMax || nowCloseToMin)) {result = "Coridor"; break;}
            else if(sumOfCloseMaxs > closes && nowCloseToMax) {result = "Upper level"; break;}
            else if(sumOfCloseMins > closes && nowCloseToMin) {result = "Lower level"; break;}

        }



        return result;



    }

    private OffsetDateTime normalOffsetDateTime(OffsetDateTime before, CandleResolution resolution){

        switch (resolution.getValue()){
            case "hour":{
                if( before.isBefore( OffsetDateTime.now().minusWeeks(1) ) ) {
                    before = OffsetDateTime.now().minusWeeks(1).plusHours(1);
                }
                break;
            }
            case "day":{
                if( before.isBefore( OffsetDateTime.now().minusYears(1) ) ) {
                    before = OffsetDateTime.now().minusDays(180);
                }
                break;
            }
            case "week":{
                if( before.isBefore( OffsetDateTime.now().minusYears(2) ) ) {
                    before = OffsetDateTime.now().minusYears(2).plusWeeks(1);
                }
                break;
            }
            case "month":{
                if( before.isBefore( OffsetDateTime.now().minusYears(10) ) ) {
                    before = OffsetDateTime.now().minusYears(10).plusMonths(1);
                }
                break;
            }
            default:{
                before = OffsetDateTime.now().minusDays(1);
            }
        }
        return before;
    }
}
