package jez.app.dao;

import org.springframework.jdbc.core.simple.SimpleJdbcInsert;

import java.util.HashMap;
import java.util.Map;

public class TestAutoIncDao extends TestAutoIncBaseDao {
    public Number insertAndReturnId(SimpleJdbcInsert simpleJdbcInsert, TestAutoIncData data) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("name", data.getName());
        parameters.put("description", data.getDescription());
        return simpleJdbcInsert.withTableName("TestAutoInc")
                .usingGeneratedKeyColumns("id")
                .usingColumns("name", "description")
                .executeAndReturnKey(parameters);

    }
}
