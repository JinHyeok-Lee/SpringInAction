package resources.spring;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class MagicExistsCondition implements ConditionInterface {

		@Override
		public boolean matches(ConditionContext ctxt, AnnotatedTypeMetadata metadata) {
			Environment env = ctxt.getEnvironment();
			return env.containsProperty("magic");
		}
		
}