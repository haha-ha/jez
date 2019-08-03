package jez.daogen;

import lombok.extern.java.Log;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;

@Configuration
@Log
public class AppContext {
    @Bean(name = "dsDao")
    @ConfigurationProperties(prefix = "dao.datasource")
    @Primary
    @Scope("prototype")
    public DataSource daoDataSource() {
        return DataSourceBuilder.create().type(SingleConnectionDataSource.class).build();
    }
}