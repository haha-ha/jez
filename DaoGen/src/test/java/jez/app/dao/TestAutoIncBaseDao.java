package jez.app.dao;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.core.RowMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import java.util.HashMap;

import java.util.Map;

public class TestAutoIncBaseDao implements RowMapper<TestAutoIncData> {
	@Override
	public TestAutoIncData mapRow(ResultSet rs, int rowNum) throws SQLException {
		TestAutoIncData data = new TestAutoIncData();
		data.setId((Integer)rs.getObject("id"));
		data.setName(rs.getString("name"));
		data.setDescription(rs.getString("description"));
		return data;
	}

	public List<TestAutoIncData> query(JdbcTemplate jdbcTemplate, String where, Object... params) {
		where = (where==null)?"":where;
		return jdbcTemplate.query("select * from TestAutoInc " + where, this, params);
	}

	public TestAutoIncData queryOne(JdbcTemplate jdbcTemplate, String where, Object... params) {
		where = (where==null)?"":where;
		return jdbcTemplate.queryForObject("select * from TestAutoInc " + where, this, params);
	}

	public int insert(JdbcTemplate jdbcTemplate, TestAutoIncData data) {
		return jdbcTemplate.update("insert into TestAutoInc (id, name, description) values (?, ?, ?)", data.getId(), data.getName(), data.getDescription());
	}

	public Number insertAndReturnId(SimpleJdbcInsert simpleJdbcInsert, TestAutoIncData data) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("name", data.getName());
		parameters.put("description", data.getDescription());
		return simpleJdbcInsert.withTableName("TestAutoInc")
			.usingGeneratedKeyColumns("id")
			.usingColumns("name", "description")
			.executeAndReturnKey(parameters);
	}

	public int[] insertBatch(JdbcTemplate jdbcTemplate, List<TestAutoIncData> listData) {
		TestAutoIncBatchSetter batchSetter = new TestAutoIncBatchSetter(listData);
		return jdbcTemplate.batchUpdate("insert into TestAutoInc (id, name, description) values (?, ?, ?)", batchSetter);
	}

	public int update(JdbcTemplate jdbcTemplate, TestAutoIncData data, String where, Object... params) {
		where = (where==null)?"":where;
		List<Object> args = new ArrayList<>();
		args.add(data.getId());
		args.add(data.getName());
		args.add(data.getDescription());
		args.addAll(Arrays.asList(params));
		return jdbcTemplate.update("update TestAutoInc set id = ?, name = ?, description = ? " + where, args.toArray(new Object[0]));
	}

	public int delete(JdbcTemplate jdbcTemplate, String where, Object... param) {
		return jdbcTemplate.update("delete from TestAutoInc " + where, param);
	}
}
class TestAutoIncBatchSetter implements BatchPreparedStatementSetter {
	List<TestAutoIncData> dataList;

	TestAutoIncBatchSetter(List<TestAutoIncData> dataList){
		this.dataList = dataList;
	}

	public void setValues(PreparedStatement ps, int i) throws SQLException {
		TestAutoIncData data = dataList.get(i);
		ps.setInt(1, data.getId());
		ps.setString(2, data.getName());
		ps.setString(3, data.getDescription());
	}

	@Override
	public int getBatchSize() {
		return dataList.size();
	}
}
