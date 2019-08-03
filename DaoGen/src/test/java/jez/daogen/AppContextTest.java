package jez.daogen;

import lombok.extern.java.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log
public class AppContextTest {
    @Autowired
    @Qualifier("dsDao")
    DataSource dataSource;
    @Test
    public void testDataSource() throws Exception {
        log.info("DAO DataSource: " + dataSource);
        if (dataSource != null) {
            log.info("DAO DataSource: " + dataSource.getConnection());
        }
    }
}