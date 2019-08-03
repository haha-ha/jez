package jez.daogen.dao.test;

import jez.app.dao.SeqIdDao;
import jez.app.dao.SeqIdData;
import lombok.extern.java.Log;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@Log
public class SeqIdDaoTest {
    @Autowired
    @Qualifier("dsDao")
    DataSource dataSource;

    @Test
    public void testQuery() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        SeqIdDao dao = getDao();
        List<SeqIdData> dataList = dao.query(jdbcTemplate, null);
        log.info("All Data:" + dataList);
        dataList = dao.query(jdbcTemplate, "where name in (?, ?) order by name", "sys", "test");
        log.info("2 Data:" + dataList);
        SeqIdData data = dao.queryOne(jdbcTemplate, "where name in (?)", "sys");
        log.info("1 Data:" + data);
    }

    protected SeqIdDao getDao() {
        return new SeqIdDao();
    }

    @Test
    public void testInsert() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        SeqIdDao dao = getDao();
        SeqIdData data = new SeqIdData();
        data.setName("test_name");
        data.setNextVal(new BigDecimal(2001));
        dao.delete(jdbcTemplate, "where name like ?", "test_%");
        dao.insert(jdbcTemplate, data);
        SeqIdData data1 = dao.queryOne(jdbcTemplate, "where name like ?", "test_%");
        log.info("data after insert: " + data1);
        assertEquals(data, data1);
    }

    @Test
    public void testBatchInsert() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        SeqIdDao dao = getDao();
        dao.delete(jdbcTemplate, "where name like ?", "testbatch_name_%");

        List<SeqIdData> listCreat = new ArrayList<>();
        for (int i = 1; i < 10; i ++) {
            SeqIdData data = new SeqIdData();
            data.setName("testbatch_name_" + i);
            data.setNextVal(new BigDecimal(2001 + i));
            listCreat.add(data);
        }

        dao.insertBatch(jdbcTemplate, listCreat);
        List<SeqIdData> listRc = dao.query(jdbcTemplate, "where name like ? order by name", "testbatch_name_%");
        log.info("data after insert: " + listRc);
        assertEquals(listCreat, listRc);
    }

    @Test
    public void testUpdate() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        SeqIdDao dao = getDao();
        SeqIdData data = new SeqIdData();
        data.setName("test_name");
        data.setNextVal(new BigDecimal(2001));
        dao.delete(jdbcTemplate, "where name like ?", "test_%");
        dao.insert(jdbcTemplate, data);
        SeqIdData data1 = dao.queryOne(jdbcTemplate, "where name like ?", "test_%");
        log.info("data after insert: " + data1);
        assertEquals(data, data1);
        data.setNextVal(new BigDecimal(3001));
        dao.update(jdbcTemplate, data, "where name like ?", "test_%");
        data1 = dao.queryOne(jdbcTemplate, "where name like ?", "test_%");
        log.info("data after update: " + data1);
        assertEquals(data, data1);
    }
}