package jez.daogen.generator;

import org.apache.commons.text.WordUtils;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;
import java.util.HashMap;


public class GeneratorHelper {
    static HashMap<Integer, String> sqlTypeMap = new HashMap<>();
    static {
        sqlTypeMap.put(Types.INTEGER, "Integer");
        sqlTypeMap.put(Types.CHAR, "String");
        sqlTypeMap.put(Types.VARCHAR, "String");
        sqlTypeMap.put(Types.TIMESTAMP, "java.sql.Timestamp"); //DateTime
        sqlTypeMap.put(Types.DECIMAL, "java.math.BigDecimal");
        sqlTypeMap.put(Types.LONGVARCHAR, "String");
        sqlTypeMap.put(Types.CLOB, "String");
        sqlTypeMap.put(Types.DOUBLE, "Double");
    }

    public static String getFieldType(int sqlType) {
        String type = sqlTypeMap.get(sqlType);
        return (type == null)?"Object":type;
    }
    public static String getSetParameterTypeName(int sqlType) {
        String name = getFieldType(sqlType);
        name = name.replace("java.sql.", "").replace("java.math.", "");
        if (name.equals("Integer")) {
            name = "Int";
        }
        return name;
    }

    public static String getClassName(String tableName) {
        tableName = tableName.replace("_", " ");
        tableName = WordUtils.capitalize(tableName);
        tableName = tableName.replace(" ", "");
        return tableName;
    }
    public static String getFieldName(String colName) {
        colName = getClassName(colName);
        return colName.substring(0, 1).toLowerCase() + colName.substring(1);
    }

    public static String getFullFileName(String srcDir, String packageName, String fileName) {
        return getFullDirName(srcDir, packageName) + "/" + fileName;
    }

    public static String getFullDirName(String srcDir, String packageName) {
        return srcDir + "/" + packageName.replace(".", "/");
    }

    public static void dump(ResultSet rs) throws  Exception {
        ResultSetMetaData metaData = rs.getMetaData();
        for (int i = 1; i <= metaData.getColumnCount(); i ++) {
            System.out.print(metaData.getColumnLabel(i) + "\t");
        }
        System.out.println();
        while (rs.next()){
            for (int i = 1; i <= metaData.getColumnCount(); i ++) {
                System.out.print(rs.getObject(i) + "\t");
            }
            System.out.println();
        }
    }
}
