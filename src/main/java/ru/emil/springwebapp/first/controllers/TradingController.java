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
import ru.emil.springwebapp.first.pojo.StocksPagination;
import ru.tinkoff.invest.openapi.model.rest.Candle;

import java.util.List;

@Controller
@RequestMapping("/trading")
public class TradingController {

    @Autowired
    TradingDAO tradingDAO;

    @GetMapping("/candles.json")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<List<Candle>> getCandles(@RequestParam("figi") String figi,
                                                   @RequestParam("candleResolution") String candleResolution){
        ResponseEntity<List<Candle>> response;
        try {
            List<Candle> candles = tradingDAO.getCandels(figi, candleResolution);
            response =  ResponseEntity.ok(candles);
        }catch (Exception e){
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
        return response;
    }

    @PostMapping ("/stocks.json")
    @ResponseStatus(value = HttpStatus.OK)
    public ResponseEntity<PaginationEntity<MyStock>> getStocks(@RequestBody StocksPagination stocksPagination){
        ResponseEntity<PaginationEntity<MyStock>> response;
        try {
            List<MyStock> myStockList = tradingDAO.getStocks(stocksPagination);
            stocksPagination.setPage(1);
            stocksPagination.setLimit(Integer.MAX_VALUE);
            int size = tradingDAO.getStocks(stocksPagination).size();
            response =  ResponseEntity.ok(
                    new PaginationEntity(myStockList, size));
        }catch (Exception e){
            response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            e.printStackTrace();
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
