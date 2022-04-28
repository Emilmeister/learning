package ru.emil.springwebapp.first.controllers;


import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.emil.springwebapp.first.dao.TradingDAO;
import ru.emil.springwebapp.first.pojo.MyStock;
import ru.emil.springwebapp.first.pojo.Pagination;
import ru.emil.springwebapp.first.pojo.PaginationEntity;

@Controller
@RequestMapping("/trading")
public class TradingController {

    @Autowired
    TradingDAO tradingDAO;

    @GetMapping("/candles.json")
    @ResponseStatus(value = HttpStatus.OK)
    public String getCandles(@RequestParam("figi") String figi,
                                           @RequestParam("candleResolution") String candleResolution){
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.writeValueAsString(tradingDAO.getCandels(figi, candleResolution));
        }catch (Exception e){
            e.printStackTrace();
        }
        return "lol";
    }

    @PostMapping ("/stocks.json")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<PaginationEntity<MyStock>> getStocks(@RequestBody Pagination pagination, @RequestParam("withLevel") boolean withLevel){
        ResponseEntity<PaginationEntity<MyStock>> response;
        try {
            int from = (pagination.getPage() -1 ) * pagination.getLimit();
            int to = pagination.getPage()* pagination.getLimit();
            response =  ResponseEntity.ok(
                    new PaginationEntity(
                            tradingDAO.getStocks(from,to, withLevel),
                            tradingDAO.getStocks(0,tradingDAO.getMyStocks().size(),
                            withLevel).size()));
        }catch (Exception e){
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return response;
    }

    @GetMapping("/stocks/graphic/ticker/{ticker}")
    @ResponseStatus(value =HttpStatus.OK)
    public ResponseEntity<String> getFigiByTicker(@PathVariable("ticker") String ticker){
        ResponseEntity<String> response;
        String figi = tradingDAO.getFigiByTicker(ticker);
        if(figi != null)
            response = ResponseEntity.ok(figi);
        else {
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return response;
    }






}
