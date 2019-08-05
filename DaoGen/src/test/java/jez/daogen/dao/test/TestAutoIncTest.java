package jez.daogen.dao.test;

import jez.app.dao.TestAutoIncDao;
import jez.app.dao.TestAutoIncData;
import lombok.extern.java.Log;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log
public class TestAutoIncTest {
    @Autowired
    @Qualifier("dsDao")
    DataSource dataSource;

    @Test
    public void testAutoInc() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        TestAutoIncDao dao = new TestAutoIncDao();
        dao.delete(jdbcTemplate, " where name like ?", "test_%");
        TestAutoIncData data = new TestAutoIncData();
        data.setName("test_name");
        data.setDescription("test_description");
        List<Integer> lst = new ArrayList<>();
        for (int i = 0; i < 1000; i ++){
            lst.add(i);
        }
        lst.parallelStream().forEach( idx -> {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(dataSource);
            Number id = dao.insertAndReturnId(simpleJdbcInsert, data);
            log.info("Auto id: " + id);
            Assert.assertTrue(id.intValue() >= 0);
        });
    }
}