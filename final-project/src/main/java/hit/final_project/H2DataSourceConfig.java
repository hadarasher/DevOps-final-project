package hit.final_project;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;

@Configuration
@Profile("h2")
public class H2DataSourceConfig {

    @Bean
    @ConfigurationProperties("spring.datasource2")
    public DataSource h2DataSource() {
        return DataSourceBuilder.create().build();
    }
}
