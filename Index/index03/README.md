# 고급 완이어링

- - - -
  
이장에서 다룰 내용

- 스프링 프로파일
- 조건 빈 선언
- 오토와이어링과 애매성
- 빈 범위

- - - -

이전 장에서 필수적인 빈 와이어링 기술들을 살펴보았다. 아마 배운 내용들이 많이 활용될 수 있음을 발견했을 것이다. 하지만 2장에서 본 것보다 더 많은 빈 와이어링에 대한 내용이 있다. 스프링은 더 향상된 빈 와이어링을 위한 여러 가지 트릭을 가지고 있다.

이번 장에서는 고급 기술들에 대해서 살펴본다. 이번 장의 기술들의 대해서 많은 시간을 쓸 필요는 없다. 그렇지만 그것이 덜 중요하다는 것은 아니다.

## 환경과 프로파일

소프트웨어 개발에 대해 가장 어려운 일 중 하나는 어떠한 환경에서 다른 응용 프로그램으로 마이그레이션 하는 것이다. 개발을 위해 만들어진 특정 환경 선택이 적절하지 않거나. 개발에서 생산으로 애플리케이션 전환이 동작하지 않는 경우가 있다.

데이터베이스 설정, 암호화 알고리즘 및 외부 시스템과의 통합은 전개 환경에서 걸처 변동될 가능성이 있는 몇 가지 예따.

예를 들어 데이터베이스 구성을 생각해 보자. 개발 환경에서는 테스트 데이터를 미리 로드한 내장 데이터베이스를 사용할 가능성이 높다. 이를테면 스프링 구성 클래스에서는 다음과 같은 @Bean 메소드로 EmbeddedDatabaseBuilder를 사용한다.  

```java
@Bean(destroyMethod="shutdown")
public DataSource datasource() {
    return new EmbeddedDatabaseBuilder()
    .addScript("classpath:schema.sql")
    .addScript("classpath:test-data.sql")
    .build();
}
```

이 유형은 javax.sql.DataSources의 빈을 작성한다. 하지만 그 빈을 만드는 방법이 가장 흥미롭다. EmbeddedDatabaseBuild를 사용하여 임베디드 Hypersonic 데이터베이스를 셋업하여, 스키마는 schema.sql에 정의하고 test-data.sql에서 테스트 데이터를 가져온다.

이 DataSource는 수동 테스트 응용 프로그램을 작동시키거나 통합 테스트를 실행하는 개발환경에서 쓸모가 있다. 사용자 데이터베이스 지정 상태에서 시작할 때마다 카운트 할 수 있다.

개발을 위해 EmbeddedDatabaseBuilder에서 만든 DataSource가 완벽하더라도 생상에서는 좋지 않은 선택일 수 있다. 생산 환경에서는 JNDI를 사용하여 컨테이너에서 DataSource를 얻는다. 따라서 이 경우에는 다음의 @Bean 방법이 더 적절하다.

```java

@Bean
public DataSource datasource() {
    JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
    jndiObjectFactoryBean.setJndiName("jdbc/myDs");
    jndiObjectFactoryBean.setResourceRef(true);
    jndiObjectFactoryBean.setProxyInterface(javax.sql.DataSource.class);
    return (DataSource) jndiObjectFactoryBean.getObject();
}

```

JNDI에서 DataSource를 얻으면 컨테이너 관리 연결 풀에서 DataSource 핸드오프를 가지는 작성 방법으로 컨테이너를 사용하여 의사 결정을 한다. JNDI에서 관리 데이터소스를 사용하면 생산에 대해서는 더 적합하지만. 간편한 통합 테스트나 개발자 테스트에는 불필요하며 복잡하다.

한편 QA환경에서는 완전히 다른 DataSource 설정을 선택할 수 있다. 사용자는 이러한 가공 DBCP연결 풀 구성을 선택할 수 있다.

```java

@Bean(destroyMethod="close")
public DataSource datasource() {
    BasicDataSource dataSource = new BasicDataSource();
    dataSource.setUrl("jdbc:h2:tcp://dbserver/~/test");
    dataSource.setDriverClassName("org.h2.Driver");
    dataSource.setUsername("sa");
    dataSource.setPassword("password");
    dataSource.setInitialSize(20);
    dataSource.setMaxActive(30);
    return dataSource;

}

```

문론 여기의 datasource() 메서드의 세 개 버전이 모두 다르다. 모든 유형은 빈 타입이 javax.sql.DataSource 인데. 이것이 유일한 공통점이다. 각 DataSource빈을 생성하기 위해서는 서로 완전히 다른 전략을 사용해야 한다.

다시 말하지만, 이와 관련된 논의는 DataSource 설정 방법이 아니다. 확실히 보기에 간단한 DataSource 빈이더라도 실제로 그리 간단하지는 않으며, 서로 다른 환경에서는 차이가 있다. 환경에 따라 가장 적절한 설정을 선택하도록 DataSource 빈 설정 방법을 찾아야 한다.

다른 해결 방법은 개별 구성 클래스를 빌드 타입에 결정하는 방법이다. 이 해결책이 가지는 문제점은 애플리케이션을 각 환경에 맞춰 재구성해야 한다는 것이다. QA 개발에서 다시 구축하는 것은 큰 문제가 아니겠지만. QA와 생산 단계에서 재작성이 필요한 버그를 일으키고 QA 팀구성원 간의 문제를 일으킬 수 있다.

### 빈 프로파일 설정하기

환경 관련 빈의 스프링 솔루션은 빌드 시 솔루션과 별로 다르지 않다. 물론 환경 관련 결정으로 그 빈이 생성되는 것은 아니다. 빌드 시에 결정을 하는 것이 아니라 스프링은 런타임 시에 결정을 내릴 때까지 기다린다. 그 결과 동일한 전개 유닛이 재구성되는 일 없이 모든 환경에서 작동한다.

스프링 버전 3.1에서는 빈 프로파일을 도입했다. 프로파일을 사용하려면 하나 이상의 프로파일에 모든 다양한 빈 정의를 수집하여 응용 프로그램이 각각의 환경에 배포 될 떄 적절한 프로파일이 활성화되어 있는지 확인해야 한다.

Java 구성에서는 빈이 속한 프로파일을 지정하는 @Profile 주석을 사용한다. 예를 들어 임베디드 데이터베이스의 DataSource빈은 이러한 설정 클래스를 구성된다.

``` java

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

```

여기서 중의해야 할 중요한 것은 클래스 수중에서 적용되는 @Profile 애너테이션이다. @Profile 애너테이션은 설정 클래스의 빈이 dev 프로파일이 활성화된 경우에만 작성되어야 함을 스프링에 알려 준다. dev 프로파일이 활서오하되지 않은 경우. @Bean 메소드는 무시된다.

사용자는 다음 코드처럼 제품 릴리스용 다른 설정 클래스를 가진다.

```java

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

```

이 경우 prod 프로파일이 활성화하지 않는 한 빈은 생성되지 않는다.

스프링 3.1은 그냥 클래스 수중에서 @Profile 애너테이션을 사용한다. 그러나 스프링 3.2이상에서는 @Bean 애너테이션과 함께 메소드 수중에서 @Profile을 사용한다. 다음 목록에 표시된 대로 하나의 설정 클래스에 두 빈 선언을 결합할 수 있다.

```java

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
        JndiObjectFactoryBean jndiObjectFactoryBean =   new JndiObjectFactoryBean();
        jndiObjectFactoryBean.setJndiName("jdbc/myDs");
        jndiObjectFactoryBean.setResourceRef(true);
        jndiObjectFactoryBean.setProxyInterfac  (javax.sql.DataSource.class);
        return (DataSource) jndiObjectFactoryBean.getObject();
    }

}

```

여기서 명확하지 않는 것은 빈이 DataSource빈의 각 프로파일에 소정의 활성화된 경우에만 생성되지만. 특정 프로파일의 범위에서 정의되지 않은 다른 빈이 있다는 것이다. 프로파일 활서오하 여부에 상관없이 프로파일이 지정되지 않은 모든 빈은 항상 활성화된다.

#### XML로 프로파일 설정하기

또한 < bean > 요소의 profile 애트리뷰트를 설정하여 XML로 프로파일된 빈을 설정할 수는 있다. 예를 들어 XML 개발을 위한 임베디드 데이터베이스의 DataSource 빈을 정의하려면 다음과 같이 XML 파일을 만들 수 있다.

```xml

    <jdbc:embedded-database id="dataSource">
        <jdbc:script    location="classpath:resources/config/schema sql"/>
        <jdbc:script    location="classpath:test-data.sql"/>
    </jdbc:embedded-database>

```

마찬가지로 제품 수준의 JNDI로 얻은 DataSource 빈을 위해 profile을 prod로 설정하여 다른 설정 파일을 만들 수 있다. 그리고 QA프로파일에 의해 지정된 연결 풀 정의 DataSource 빈의 또다른 XML 파일을 만들 수 있다.

모든 설정 XML 파일은 배치 유닛에 수집되지만 오직 profile 애트리뷰트는 사용될 활성 프로파일에 매칭된다. 오히려 각각의 환경에서 XML파일이 확대되도록 만드는 것보다 루트 < beans > 요소에 포함된 < beans > 요소를 정의하는 옵션을 사용한다.

``` xml

    <beans profile="dev">
        <jdbc:embedded-database id="dataSource">
            <jdbc:script location="classpath:resources/config/schema.sql"/>
            <jdbc:script location="classpath:test-data.sql"/>
        </jdbc:embedded-database>
    </beans>

    <beans profile="qa">
    <bean id="datasource" class="org.apache.tomcat.dbcp.dbcp2.BasicDataSource"
        destroy-method="close"
        p:url="jdbc:h2:tcp://dbserver/~/test"
        p:driverClassName="org.h2.Driver"
        p:username="sa"
        p:password="password"
        p:initialSize="20" />
    </beans>

    <beans profile="prod">
        <jee:jndi-lookup id="dataSource"
            resource-ref="true"
            proxy-interface="javax.sql.DataSourde" 
            jndi-name="jdbc/myDatabase" />
    </beans>

```

위 예제의 여러 빈들이 동일한 XML파일에 정의되어 있는 것과 별도로 다른 XML 파일에 있더라도 그 동작 효과는 동일하다. 세 가지 정류의 빈이 있으며, javax.sql.DataSource 타입을 가지고 ,dataSource의 ID를 가진다. 그러나 런타임 시에는 프로파일이 활성 상태이니지에 따라 오직 하나의 빈이 만들어진다.

이때 이런 질문이 떠오른다. 어떻게 프로파일을 활성화할 수 있는가?

### 프로파일 활성화

스프링은 프로파일이 활성 상태인지를 결정하는 두 가지 다른 프로퍼티를 가진다. spring.profiles.active 와 spring.profiles.default이다. spring.profiles.active가 설정되어 있는 경우, 그 값은 프로파일이 활성 상태인지를 결정한다. spring.profiles.active가 설정되어 있지 않으면 스프링은 spring.profile.default가 된다. spring.profiles.active 또는 spring.profiles.default가 설정되어 있지 않는 경우 활성화 프로파일은 없으며 프로파일에 정의되지 않은 빈만 만들어진다.

프로파일을 설정하기 위한 여러 가지 방법이 있다.

- DispatcherServlet에 초기화된 파라미터 설정
- 웹 애플리케이션의 컨텍스트 파라미터
- JNDI 엔트리
- 환경 변수
- JVM 시스템 프로퍼티
- 통합 테스트 클래스에서 @ActiveProfiles 애너테이션 사용

여러분은 여러분의 필요에 따라 spring.profiles.active와 spring.profiles.default의 최정의 조합을 선택할 수 있습니다.

설정을 위한 한 가지 방법은 DispatcherServlet에서와 서블릿 컨텍스트에서(ContextLoaderListener) 파라미터를 사용하여 개발 프로파일에 spring.profiles.default를 설정하는 것이다. 다음 목록에 나타낸 바와 같이 예를 들어 웹 애플리케이션의 web.xml 파일은 spring.profiles.default를 설정한다.

``` xml

     <!-- 	컨텍스트 설정 파일 -->
     <context-param>
        <param-name>ContextConfigLocation</param-name>
        <param-value>classpath:resources/spring/MultifulDataSourceProfile.xml</param-value>
     </context-param>

     <context-param>
        <param-name>spring.profiles.default</param-name>
        <param-value>dev</param-value>
     </context-param>
  

    <!-- 스프링 부트스트랩 컴포넌트  -->
    <listener>
        <listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
    </listener>
  
    <!--디스패처 서블릿을 통한 부트스트랩 컴포넌트 -->
    <servlet>
        <servlet-name>springDispatcherServlet</servle-name>
        <servlet-class>org.springframework.web.servle.DispatcherServlet</servlet-class>
        <init-param>
            <param-name>spring.profiles.default</param-name>
            <param-value>dev</param-value>
        </init-param>
        <load-on-startup>1</load-on-startup>
    </servlet>

    <!-- URL 매핑 -->
    <servlet-mapping>
    <servlet-name>springDispatcherServlet</servlet-name>
        <url-pattern>/</url-pattern>
    </servlet-mapping>

```

spring.profiles.defult를 이렇게 설정하면, 모든 개발자가 소스 제어에서 애플리케이션 코드를 검색하여 추가 설정 없이 개발 설정을 사용하여 실행한다.

그리고 애플리케이션은 QA, 생산, 또는 기타 환경에서 배포될 떄 그리고 애플리케이션은 QA, 생산 또는 기타 환경에서 배포될 때, 시스템 프로퍼티, 환경 변수 또는 JNDI를 사용하여 spring.profiles.active를 제대로 설정할 수 있따. spring.profiles.active가 설정될 때 spring.profiles.default가 무엇으로 설정되었는지 문제가 아니라 spring.profiles.active의 프로파일 설정이 우선이다.

spring.profiles.active와 spring.profiles.default에서 단어 profiles 가 여러 개임을 알 수 있다. 이 것은 쉼표로 구분된 프로파일 이름을 나열하여 동시에 여러 프로파일을 활성화한다. 물론 동시에 두 dev와 prod 프로파일을 사용하는 것은 의미가 없을 테지만 동시에 여러 직교 프로파일을 사용할 수 있다.

#### 프로파일 테스팅

통합 테스트를 수행할 떄 종종 제품에 적용 했던 같은 설정을 사용하여 테스트 하고 싶을 것이다. 하지만 설정에서 프로파일에 있는 설정 참조 빈을 참조한다면 이러한 테스트를 실행하는 경우에 해당 프로파일을 사용해야만 한다.

스프링은 테스트를 실행할 때 활성화될 필요가 있는 프로파일을 지정할 수 있도록 @ActiveProfiles 제공한다. 이는 대부분 통합 테스트 중에 활성화하고자 하는 개발 프로파일이다. 예를 들어 다음은 dev 프로파일을 활성화하기 위해 @ActiveProfile를 사용하여 테스트 클래스를 발취한 것이다.

``` java

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes= {PersistenceTestClass.class})
@ActiveProfiles("dev")
public class PersistenceTest { }

```

스프링 프로파일은 프로파일이 활성화 된 기반을 조건부로 빈을 정의하는 좋은 방법이다. 그러나 스프링 4.0은 조건부 빈 정의를 위한 보다 일반적인 매커니즘을 제공한다. 스프링 4.0과 @Conditional 애너테이션을 사용하여 조건부 빈을 정의하는 방법을 살펴보자.

## 조건부 빈

하나 이상의 빈을 설정하는 경우, 일부 라이브러리는 애플리케이션의 클래스 패스에서 사용할 수 있다고 가정해 보자. 아니면 빈이 특정 다른 빈도 선언되어 있는 경우에만 작성된다고 하자 아마도 당신은 특정 환경 변수가 설정되어 있는 경우에만 빈을 생성하려 할 것이다.

스프링 4.0 이전까지 조건 설정 수준을 달성하는 것은 어려웠지만, 스프링 4.0에서는 @Bean을 적용할 수 있는 새로운 @Conditional 애너테이션이 소대 되었다. 소정의 조건이 참으로 평가되는 경우 빈이 생성된다. 그렇지 않으면 빈은 무시된다.

예를 들어 마법 환경 프로퍼티가 설정되어 있으며, 스프링이 인스턴스화하려는 이름이 MasicBean인 클래스가 있다고 하자. 환경이 그런 프로퍼티를 가지고 있지 않는 경우 Magicean은 무시된다. 다음 코드는 조건부로 @Conditional을 사용한 MagicBean을 설정하는 구성을 보여준다.

```java

@Configuration
public class ConditionalBeanConfig {
    @Conditional(MagicExistsCondition.class)
    @Bean
    public MagicBean magicBean() {
        return new MagicBean();
    }
}

```

@Conditional은 조건을 지정하기 위한 클래스이며, 이 경우에는 MagicExistsCondition이다.
@Conditional은 Condition 인터페이스와 같이 사용된다.

``` java

interface ConditionInterface extends Condition {
    boolean matches(ConditionContext ctxt, AnnotatedTypeMetadata metadata);
}

```

@Condition에 지정된 클래스는 @Condition 인터페이스를 구현하는 모든 유형이 도니다. 보다시피 Condition 인터페이스는 matches() 메소드를 제공이 필요한 간단한 인터페이스다. matches() 가 false 를 반환하면 그 빈은 작성되지 않는다.

아래 Environment에서 magic 프로퍼티가 존재하는지를 확인하기 위한 Condition 코드를 제공한다. 아래코드는 MagicExistsCondition을 보여 주며 Condition 구현을 제공한다.

```java

class MagicExistsCondition implements ConditionInterface {

    @Override
    public boolean matches(ConditionContext ctxt, AnnotatedTypeMetadata metadata) {
        Environment env = ctxt.getEnvironment();
        return env.containsProperty("magic");
    }

}

```

이 코드의 matches() 메소드는 간단하지만 강력하다. 환경 프로퍼티의 이름 magic이 존재하는지 여부를 확인하기 위해 주어진 ConditionContext 객체에서 Environment를 사용한다. 이 예제에서는 프로퍼티 값은 무관하다. 단지 존재할 필요가 있다. 결과는 matches() 에서 반환된 true 값을 가진다. 그 결과 조건을 충족하고 MagicExistsCondition을 참조하는 @Conditional애너테이션의 빈이 생성된다.

반면, 프로퍼티가 존재하지 않는다면 조건은 실패하고 false가 matches()에서 반환된다. 그 빈들은 모두 생성되지 않는다.

MagicExistsCondition만 ConditionContext에서 Environment를 사용한다. 그렇지만 COndition구현에서 고려할 것이 많다. matches() 메소드에서는 의사 결정 시에 사용할 COnditionContet와 AnnotatedTypeMetadata가 주어진다.

ConditionContet는 다음과 같은 인터페이스다.

``` java

interface ConditionContextInterface {
    BeanDefinitionRegistry getRegistry();
    ConfigurableListableBeanFactory getBeanFactory();
    Environment getEnvorinment();
    ResourceLoader getResourceLoader();
    ClassLoader getClassLoader();
}

```

ConditionContext에서 다음을 수행할 수 있다.

- BeanRegistry() 반환을 통한 beanDefinitionRegistry로 빈 정의를 확인한다.
- 빈의 존재를 확인하고, getBeanFactory()에서 반환되는 ConfigurableListableBeanFactory를 통해 빈 프로퍼티를 발굴한다.
- getEnvironment() 로 부터 얻는 Environment를 통해 환경 변수 값을 확인한다.
- getResourceLoader() 에서 반환된 ResourceLoader를 통해 로드된 자원 내용을 읽고 검사한다.
- getClassLoader() 에서 반환된 ClassLoader를 통해 클래스의 존재를 로드하고 확인한다.

AnnotatedTyoeMetadata는 @Bean 메소드의 애너테이션을 검사할 수 있는 기회를 제공한다. ConditionContext 처럼 AnnotatedTypeMetadata의 인터페이스는 다음과 같다.

```java

public interface AnnotatedTypeMetadata {
    boolean isAnnotated(String annotationType);
    Map<String, Object> getAnnotationAttributes(String annotationType);
    Map<String, Object> getAnnotationAttributes(String annotationType, boolean classValueAsString);
    MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationType);
    MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationType, boolean ClassValuesAsString);
}

```

isAnnotated() 메소드를 사용하여, @Bean 메소드가 특정 에너테이션 타입을 사용해 애너테이션을 붙일 수 있는지를 검사한다. 다른 방법을 사용하여 @Bean 메소드에 적용된 애너테이션 애트리뷰트를 확인한다.

흥미롭게도, 스프링 4에서 시작된 @Proifle 애너테이션은 @Conditional 및 Conditional 인터페이스에 기초하여 리팩토링된다 @Conditional과 @Conditional에서 작동하는 또 다른 예로서 Profile이 스프링 4에서 구현되는 방법을 살펴본다.

@Profile 애너테이션은 다음과 같다.

```java

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(ProfileCondition.class)
public @interface Profile {
    String[] value();
}

```

@Profile 자체가 @Conditional을 사용하여 애너테이션되고, Condition 구현으로 ProfileCondition을 참조한다. 다음과 같이 ProfileCondition은 Condition을 구현하고 그 결정을 할 떄 ConditionContext와 AnnotatedTypeMetadata 모두에서 여러 가지 요인을 고려한다.

```java

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

```

보다시피 ProfileCOndition은 AnnotatedTypeMetadata에서 @Profile의 모든 애너테이션 애트리뷰트를 가져온다. 빈 프로파일의 미음리 포함되어 있는 value 애트리뷰트를 명시적으로 확인한다. 그 후 프로파일이 활성 상태인지 여부를 확인하기 위해 ConditionContext에서 가져온 Environment를 살펴본다.