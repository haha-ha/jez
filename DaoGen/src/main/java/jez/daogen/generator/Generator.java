package jez.daogen.generator;

import lombok.Cleanup;
import lombok.extern.java.Log;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@Component
@Log
public class Generator {
    @Autowired
    private Environment env;
    @Autowired
    @Qualifier("dsDao")
    DataSource dataSource;

    public void generate() throws Exception{
        String schema = env.getProperty("dao.gen.schema");
        String tables = env.getProperty("dao.gen.table.list");
        log.info("Table Match String: " + tables);
        List<String> matchList = new ArrayList<>();
        Set<String> tableList = new TreeSet<>();
        if (tables != null) {
            for (String table : tables.split(", ")) {
                table = table.replace("*", "(.*)");
                table = table.replace("?", "(.)");
                matchList.add(table);
            }
        }
        log.info("Match Reg List:" + matchList);
        List<String> dbTables = getTableList(schema);
        log.info("Database Table List:" + dbTables);
        for (String table: dbTables) {
            if (matchList.size() <=0) {
                tableList.add(table);
            }
            else {
                for (String match:matchList) {
                    if (table.matches(match)) {
                        tableList.add(table);
                    }
                }
            }
        }
        log.info("Dao Generation Table List:" + tableList);
        String packageName = env.getProperty("dao.gen.package");
        String srcDir = env.getProperty("dao.gen.src");
        tableList.parallelStream().forEach( table -> {
            try {
                generate(schema, table, packageName, srcDir);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }

    protected List<String> getTableList(String schema) throws SQLException {
        List<String> rcTables = new ArrayList<>();
        Connection conn = dataSource.getConnection();
        DatabaseMetaData metaData = conn.getMetaData();
        @Cleanup ResultSet tableRs = metaData.getTables(schema, null, null, null);

        while (tableRs.next()) {
            rcTables.add(tableRs.getString("TABLE_NAME").trim());
        }
        return rcTables;
    }

    public void generate(String schema, String table, String packageName, String srcDir) throws Exception {
        Connection conn = dataSource.getConnection();
        DatabaseMetaData metaData = conn.getMetaData();
        @Cleanup ResultSet colRs = metaData.getColumns(schema, null, table, null);
        //GeneratorHelper.dump(colRs);
        List<ColInfo> colInfoList = new ArrayList<>();
        while (colRs.next()) {
            ColInfo info = new ColInfo();
            info.setDbName(colRs.getString("COLUMN_NAME").trim());
            info.setDbType(colRs.getInt("DATA_TYPE"));
            colInfoList.add(info);
        }
        String dir = GeneratorHelper.getFullDirName(srcDir, packageName);
        (new File(dir)).mkdirs();
        generateDataClass(schema, table, packageName, srcDir, colInfoList);
        generateBaseDaoClass(schema, table, packageName, srcDir, colInfoList);
        generateDaoClass(schema, table, packageName, srcDir, colInfoList);
    }

    public void generateDataClass(String schema, String table, String packageName, String srcDir, List<ColInfo> colInfoList) throws Exception{
        String fileName = GeneratorHelper.getFullFileName(srcDir, packageName, GeneratorHelper.getClassName(table) + "Data.java");
        log.info("creating " + fileName);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("package %s;\n\n", packageName));
        sb.append("import lombok.Builder;\n");
        sb.append("import lombok.Data;\n\n");
        sb.append("@Data\n");
        sb.append(String.format("public class %sData {\n", GeneratorHelper.getClassName(table)));
        for (ColInfo info: colInfoList) {
            sb.append(String.format("\tprotected %s %s;\n", GeneratorHelper.getFieldType(info.getDbType()), GeneratorHelper.getFieldName(info.getDbName())));
        }
        sb.append("}\n");
        IOUtils.write(sb.toString().getBytes(), new FileOutputStream(fileName));
    }

    public void generateBaseDaoClass(String schema, String table, String packageName, String srcDir, List<ColInfo> colInfoList) throws Exception{
        String className = GeneratorHelper.getClassName(table);
        String fileName = GeneratorHelper.getFullFileName(srcDir, packageName, className + "BaseDao.java");
        log.info("creating " + fileName);
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("package %s;\n\n", packageName));
        sb.append("import org.springframework.jdbc.core.BatchPreparedStatementSetter;\n");
        sb.append("import org.springframework.jdbc.core.JdbcTemplate;\n");
        sb.append("import org.springframework.jdbc.core.simple.SimpleJdbcInsert;\n");
        sb.append("import org.springframework.jdbc.core.RowMapper;\n\n");
        sb.append("import java.sql.PreparedStatement;\n");
        sb.append("import java.sql.ResultSet;\n");
        sb.append("import java.sql.SQLException;\n");
        sb.append("import java.util.ArrayList;\n");
        sb.append("import java.util.Arrays;\n");
        sb.append("import java.util.List;\n\n");
        sb.append("import java.util.HashMap;\n\n");
        sb.append("import java.util.Map;\n\n");

        sb.append(String.format("public class %sBaseDao implements RowMapper<%sData> {\n", className, className));

        // mapRow function
        sb.append("\t@Override\n");
        sb.append(String.format("\tpublic %sData mapRow(ResultSet rs, int rowNum) throws SQLException {\n", className));
        sb.append(String.format("\t\t%sData data = new %sData();\n", className, className));
        for (ColInfo info: colInfoList) {
            String typeName = GeneratorHelper.getSetParameterTypeName(info.getDbType());
            if ("Int".equals(typeName)) {
                sb.append(String.format("\t\tdata.set%s((Integer)rs.getObject(\"%s\"));\n",
                        GeneratorHelper.getClassName(info.getDbName()),
                        info.getDbName()));
            }
            else {
                sb.append(String.format("\t\tdata.set%s(rs.get%s(\"%s\"));\n",
                        GeneratorHelper.getClassName(info.getDbName()),
                        typeName,
                        info.getDbName()));
            }
        }
        sb.append("\t\treturn data;\n");
        sb.append("\t}\n\n");

        // query function
        sb.append(String.format("\tpublic List<%sData> query(JdbcTemplate jdbcTemplate, String where, Object... params) {\n", className));
        sb.append("\t\twhere = (where==null)?\"\":where;\n");
        sb.append(String.format("\t\treturn jdbcTemplate.query(\"select * from %s \" + where, this, params);\n", table));
        sb.append("\t}\n\n");

        // queryOne function
        sb.append(String.format("\tpublic %sData queryOne(JdbcTemplate jdbcTemplate, String where, Object... params) {\n", className));
        sb.append("\t\twhere = (where==null)?\"\":where;\n");
        sb.append(String.format("\t\treturn jdbcTemplate.queryForObject(\"select * from %s \" + where, this, params);\n", table));
        sb.append("\t}\n\n");

        // insert function
        sb.append(String.format("\tpublic int insert(JdbcTemplate jdbcTemplate, %sData data) {\n", className, className));
        StringBuilder sbCols = new StringBuilder();
        StringBuilder sbValuls = new StringBuilder();
        StringBuilder sbParams = new StringBuilder();
        for (int i = 0; i < colInfoList.size(); i ++) {
            ColInfo info = colInfoList.get(i);
            if (i != 0){
                sbCols.append(", ");
                sbValuls.append(", ");
                sbParams.append(", ");
            }
            sbCols.append(info.getDbName());
            sbValuls.append("?");
            sbParams.append(String.format("data.get%s()", GeneratorHelper.getClassName(info.getDbName())));
        }
        sb.append(String.format("\t\treturn jdbcTemplate.update(\"insert into %s (%s) values (%s)\", %s);\n"
                    , table, sbCols.toString(), sbValuls.toString(), sbParams.toString()));
        sb.append("\t}\n\n");

        // insert and return key
        sb.append(String.format("\tpublic Number insertAndReturnId(SimpleJdbcInsert simpleJdbcInsert, %sData data) {\n", className));
        sb.append("\t\tMap<String, Object> parameters = new HashMap<>();\n");
        StringBuilder sbInsertKeyCols = new StringBuilder();
        for (int i = 0; i < colInfoList.size(); i ++) {
            ColInfo info = colInfoList.get(i);
            if (!"id".equalsIgnoreCase(info.getDbName())){
                if (sbInsertKeyCols.length() > 0) {
                    sbInsertKeyCols.append(", ");
                }
                sb.append(String.format("\t\tparameters.put(\"%s\", data.get%s());\n", info.getDbName(),
                        GeneratorHelper.getClassName(info.getDbName())));
                sbInsertKeyCols.append(String.format("\"%s\"", info.getDbName()));
            }
        }
        sb.append(String.format("\t\treturn simpleJdbcInsert.withTableName(\"%s\")\n", table));
        sb.append("\t\t\t.usingGeneratedKeyColumns(\"id\")\n");
        sb.append(String.format("\t\t\t.usingColumns(%s)\n", sbInsertKeyCols.toString()));
        sb.append("\t\t\t.executeAndReturnKey(parameters);\n");
        sb.append("\t}\n\n");

        // insertBatch function
        sb.append(String.format("\tpublic int[] insertBatch(JdbcTemplate jdbcTemplate, List<%sData> listData) {\n", className));
        sb.append(String.format("\t\t%sBatchSetter batchSetter = new %sBatchSetter(listData);\n", className, className));
        sb.append(String.format("\t\treturn jdbcTemplate.batchUpdate(\"insert into %s (%s) values (%s)\", batchSetter);\n"
                ,table, sbCols.toString(), sbValuls.toString()));
        sb.append("\t}\n\n");

        // update function
        sb.append(String.format("\tpublic int update(JdbcTemplate jdbcTemplate, %sData data, String where, Object... params) {\n", className));
        sb.append("\t\twhere = (where==null)?\"\":where;\n");
        sb.append("\t\tList<Object> args = new ArrayList<>();\n");
        StringBuilder sbSet = new StringBuilder();
        for (int i = 0; i < colInfoList.size(); i ++) {
            ColInfo info = colInfoList.get(i);
            if (i != 0){
                sbSet.append(", ");
            }
            sbSet.append(String.format("%s = ?", info.getDbName()));
            sb.append(String.format("\t\targs.add(data.get%s());\n", GeneratorHelper.getClassName(info.getDbName())));
        }
        sb.append("\t\targs.addAll(Arrays.asList(params));\n");
        sb.append(String.format("\t\treturn jdbcTemplate.update(\"update %s set %s \" + where, args.toArray(new Object[0]));\n",
                table, sbSet.toString()));
        sb.append("\t}\n\n");

        // delete function
        sb.append("\tpublic int delete(JdbcTemplate jdbcTemplate, String where, Object... param) {\n");
        sb.append(String.format("\t\treturn jdbcTemplate.update(\"delete from %s \" + where, param);\n", table));
        sb.append("\t}\n");

        // end of the class
        sb.append("}\n");

        // BatchSetter class
        sb.append(String.format("class %sBatchSetter implements BatchPreparedStatementSetter {\n", className));
        sb.append(String.format("\tList<%sData> dataList;\n\n", className));
        sb.append(String.format("\t%sBatchSetter(List<%sData> dataList){\n", className, className));
        sb.append("\t\tthis.dataList = dataList;\n");
        sb.append("\t}\n\n");

        // batch.setValues function
        sb.append("\tpublic void setValues(PreparedStatement ps, int i) throws SQLException {\n");
        sb.append(String.format("\t\t%sData data = dataList.get(i);\n", className));
        for (int i = 0; i < colInfoList.size(); i ++) {
            ColInfo info = colInfoList.get(i);
            sb.append(String.format("\t\tps.set%s(%d, data.get%s());\n",
                    GeneratorHelper.getSetParameterTypeName(info.getDbType()),
                    (i + 1),
                    GeneratorHelper.getClassName(info.getDbName())));
        }
        sb.append("\t}\n\n");

        sb.append("\t@Override\n");
        sb.append("\tpublic int getBatchSize() {\n");
        sb.append("\t\treturn dataList.size();\n");
        sb.append("\t}\n");

        sb.append("}\n");
        // save to file
        IOUtils.write(sb.toString().getBytes(), new FileOutputStream(fileName));
    }

    public void generateDaoClass(String schema, String table, String packageName, String srcDir, List<ColInfo> colInfoList) throws Exception{
        String fileName = GeneratorHelper.getFullFileName(srcDir, packageName, GeneratorHelper.getClassName(table) + "Dao.java");
        File f = new File(fileName);
        if (f.exists()) {
            log.info(fileName + " exists, will not overwrite");
        }
        else {
            log.info("creating " + fileName);
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("package %s;\n\n", packageName));
            sb.append(String.format("public class %sDao extends %sBaseDao {\n", GeneratorHelper.getClassName(table), GeneratorHelper.getClassName(table)));
            sb.append("}\n");
            IOUtils.write(sb.toString().getBytes(), new FileOutputStream(fileName));
        }
    }
}
