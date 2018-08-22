package resources.spring;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;

@Configuration
@Profile("dev")
public class DevelopmentProfileConfig {
	
	@Bean(destroyMethod="shutdown")
	public DataSource datasourceEmbedded() {
	    return new EmbeddedDatabaseBuilder()
	    .addScript("classpath:schema.sql")
	    .addScript("classpath:test-data.sql")
	    .build();
	}
}
