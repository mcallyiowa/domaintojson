package com.domaintojson;

import java.util.ArrayList;
import java.util.List;

public class SqlTable {
    private  String tableName;
    private List<String> fields = new ArrayList<String>();
    public SqlTable(String tableName) {
        this.tableName = tableName;
    }
    public void add(String field) { fields.add(field); }
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
