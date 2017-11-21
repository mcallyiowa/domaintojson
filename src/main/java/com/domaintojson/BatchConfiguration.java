package com.domaintojson;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.transform.PassThroughLineAggregator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {
    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public DataSource dataSource;

    private static final Logger log = LoggerFactory.getLogger(BatchConfiguration.class);


    @Bean
    JdbcCursorItemReader<String> getTableNamesReader() {
        JdbcCursorItemReader<String> itemReader = new JdbcCursorItemReader<String>();
        itemReader.setDataSource(dataSource);
        itemReader.setSql("SHOW TABLES");
        itemReader.setRowMapper(new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet rs, int rowNum) throws SQLException {
                String sqlTable = rs.getString(1);
                return sqlTable;

            }
        });
        return itemReader;
    }

    @Bean
    public FlatFileItemReader<SqlTable> createTableReader() {
        FlatFileItemReader<SqlTable> itemReader = new FlatFileItemReader<SqlTable>();
        itemReader.setResource(new FileSystemResource(new File("target/out-jsonish.txt")));
        itemReader.setLineMapper(new LineMapper<SqlTable>() {
            @Override
            public SqlTable mapLine(String line, int lineNumber) throws Exception {
                JdbcTemplate jdbcTemplate = new JdbcTemplate();
                jdbcTemplate.setDataSource(dataSource);
                SqlRowSet rowSet = jdbcTemplate.queryForRowSet("DESC " + line);
                SqlTable sqlTable = new SqlTable(line);
                while(rowSet.next()) {
                    SqlColumn sqlColumn = new SqlColumn();
                    sqlColumn.setColumnName(rowSet.getString("Field"));
                    sqlColumn.setColumnType(rowSet.getString("Type"));
                    if(rowSet.getString("Key") != null && rowSet.getString("Key").equals("PRI")) {
                        sqlColumn.setPrimaryKey(true);
                    }
                    sqlTable.add(sqlColumn);
                }
                return sqlTable;
            }
        });
        return itemReader;
    }

    @Bean
    public SqlTableProcessor getTableNamesProcessor() {
        return new SqlTableProcessor();
    }
    @Bean
    public JsonObjectProcessor processor() { return new JsonObjectProcessor(); }

    @Bean
    public ItemWriter<String> getTableNamesWriter() {
        FlatFileItemWriter<String> writer = new FlatFileItemWriter<String>();
        writer.setResource(new FileSystemResource(new File("target/out-jsonish.txt")));
        writer.setLineAggregator(new PassThroughLineAggregator<String>());
        return writer;
    }
    @Bean
    public ItemWriter<String> writer() {
        FlatFileItemWriter<String> writer = new FlatFileItemWriter<String>();
        writer.setResource(new FileSystemResource(new File("target/out-json.json")));
        writer.setLineAggregator(new PassThroughLineAggregator<String>());
        return writer;
    }
    @Bean
    public Job importUserJob(JobCompletionNotificationListener listener) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(step1())
                .next(step2())
                .build();
    }

    @Bean
    public Step step1() {
        return stepBuilderFactory.get("step1")
                .<String, String> chunk(1)
                .reader(getTableNamesReader())
                .processor(getTableNamesProcessor())
                .writer(getTableNamesWriter())
                .build();
    }
    @Bean
    public Step step2() {
        return stepBuilderFactory.get("step2")
                .<SqlTable, String> chunk(1)
                .reader(createTableReader())
                .processor(processor())
                .writer(writer())
                .build();
    }

 }
