package resources.spring;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

public interface ConditionContextInterface {
	BeanDefinitionRegistry getRegistry();
	ConfigurableListableBeanFactory getBeanFactory();
	Environment getEnvorinment();
	ResourceLoader getResourceLoader();
	ClassLoader getClassLoader();
}
