package jez.app.dao;

import lombok.Builder;
import lombok.Data;

@Data
public class SeqIdData {
	protected String name;
	protected java.math.BigDecimal nextVal;
}
