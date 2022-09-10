package ru.emil.springwebapp.first.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.emil.springwebapp.first.dao.TradingDAO;
import ru.emil.springwebapp.first.data.pojo.Level;
import ru.tinkoff.invest.openapi.model.rest.Candle;
import ru.tinkoff.invest.openapi.model.rest.CandleResolution;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LevelService {

    private final TradingDAO tradingDAO;

    public LevelService(@Autowired TradingDAO tradingDAO) {
        this.tradingDAO = tradingDAO;
    }

    public List<Level> getSpecter(String figi, CandleResolution candleResolution, Integer initNumberOfLevels, Integer filterNumbers) {
        List<Level> levels = new ArrayList<>(initNumberOfLevels);
        List<Candle> candles = tradingDAO.getExtremums(tradingDAO.getCandels(figi, candleResolution.toString()));
        Double minimum = this.getMinOfCandles(candles);
        Double maximum = this.getMaxOfCandles(candles);
        Double height = (maximum - minimum) / initNumberOfLevels;
        for (int i = 0; i < initNumberOfLevels; i++) {
            Level level = new Level();
            level.setFrom(minimum + (i * height));
            level.setTo(minimum + ((i+1) * height) );
            levels.add(level);
        }
        levels.forEach(level -> {
            Integer count = 0;
            for (int i = 0; i < candles.size(); i++) {
                Double min = tradingDAO.getMin(candles.get(i));
                Double max = tradingDAO.getMax(candles.get(i));
                if (max  >= level.getFrom() && min <= level.getFrom() || max  >= level.getTo() && min <= level.getTo()) {
                    count++;
                }
            }
            level.setCount(count);
        });
        return levels.stream().sorted((l1, l2) -> Integer.compare(l2.getCount(), l1.getCount())).collect(Collectors.toList()).subList(0, filterNumbers);
    }

    private Double getMinOfCandles(List<Candle> candles) {
        Double min = Double.MAX_VALUE;
        for (Candle candle: candles) {
            if (tradingDAO.getMin(candle) < min) {
                min = tradingDAO.getMin(candle);
            }
        }
        return min;
    }

    private Double getMaxOfCandles(List<Candle> candles) {
        Double max = Double.MIN_VALUE;
        for (Candle candle: candles) {
            if (tradingDAO.getMax(candle) > max) {
                max = tradingDAO.getMax(candle);
            }
        }
        return max;
    }
}
