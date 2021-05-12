package idatt2105.backend.Configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * Configuration for Database
 */
@Configuration
@PropertySource("classpath:config.properties")
public class DatabaseConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseConfiguration.class);

    @Value("${database.url}")
    private String databaseUrl;
    @Value("${database.username}")
    private String databaseUsername;
    @Value("${database.password}")
    private String databasePassword;

    /**
     * Creates a MySQL datasource
     * using url, password and username from config.properties file
     * @return DataSource object
     */
    @Bean
    @Profile("dev")
    public DataSource getDataSource() {
        DriverManagerDataSource dataSourceBuilder = new DriverManagerDataSource();
        dataSourceBuilder.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSourceBuilder.setUrl(databaseUrl);
        dataSourceBuilder.setUsername(databaseUsername);
        dataSourceBuilder.setPassword(databasePassword);
        return dataSourceBuilder;
    }
}

