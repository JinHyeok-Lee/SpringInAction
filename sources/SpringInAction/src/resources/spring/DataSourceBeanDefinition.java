package resources.spring;

import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jndi.JndiObjectFactoryBean;

public class DataSourceBeanDefinition {
	
	@Bean(destroyMethod="shutdown")
	public DataSource datasourceEmbedded() {
	    return new EmbeddedDatabaseBuilder()
	    .addScript("classpath:schema.sql")
	    .addScript("classpath:test-data.sql")
	    .build();
	}
	
	@Bean
	public DataSource datasourceJndi() {
	    JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
	    jndiObjectFactoryBean.setJndiName("jdbc/myDs");
	    jndiObjectFactoryBean.setResourceRef(true);
	    jndiObjectFactoryBean.setProxyInterface(javax.sql.DataSource.class);
	    return (DataSource) jndiObjectFactoryBean.getObject();
	}
	
	@Bean(destroyMethod="close")
	public DataSource datasourceBasic() {
	    BasicDataSource dataSource = new BasicDataSource();
	    dataSource.setUrl("jdbc:h2:tcp://dbserver/~/test");
	    dataSource.setDriverClassName("org.h2.Driver");
	    dataSource.setUsername("sa");
	    dataSource.setPassword("password");
	    dataSource.setInitialSize(20);
	    return dataSource;

	}
	
}
