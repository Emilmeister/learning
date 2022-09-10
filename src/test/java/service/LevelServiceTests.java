package service;


import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import ru.emil.springwebapp.first.App;
import ru.emil.springwebapp.first.data.pojo.Level;
import ru.emil.springwebapp.first.service.LevelService;
import org.junit.jupiter.api.Test;
import ru.tinkoff.invest.openapi.model.rest.CandleResolution;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ContextConfiguration(classes = App.class)
public class LevelServiceTests {


    @Autowired
    LevelService levelService;

    @DisplayName("Test AAPL levels")
    @Test
    void testGetSpecter() {
        List<Level> levels = levelService.getSpecter("BBG000MM2P62", CandleResolution.DAY, 50, 50);
        System.out.println(levels);
    }

}
