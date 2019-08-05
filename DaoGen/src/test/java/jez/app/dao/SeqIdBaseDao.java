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

public class SeqIdBaseDao implements RowMapper<SeqIdData> {
	@Override
	public SeqIdData mapRow(ResultSet rs, int rowNum) throws SQLException {
		SeqIdData data = new SeqIdData();
		data.setName(rs.getString("name"));
		data.setNextVal(rs.getBigDecimal("next_val"));
		return data;
	}

	public List<SeqIdData> query(JdbcTemplate jdbcTemplate, String where, Object... params) {
		where = (where==null)?"":where;
		return jdbcTemplate.query("select * from SeqId " + where, this, params);
	}

	public SeqIdData queryOne(JdbcTemplate jdbcTemplate, String where, Object... params) {
		where = (where==null)?"":where;
		return jdbcTemplate.queryForObject("select * from SeqId " + where, this, params);
	}

	public int insert(JdbcTemplate jdbcTemplate, SeqIdData data) {
		return jdbcTemplate.update("insert into SeqId (name, next_val) values (?, ?)", data.getName(), data.getNextVal());
	}

	public Number insertAndReturnId(SimpleJdbcInsert simpleJdbcInsert, SeqIdData data) {
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("name", data.getName());
		parameters.put("next_val", data.getNextVal());
		return simpleJdbcInsert.withTableName("SeqId")
			.usingGeneratedKeyColumns("id")
			.usingColumns("name", "next_val")
			.executeAndReturnKey(parameters);
	}

	public int[] insertBatch(JdbcTemplate jdbcTemplate, List<SeqIdData> listData) {
		SeqIdBatchSetter batchSetter = new SeqIdBatchSetter(listData);
		return jdbcTemplate.batchUpdate("insert into SeqId (name, next_val) values (?, ?)", batchSetter);
	}

	public int update(JdbcTemplate jdbcTemplate, SeqIdData data, String where, Object... params) {
		where = (where==null)?"":where;
		List<Object> args = new ArrayList<>();
		args.add(data.getName());
		args.add(data.getNextVal());
		args.addAll(Arrays.asList(params));
		return jdbcTemplate.update("update SeqId set name = ?, next_val = ? " + where, args.toArray(new Object[0]));
	}

	public int delete(JdbcTemplate jdbcTemplate, String where, Object... param) {
		return jdbcTemplate.update("delete from SeqId " + where, param);
	}
}
class SeqIdBatchSetter implements BatchPreparedStatementSetter {
	List<SeqIdData> dataList;

	SeqIdBatchSetter(List<SeqIdData> dataList){
		this.dataList = dataList;
	}

	public void setValues(PreparedStatement ps, int i) throws SQLException {
		SeqIdData data = dataList.get(i);
		ps.setString(1, data.getName());
		ps.setBigDecimal(2, data.getNextVal());
	}

	@Override
	public int getBatchSize() {
		return dataList.size();
	}
}
