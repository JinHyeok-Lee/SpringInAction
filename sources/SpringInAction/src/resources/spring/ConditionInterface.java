package resources.spring;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public interface ConditionInterface extends Condition {
	boolean matches(ConditionContext ctxt,
			AnnotatedTypeMetadata metadata);
}
