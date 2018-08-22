package resources.spring;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jndi.JndiObjectFactoryBean;

@Configuration
public class ComplexDataSourceConfig {

	@Bean(destroyMethod="shutdown")
	@Profile("dev")
	public DataSource datasourceEmbedded() {
	    return new EmbeddedDatabaseBuilder()
	    .addScript("classpath:schema.sql")
	    .addScript("classpath:test-data.sql")
	    .build();
	}
	
	@Bean
	@Profile("prod")
	public DataSource datasourceJndi() {
	    JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
	    jndiObjectFactoryBean.setJndiName("jdbc/myDs");
	    jndiObjectFactoryBean.setResourceRef(true);
	    jndiObjectFactoryBean.setProxyInterface(javax.sql.DataSource.class);
	    return (DataSource) jndiObjectFactoryBean.getObject();
	}
	
}
