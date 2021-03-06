package com.domaintojson;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.batch.item.ItemProcessor;

public class SqlTableProcessor implements ItemProcessor<String, String> {

    private static final Logger log = LoggerFactory.getLogger(SqlTableProcessor.class);

    @Override
    public String process(final String sqlTable) throws Exception {
        Gson gson = new Gson();
        final String transformedObject = sqlTable;
        log.info("Translating to json object + " + transformedObject);
        return transformedObject;
    }

}