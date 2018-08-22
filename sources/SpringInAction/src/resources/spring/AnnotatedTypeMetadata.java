package resources.spring;

import java.util.Map;

import org.springframework.util.MultiValueMap;

public interface AnnotatedTypeMetadata {
	boolean isAnnotated(String annotationType);
	Map<String, Object> getAnnotationAttributes(String annotationType);
	Map<String, Object> getAnnotationAttributes(String annotationType, boolean classValueAsString);
	MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationType);
	MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationType, boolean ClassValuesAsString);
}
