package resources.spring;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.MultiValueMap;

public class ProfileCondition implements Condition{

	@Override
	public boolean matches(ConditionContext ctxt, AnnotatedTypeMetadata metadata) {
		if ( ctxt.getEnvironment() != null ) {
			MultiValueMap<String, Object> attrs =
				metadata.getAllAnnotationAttributes(Profile.class.getName());
			if(attrs != null) {
				for (Object value : attrs.get("value")) {
					if (ctxt.getEnvironment().acceptsProfiles((String[]) value)) {
						return true;
					}
				}
				return false;
			}
		}
		return true;
	}

}
