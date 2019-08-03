package jez.app.dao;

import lombok.Builder;
import lombok.Data;

@Data
public class TestAllData {
	protected Integer id;
	protected String name;
	protected String description;
	protected Integer age;
	protected java.sql.Timestamp dob;
	protected java.math.BigDecimal income;
	protected String history;
}
