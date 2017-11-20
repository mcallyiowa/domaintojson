package com.domaintojson;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class JsonObjectProcessor implements ItemProcessor<SqlTable, String> {
    private static final Logger log = LoggerFactory.getLogger(SqlTableProcessor.class);

    @Override
    public String process(final SqlTable sqlTable) throws Exception {
        Gson gson = new Gson();
        final String transformedObject = gson.toJson(sqlTable);
        log.info("Translating to json object + " + transformedObject);
        return transformedObject;
    }
}
