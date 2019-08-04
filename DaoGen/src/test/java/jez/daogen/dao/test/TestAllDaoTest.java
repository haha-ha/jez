package jez.daogen.dao.test;


import jez.app.dao.TestAllDao;
import jez.app.dao.TestAllData;
import lombok.extern.java.Log;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log
public class TestAllDaoTest {
    @Autowired
    @Qualifier("dsDao")
    DataSource dataSource;

    @Test
    public void testGetNullValue() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        TestAllDao dao = new TestAllDao();
        TestAllData data = dao.queryOne(jdbcTemplate, "where id=?", 2);
        log.info("data with null value" + data);
        Assert.assertEquals(null, data.getAge());
    }

    @Test
    public void testGet() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        TestAllDao dao = new TestAllDao();
        List<TestAllData> list = dao.query(jdbcTemplate, "where id=?", 1);
        log.info("data:" + list);
        log.info("don't be suprised by the above docker message. tt is part of the return data");
    }

    @Test
    public void testInsertBatch() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        TestAllDao dao = new TestAllDao();
        dao.delete(jdbcTemplate, "where name like ?", "test_%");
        List<TestAllData> dataList = new ArrayList<>();
        for (int i = 10000; i < 100000; i ++) {
            TestAllData data = new TestAllData();
            data.setId(i);
            data.setName("test_" + i);
            data.setAge(i % 100 + 10);
            data.setDescription("test batch insert");
            data.setDob(new Timestamp(System.currentTimeMillis()));
            data.setIncome(new BigDecimal(i));
            data.setHistory("hi hi");
            dataList.add(data);
        }
        int[] rc = dao.insertBatch(jdbcTemplate, dataList);
        for (int v:rc) {
            System.out.print(v);
            if (v != 1) {
                System.out.print("\n***********" + v + "*************\n");
            }
        }
    }

    @Test
    public void testInsertLoop() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        TestAllDao dao = new TestAllDao();
        dao.delete(jdbcTemplate, "where name like ?", "test_%");
        for (int i = 1000; i < 2000; i ++) {
            TestAllData data = new TestAllData();
            data.setId(i);
            data.setName("test_" + i);
            data.setAge(i % 100 + 10);
            data.setDescription("test batch insert");
            data.setDob(new Timestamp(System.currentTimeMillis()));
            data.setIncome(new BigDecimal(i));
            data.setHistory("hi hi");
            dao.insert(jdbcTemplate, data);
        }
    }
}
