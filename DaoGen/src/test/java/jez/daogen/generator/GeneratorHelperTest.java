package jez.daogen.generator;


import lombok.extern.java.Log;
import org.junit.Test;

import java.sql.Types;

import static org.junit.Assert.assertEquals;

@Log
public class GeneratorHelperTest {
    @Test
    public void testGetFieldType() {
        assertEquals("String", GeneratorHelper.getFieldType(Types.CHAR));
        assertEquals("String", GeneratorHelper.getFieldType(Types.VARCHAR));
        assertEquals("String", GeneratorHelper.getFieldType(Types.LONGVARCHAR));
        assertEquals("String", GeneratorHelper.getFieldType(Types.CLOB));
        assertEquals("Object", GeneratorHelper.getFieldType(Types.OTHER));
        assertEquals("java.sql.Timestamp", GeneratorHelper.getFieldType(Types.TIMESTAMP));
        assertEquals("java.math.BigDecimal", GeneratorHelper.getFieldType(Types.DECIMAL));
        assertEquals("Integer", GeneratorHelper.getFieldType(Types.INTEGER));
        assertEquals("Double", GeneratorHelper.getFieldType(Types.DOUBLE));
    }

    @Test
    public void testGetSetParameterTypeName() {
        assertEquals("String", GeneratorHelper.getSetParameterTypeName(Types.CHAR));
        assertEquals("String", GeneratorHelper.getSetParameterTypeName(Types.VARCHAR));
        assertEquals("String", GeneratorHelper.getSetParameterTypeName(Types.LONGVARCHAR));
        assertEquals("String", GeneratorHelper.getSetParameterTypeName(Types.CLOB));
        assertEquals("Object", GeneratorHelper.getSetParameterTypeName(Types.OTHER));
        assertEquals("Timestamp", GeneratorHelper.getSetParameterTypeName(Types.TIMESTAMP));
        assertEquals("BigDecimal", GeneratorHelper.getSetParameterTypeName(Types.DECIMAL));
        assertEquals("Int", GeneratorHelper.getSetParameterTypeName(Types.INTEGER));
        assertEquals("Double", GeneratorHelper.getSetParameterTypeName(Types.DOUBLE));
    }

    @Test
    public void testGetFieldName() {
        assertEquals("nameValue", GeneratorHelper.getFieldName("name_value"));
        assertEquals("name", GeneratorHelper.getFieldName("name"));
        assertEquals("nameValue", GeneratorHelper.getFieldName("Name_value"));
    }

    @Test
    public void testGetClassName() {
        assertEquals("NameValue", GeneratorHelper.getClassName("name_value"));
        assertEquals("Name", GeneratorHelper.getClassName("name"));
        assertEquals("NameValue", GeneratorHelper.getClassName("Name_value"));
    }
    @Test
    public void testGetFullDirName() {
        assertEquals("/devl/projects/java/jez/DaoGen/src/test/java/jez/app/dao", GeneratorHelper.getFullDirName("/devl/projects/java/jez/DaoGen/src/test/java", "jez.app.dao"));
    }
    @Test
    public void testGetFullFileName() {
        assertEquals("/devl/projects/java/jez/DaoGen/src/test/java/jez/app/dao/BaseDao.java", GeneratorHelper.getFullFileName("/devl/projects/java/jez/DaoGen/src/test/java", "jez.app.dao", "BaseDao.java"));
    }
}