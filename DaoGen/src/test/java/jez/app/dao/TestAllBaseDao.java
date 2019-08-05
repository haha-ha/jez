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

public class TestAllBaseDao implements RowMapper<TestAllData> {
	@Override
	public TestAllData mapRow(ResultSet rs, int rowNum) throws SQLException {
		TestAllData data = new TestAllData();
		data.setId((Integer)rs.getObject("id"));
		data.setName(rs.getString("name"));
		data.setDescription(rs.getString("description"));
		data.setAge((Integer)rs.getObject("age"));
		data.setDob(rs.getTimestamp("dob"));
		data.setIncome(rs.getBigDecimal("income"));
		data.setHistory(rs.getString("history"));
		return data;
	}

	public List<TestAllData> query(JdbcTemplate jdbcTemplate, String where, Object... params) {
		where = (where==null)?"":where;
		return jdbcTemplate.query("select * from TestAll " + where, this, params);
	}

	public TestAllData queryOne(JdbcTemplate jdbcTemplate, String where, Object... params) {
		where = (where==null)?"":where;
		return jdbcTemplate.queryForObject("select * from TestAll " + where, this, params);
	}

	public int insert(JdbcTemplate jdbcTemplate, TestAllData data) {
		return jdbcTemplate.update("insert into TestAll (id, name, description, age, dob, income, history) values (?, ?, ?, ?, ?, ?, ?)", data.getId(), data.getName(), data.getDescription(), data.getAge(), data.getDob(), data.getIncome(), data.getHistory());
	}

	public Number insertAndReturnId(SimpleJdbcInsert simpleJdbcInsert, TestAllData data) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("name", data.getName());
		parameters.put("description", data.getDescription());
		parameters.put("age", data.getAge());
		parameters.put("dob", data.getDob());
		parameters.put("income", data.getIncome());
		parameters.put("history", data.getHistory());
		return simpleJdbcInsert.withTableName("TestAll")
			.usingGeneratedKeyColumns("id")
			.usingColumns("name", "description", "age", "dob", "income", "history")
			.executeAndReturnKey(parameters);
	}

	public int[] insertBatch(JdbcTemplate jdbcTemplate, List<TestAllData> listData) {
		TestAllBatchSetter batchSetter = new TestAllBatchSetter(listData);
		return jdbcTemplate.batchUpdate("insert into TestAll (id, name, description, age, dob, income, history) values (?, ?, ?, ?, ?, ?, ?)", batchSetter);
	}

	public int update(JdbcTemplate jdbcTemplate, TestAllData data, String where, Object... params) {
		where = (where==null)?"":where;
		List<Object> args = new ArrayList<>();
		args.add(data.getId());
		args.add(data.getName());
		args.add(data.getDescription());
		args.add(data.getAge());
		args.add(data.getDob());
		args.add(data.getIncome());
		args.add(data.getHistory());
		args.addAll(Arrays.asList(params));
		return jdbcTemplate.update("update TestAll set id = ?, name = ?, description = ?, age = ?, dob = ?, income = ?, history = ? " + where, args.toArray(new Object[0]));
	}

	public int delete(JdbcTemplate jdbcTemplate, String where, Object... param) {
		return jdbcTemplate.update("delete from TestAll " + where, param);
	}
}
class TestAllBatchSetter implements BatchPreparedStatementSetter {
	List<TestAllData> dataList;

	TestAllBatchSetter(List<TestAllData> dataList){
		this.dataList = dataList;
	}

	public void setValues(PreparedStatement ps, int i) throws SQLException {
		TestAllData data = dataList.get(i);
		ps.setInt(1, data.getId());
		ps.setString(2, data.getName());
		ps.setString(3, data.getDescription());
		ps.setInt(4, data.getAge());
		ps.setTimestamp(5, data.getDob());
		ps.setBigDecimal(6, data.getIncome());
		ps.setString(7, data.getHistory());
	}

	@Override
	public int getBatchSize() {
		return dataList.size();
	}
}
