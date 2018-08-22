package resources.spring;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jndi.JndiObjectFactoryBean;

@Configuration
@Profile("prod")
public class ProductionProfileConfig {

	@Bean
	public DataSource datasourceJndi() {
	    JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
	    jndiObjectFactoryBean.setJndiName("jdbc/myDs");
	    jndiObjectFactoryBean.setResourceRef(true);
	    jndiObjectFactoryBean.setProxyInterface(javax.sql.DataSource.class);
	    return (DataSource) jndiObjectFactoryBean.getObject();
	}
}
