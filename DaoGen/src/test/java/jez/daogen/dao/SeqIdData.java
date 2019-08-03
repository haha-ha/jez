package jez.daogen.dao;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class SeqIdData {
    protected String name;
    protected BigDecimal nextVal;
}
