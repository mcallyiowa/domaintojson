package com.domaintojson;

import org.springframework.batch.item.file.FlatFileFooterCallback;

import java.io.IOException;
import java.io.Writer;

public class JsonFlatFileFooterCallback implements FlatFileFooterCallback {

    @Override
    public void writeFooter(final Writer writer) throws IOException {
        writer.write("]");
    }
}