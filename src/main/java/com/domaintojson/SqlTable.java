package com.domaintojson;

import java.util.ArrayList;
import java.util.List;

public class SqlTable {
    private  String tableName;
    private List<SqlColumn> fields = new ArrayList<SqlColumn>();
    public SqlTable(String tableName) {
        this.tableName = tableName;
    }
    public void add(SqlColumn field) { fields.add(field); }
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
