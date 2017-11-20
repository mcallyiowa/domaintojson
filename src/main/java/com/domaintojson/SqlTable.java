package com.domaintojson;

public class SqlTable {
    public String tableName;
    public SqlTable(String tableName) {
        this.tableName = tableName;
    }
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
