package ru.emil.springwebapp.first.controllers;


import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.emil.springwebapp.first.dao.TradingDAO;

@Controller
@RequestMapping("/trading")
public class TradingController {

    @Autowired
    TradingDAO tradingDAO;

    @GetMapping("/candles.json")
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody String getCandles(@RequestParam("figi") String figi,
                                           @RequestParam("candleResolution") String candleResolution){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(tradingDAO.getCandels(figi, candleResolution));
        }catch (Exception e){
            e.printStackTrace();
        }
        return "lol";
    }

    @GetMapping("/stocks.json")
    @ResponseStatus(value = HttpStatus.OK)
    public @ResponseBody String getStocks(@RequestParam("from") int from, @RequestParam("to") int to, @RequestParam("withLevel") boolean withLevel){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(tradingDAO.getStocks(from,to, withLevel));
        }catch (Exception e){
            e.printStackTrace();
        }
        return "lol";
    }

    @GetMapping("/stocks/graphic/ticker/{ticker}")
    @ResponseStatus(value =HttpStatus.OK)
    public @ResponseBody Object getFigiByTicker(@PathVariable("ticker") String ticker){
        String figi = tradingDAO.getFigiByTicker(ticker);
        if(figi != null)
            return "{ \"value\": \""+ figi+"\"}";
        return null;
    }

    //Страницы

    @GetMapping("/stocks/graphic/figi/{figi}")
    public String showGraphicByFigi(@PathVariable("figi") String figi){
        return "/trading/showCandles.html";
    }



    @GetMapping("/stocks")//////////////////
    public String showStocks(){
        return "trading/main_page.html";
    }





}
