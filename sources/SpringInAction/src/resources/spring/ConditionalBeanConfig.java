package resources.spring;


import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotatedTypeMetadata;


@Configuration
public class ConditionalBeanConfig {
	@Conditional(MagicExistsCondition.class)
	@Bean
	public MagicBean magicBean() {
		return new MagicBean();
	}
}


class MagicBean{
	
}



