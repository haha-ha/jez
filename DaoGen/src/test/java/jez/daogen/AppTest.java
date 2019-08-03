package jez.daogen;

import lombok.extern.java.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log
public class AppTest {
    @Test
    public void testMain() throws Exception {
        String[] args = {};
        App.main(args);
    }
}