# 고급 와이어링

> 목차
1. [환경과 프로파일](#환경과-프로파일)
2. [조건부 빈](#조건부-빈)
3. [오토와이어링의 모호성](#오토와이어링의-모호성)
4. [빈 범위](#빈-범위)

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

또한 `<bean>` 요소의 profile 애트리뷰트를 설정하여 XML로 프로파일된 빈을 설정할 수는 있다. 예를 들어 XML 개발을 위한 임베디드 데이터베이스의 DataSource 빈을 정의하려면 다음과 같이 XML 파일을 만들 수 있다.

```xml

    <jdbc:embedded-database id="dataSource">
        <jdbc:script    location="classpath:resources/config/schema sql"/>
        <jdbc:script    location="classpath:test-data.sql"/>
    </jdbc:embedded-database>

```

마찬가지로 제품 수준의 JNDI로 얻은 DataSource 빈을 위해 profile을 prod로 설정하여 다른 설정 파일을 만들 수 있다. 그리고 QA프로파일에 의해 지정된 연결 풀 정의 DataSource 빈의 또다른 XML 파일을 만들 수 있다.

모든 설정 XML 파일은 배치 유닛에 수집되지만 오직 profile 애트리뷰트는 사용될 활성 프로파일에 매칭된다. 오히려 각각의 환경에서 XML파일이 확대되도록 만드는 것보다 루트 `<beans>` 요소에 포함된 `<beans>` 요소를 정의하는 옵션을 사용한다.

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

보다시피 ProfileCondition은 AnnotatedTypeMetadata에서 @Profile의 모든 애너테이션 애트리뷰트를 가져온다. 빈 프로파일의 미음리 포함되어 있는 value 애트리뷰트를 명시적으로 확인한다. 그 후 프로파일이 활성 상태인지 여부를 확인하기 위해 ConditionContext에서 가져온 Environment를 살펴본다.

### 오토와이어링의 모호성

2장에서 생성자의 인수와 프로퍼티에 빈 참조를 주입할 떄 스프링이 모든 작업을 수행하는 오토와이어링 사용 방법을 설명했다. 오토와이어링은 응용 프로그래밍 구성 요소를 조립하는 데 필요한 명시적 설정 양을 삼소시키므로 큰 도움이 된다.

정확히 하나의 빈이 원하는 결과와 일치할 떄 오토와이어링은 동작한다. 일치하면 빈이 여럿있으면 모호성 때문에 스프링에서 프로퍼티, 생성자 인수, 메소드 파라미터의 오토와이어링은 어렵다.

오토와이어링의 모호성을 나타내기 위해 @AUtowired를 가지는 다음 setDessert() 메소드가 에너테이션 됬다고 가정하자.

```java

@Autowired
public void setDessert(Dessert dessert) {

    this.dessert = dessert;
}

```

이 예제에서 Dessert는 인터페이스이며 세 개의 클르스, 즉 Cake, Cookies 및 IceCream으로 구현 된다.

```java

@Component
public class Cake implements { ... }

@Component
public class Cookies implements { ... }

@Component
public class IceCream implements { ... }

```

모든 세 개의 구현은 @Component으로 애너테이션되어 컴포넌트 스캐닝 중 찾을 수 있고, 스프링 애플리케이션 컨텍스트에서 빈으로 생성된다. 스프링이 SetDessert() 에서 Dessert 파라미터를 오토와이어링 할 때 하나의 명확한 대안이 있지는 않다. 대부분의 사람들이 여러 디저트 옵션에 직면했을 때 선택하는 것은 문제가 되지 않지만, 스프링은 선택할 수 없다. 스프링은 실패하고 예외를 발생시킨다. 정확하게는 스프링은 NoUniqueBeanDefinitionException을 발생시킨다.

```java

nested exception is org.springframework.beans.factory.NoUniqueBeanDefinitionException ....

```

물론 이 디저트를 먹는 예제에서 오토와이어링은 모호함 문제를 발생시킨다. 사실 모호성 오토와이러링은 기대 이상으로 드물다. 그런 모호함이 진짜 문제임에도 불구하고, 주어진 타입의 구현이 한 개만 있는 경우는 종종 있으며, 오토와이어링은 완벽하게 동작한다.

모호함이 발생하지 않는 그 순간을 위해 스프링은 옵션 멱 가지를 제공한다. 기본적인 방법을 사용하여 후보 빈들 중 하나를 선택할 수 있으며, 스프링에서 단일 후보로 선택을 좁히기 위해서 한정자를 사용한다.

#### 기본 빈 설정

만약에 당신이 나와 비슷하다면, 모든 종류의 디저트르 좋아할 것이다. 케이크 ... 쿠키 ... 아이스크림 ... 모두 좋다. 하지만 하나의 디저트를 고르도록 강제된 경우는 어떻게 할까?

빈 선언 시, 기본 빈으로 후보 빈 중 하나를 지정하여 오토와이어링의 모호함을 피할 수 있다. 모든 모호성 이벤트에서 스프링은 여러 개의 후보 빈 중에서 주요 빈을 선택하며, 기본적으로 사용하가 "선호하는" 빈을 선언한다.

아이스크림을 가장 좋아하는 디저트라고 하자. @Primary 애너테이션을 사용하여 스프링에서 그 맛 선택을 표현한다. @Primary는 컴포넌트 스캐닝 도니 빈을 위한 @Component와 자바 설정에서 선언된 빈의 @Bean을 함께 사용한다. 예를 들어, 여기에 주요 대안으로 @Component로 애너테이션된 @IceCream 빈을 선언하는 방법이 있다.

```java

@Component
@Primary
publuc class IceCream Implements Dessert { ... }

```

또는 자바 설정으로 명시적으로 IceCream 빈을 선언한다. @Bean 메소드는 다음과 같다.

```java

@Bean
@Primary
public Dessert iceCream() {

    return new IceCream();
}

```

XML로 빈을 설정하여, 기본 빈을 지정할 수 있다. `<bean>` 요소는 기본 빈을 지정하기 위해서 primary 애트리뷰트를 가진다.

```xml

<bean id="iceCream"
      class="com.desserteater.IceCream"
      primary="true" />

```

기본 빈을 어떻게 지정하든 효과는 동일하다. 모호함이 있으면 기본 빈을 선택해야 한다. 이 작업은 두 개 이사의 기본 빈을 지정할 때 활용된다. 예를 들어, Cake 클래스는 다음과 같다.

```java

@Component
@Primary
public class Cake implements Dessert { ... }

```

지금 두 가지 주요 Dessert 빈인 Cake와 IceCream이 있다. 이 때 새로운 모호성 문제가 제기된다. 새로운 모호성 때문에, 스프링에서 여로 후보 빈 중 선택이 어려우며, 여러 기본 빈 중에서도 선택이 마찬가지로 어렵다. 여러 개의 빈을 기본으로 지정하면 사실 기본 후보자는 없다.

더 강력한 모호성을 가지는 메커니즘에 대응하기 위해 한정자(qualifier)를 살펴보자.

#### 오토와이어링 빈의 자격

기본 빈의 한계점은 @Primary가 하나의 명백한 옵션 선택을 하지 못한다는 점이다. 단지 바람직한 대안을 지정할 뿐이다. 여러 개의 기본이 있으면, 옵션을 줄이기 위해 할 수 있는 것이 없다.

이와는 대조적으로 스프링의 수식은 결국 소정의 자격을 충족하는 모든 후보 빈에 적용되고, 단일 빈 대상으로 협소화 작업을 적용한다. 모든 한정자에 적용한 휴에도 모호함이 남아 있는 경우, 항상 새로운 범위를 좁히기 위해 다시 많은 수식자를 적용한다.

@Qualifier 애너테이션은 수식자를 사용하는 주도니 방법이다. 그것은 주입 대상 빈을 지정할 주입 지점에서 @Autowired나 @Inject와 함께 적용된다. 예를 들어, 아이스크림 빈이 setDessert() 내로 주입되는지 확인하고 싶다고 하자.

```java
@Autowired
@Qualifier("iceCream")
public void setDessert(Dessert dessert) {

    this.dessert = dessert;
}

```

이것은 가장 간단한 형태의 전형적인 수식자의 예다. @Qualifier의 파라미터는 주입할 빈의 ID다. 모든 @Component로 애너테이션 된 클래스는 ID가 대문자로 시작하지 않는 클래스 명인 빈으로 만들어진다. @Qualifier("iceCream")은 컴포넌트 스캔 시, IceCream 클래스의 인스턴스를 만들 경우 생성 빈을 참조한다.

사실, 이보다 더 많은 이야기가 있다, 더 정확히 말하면, @Qualifier("iceCream")은 수식으로 문자열 "iceCream"을 가지는 빈을 참조한다. 다른 수식자를 지정하지 못하는 경우에, 모든 빈은 그들의 빈 ID와 같은 기본 수식자를 부여받는다. 따라서 setDessert() 메소드는 수식으로 "iceCream"을 가진 빈이 주입된다. IceCream 클래스가 스캔될 떄, ID가 iceCream인 빈이 생성된다.

기본적인 빈 ID 수식자 자격은 간단하지만 몇 가지 문제를 제기한다. IceCream 이름을 Gelato로 변경하거나 IceCream 클래스를 리팩토링하면 어떻게 될까? 그 경우, 빈의 ID 및 기본 수식자로 gelato이며, setDessert() 에서 수식자와 일치하지 않는다. 따라서 오토와이어링은 실패한다.

문제는 주입되는 빈의 클래스 명에 연결되어 있는 setDessert() 에 수식자가 지정되어 있다는 점이다, 클래스 이름을 변경하면 수식자는 잘못된 것으로 렌터링 된다.

##### 맞춤식 수식자 만들기

수식자를 빈 ID에 의존하는 대신, 빈에 자신의 수식자를 지정한다. 사용자가 해야 할 것은 빈 선언에서 @Qualifier 애너테이션을 배치하는 것이다. 예를 들어, @Component 와 함께 적용한다.

```java

@Component
@Qualifier("cold")
public class IceCream implements Dessert { ... }

```

이 경우 cold 라는 수식자는 IceCream 빈에 할당된다, 클래스 명에 연결되어 있지 않으므로 사용자가 원하는 모든 오토와이어링의 끓어짐 없이 IceCream 클래스 명을 리팩토링한다. 주입지점에서 cold 수식자를 참조하는 한 문제는 발생하지 않는다.

```java

@Autowired
@Qualifier("cold")
public void setDessert(Dessert dessert) {

    this.dessert = dessert;
}

```

이것은 명시적으로 자바 설정을 가지고 빈을 정의할 때, @Qualifier를 @Bean 애터테이션과 함께 사용한다.

```java

@Bean
@Qualifier("cold")
public Dessert iceCream() {

    return new IceCream();
}

```

맞춤형 @Qualifier 값을 정의하는 경우, 그것은 오히려 어떤 이름을 사용하는 것보다 빈의 특성 또는 서술적 용어를 사용하는 것이 좋다. 이 경우에 "cold" 빈으로 IceCream 빈을 설명했다. 주입 지점에서 그것은 IceCream 을 묘사하는 표현인 "나에게 차가운 디저트를 주세요" 라고 읽는다. 마찬가지로 Cake는 "soft"로 묘사할 수 있고 Cookies는 "crispy"로 묘사할 수 있다.

##### 맞춤형 수식자 에터테이션 정의하기

한정자로 빈 ID에 기반을 두는 대신에, 빈에 할당할 수 있다. 그러나 이렇게 하더라도 여전히 일반적인 특성을 공유하는 여러 빈을 가지고 있는 경우에는 문제가 발생한다. 예를 들어, 새로운 Dessert 빈을 도입하면 어떻게 될지 상상해 보자.

```java

@Component
@Qualifier("cold")
public class Popsicle implements Dessert { ... }

```

이제 두 개의 "cold" 디저트를 가지고 있다. 다시 디저트 빈이 오토와이어링의 모호함에 직면한다. 하나의 빈에 대한 선택 범위를 좁힐 수식자가 필요하다.

해결책은 양쪽의 주입 지점과 빈 정의에 다른 @Qualifier를 고정 시키는 방법을 사용하는 것이다. IceCream 클래스는 다음과 같이 된다.

```java

@Component
@Qualifier("cold")
@qualifier("creamy")
public class IceCream implements Dessert { ... }

```

Popsicle 클래스는 다른 @Qualifier를 사용한다.

```java

@Component
@Qualifier("cold")
@Qualifier("fruity")
public class Popsicle implements Dessert { ... }

```

주입 시, 다음과 같이 IceCream 으로 좁혀 간다.

```java

@Autowired
@Qualifier("cold")
@Qualifier("creamy")
public void setDessert(Dessert dessert) {

    this.dessert = dessert;
}

```

작은 문제가 있다. 자바는 동일한 유형의 여러 애너테이션이 같은 항목에 반복될 수 없다. 계속 시도하면 컴파일러 오류가 발생한다. 하나의 선택에 오토와이어링 된 후보 리스트를 좁힐 수 있는 @Qualifier를 사용할 수 있는 방법은 없다.

하지만 빈에 자격이 주어지고 특성을 표현하기 위해 사용자 지정 수식자 애너테이션을 작성한다. 그리고, 또한 해야 할 일로 @Qualifier를 사용하여 애너테이션해야 한다. @Qualifier("cold")를 사용하는 것이 아니라 다음과 같이 정의된 맞춤형 @Cold 애터네이션을 사용할 수 있다.

```java

@Target({ElementType.CONSTRUCTOR, ElementType.FIELD,
         ElementType.METHOD, ElementType.TYPE})
@Retention(Retentionpolicy.RUNTIME)
@Qualifier
public @interface Cold { }

```

마찬가지로 @Qualifier("creamy")의 교체로 새로은 @Creamy 애너테이션을 만들 수 있다.

```java

@Target({ElementType.CONSTRUCTOR, ElementType.FIELD,
         ElementType.METHOD, ElementType.TYPE})
@Retention(Retentionpolicy.RUNTIME)
@Qualifier
public @interface Creamy { }

```

그리고 마찬가지로 어디서든지 사용할 수 있는 @Soft, @Crispy 및 @Fruity 애너테이션을 만들수 있고, 그렇지 않으면 @Qualifier 애너테이션을 사용한다. @Qualifier 애너테이션을 사용하여 @Qualifier의 특성을 파악한다. 실제로 그 특성은 수식자 애너테이션을 가진다.

이제 IceCream을 재방문하고, 다음과 같은 @Cold와 @Cream을 사용하여 애너테이션을 달 수 있다.

```java

@Component
@Cold
@Creamy
public class IceCream implements Dessety { ... }

```

이와 유사하게 popsicle 클래스는 @Cold와 @Fruity로 애너테이션된다.

```java

@Component
@Cold
@Fruity
public class Popsicle implements Dessety { ... }

```

마지막으로, 주입 시에 스펫을 만족시키기 위한 빈 대상 선택 범위를 좁히려면 수식자 애너테이션 조합을 사용한다. IceCream 빈을 엑세스하려면 serDessert() 메서드는 다음과 같이 애너테이션 된다.

```java

@Autowired
@Cold
@Creamy
public void setDessert(Dessert dessert) {

    this.dessert = dessert;
}

```

사용자 지정 수식자 애너테이션을 정의하여, 자바 컴파일러에서 제한 없이 또는 불만 없이 여러 개의 수식자를 사용한다. 또한 맞춤혐 애너테이션은 @Qualifier 애너테이션을 사용하는 것과 문자열로 수식자를 지정하는 것보다 더 타입세이프(type-safe)하다.

setDessert() 메서드를 자세히 살펴보고 그거싱 어떻게 애터네이션되는 지를 살펴보자. IceCream빈으로 오토와이어링 하는 메소드를 사용하고 있는지 명시적으로 이야기 해 보자대신 그 특성 @Cold와 @Creamy 를 사용하여 원하는 빈을 확인한다. 따라서 setDessert() 메서드는 어떤 특정 디저트 구현으로부터 분리된다. 이를 만족하면 어느 빈도 문제업다. Dessert 구현의 현재 선택으로 IceCream 빈이 단일 매칭 후보가 된다.

이 절과 이전 절에서는 맞춤형 애너테이션에서 스프힝을 확장하는 몇 가지 방법을 검토했다. 맞춤형 조건부 애너테이션을 만들려면 새 애너테이션을 작성하고 @Conditional을 사용하여 애너테이션한다. 맞춤형 수식자 애너테이션을 만들기 위해 새 애너테이션을 작성하고 @Qualfier를 사용하여 애너테이션한다. 이 기술은 맞춤형 특수 목적의 애너테이션을 구성하고, 스프링의 애너테이션을 많이 사용한다.

이제 다른 범위에 만들어진 빈을 선언할 수 있는 방법을 알아보자.

### 빈 범위

기본적으로 스프링 애플리케이션에서 생성되는 모든 빈은 싱글톤이다. 말하자면 주어진 빈이 다른 빈에 얼마나 많이 주입되든 간에 매번 동일한 인스턴스가 주입된다.

대부분의 경우, 싱글톤 빈은 이상적이다. 작은 태스크용으로만 사용되는 객체의 인스턴스화와 가비지 컬레션 인스턴스의 비용은 객체가 무상태일 때와 애플리케이션엣 재사용될 때 적합하지 않다.

그러나 이따금 어떤 상태를 유지하거나 재사용용으로는 안전하지 않은 이변성 클래스를 사용한다. 객체에 문제가 생길 수 있으며 나중에 재사용될 때 예기치 않은 문제를 만들 수 있으므로 싱글톤 빈으로 클래스를 선언하는 것은 좋은 아이디어가 아니다.

스프링은 빈이 생성될 수 있는 여러 개의 범위를 정의하며 다음을 포함한다.

* 싱들톤 - 전체 애플리케이션을 위해 생성되는 빈의 인스턴스
* 프로토타입 - 빈이 주입될 때마다 생성되거나 스프링애플리케이션 컨텍스트에서 얻는 빈의 인스턴스
* 세션 - 웹 애플리케이션에서 각 세션용으로 생성되는 빈의 인스턴스
* 요청 - 웹 애플리케이션에서 각 요청 단위로 생성되는 빈의 인스턴스

싱글톤 범위가 기본 범위이지만, 이야기한 것 처럼 이변성 타입에는 적합하지 않다. @Component 에너테이션 또는 @Bean 애너테이션과 관련된 다른 타입을 선택하기 위해서는 @Scope 애너테이션을 사용한다.

예를 들면, 빈을 찾고 선언하기 위해서 컴포넌트 스캐닝을 한다면, 프로토타입 빈을 만들기 위해 @Scope를 사용하여 bean 클래스를 애너테이션한다.

```java

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Notepad { ... }

```

여기서는 ConfigurableBeanFactory 클래스에서 상수 SCOPE_PROTOTYPE 을 사용하여 프로토타입 범위를 지정한다. 또한 @Scope("prototype")을 사용할 수 있지만 SCOPE_PROTOTYPE을 사용하는 것이 더 안전하고 실수도 적다.

자바 설정에서 프로토타입으로서 Notepad 빈을 설정한다면, 원하는 범위를 지정하기 위해서 @Bean과 함께 @Scope를 사용한다.

```java

@Bean
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public Notepad notepad() {

    return new Notepad();
}

```

그리고 XML으로 빈을 설정할 때, `<bean>` 요소의 scope 애트리뷰트를 사용하여 범위를 설정한다.

```xml

<bean id="notepad"
    class="com.myapp.Notepad"
    scope="prototype" />

```

프로토타입 범위를 어떻게 지정하든 상관없이 빈의 인스턴스는 매번 생성되고, 스프링 애플리케이션 컨텍스트로부터 주입되거나 얻을 수 있다. 따라서 Notepad의 인스턴스를 얻는다.

#### 요청과 세션 범위 작업하기

웹 애플리케이션에서 특정 요청을 하거나, 세션 범위 내에서 공유하는 Bean을 인스턴스화할 때 도움을 얻을 수 있다. 예를 들어, 일반적인 전자 상거래 애플리케이션에서는 사용자의 장바구니를 나타내는 빈을 가진디ㅏ. 장바구니를 나타내는 빈이 싱글톤인 경우에는 모든 사용자가 동일한 장바구니에 제품을 추가한다. 한편, 장바구니가 프로토타입 범위인 경우에는 제품이 다른 프로토타입 범위의 장바구니가 주입된 애플리케이션의 다른 부분에서는 사용되지 못할 수 있으며, 애플리케이션 한 부분의 장바구니에 추가된다.

장바구니 빈의 경우, 가장 직접적으로 사용자에게 주어지므로 세션 범위가 가장 알맞다. 세션 범위를 적용하려면 지정한 프로토타입 범위와 같이 @Scope 애너테이션을 사용한다.

```java

@Component
@Scope(value=WebApplicationContext.SCOPE_SESSION
       proxyMode=ScopedProxyMode.INTERFACES)
public ShoppingCart cart() { ... }


```

여기서는 (세션 값을 가지는) WebapplicationContext 에서 SCOPE_SESSION 상수에 value 애트리뷰트를 설정한다. 이것은 웹 애플리케이션의 각 세션당 ShoppingCart 빈의 인스턴스를 생성하도록 스프링에 지시한다. ShoppingCart 빈의 여러 인스턴스가 존재하지만, 한 가지는 주어진 세션을 생헝되고 그것은 해당 세션에 관해서는 본질적으로 싱글톤이다.

@Scope 도 ScopedProxyMode.INTERFACES로 설정되는 proxyMode 특성을 가진다. 이 특성은 싱글톤 범위의 빈에 세션 범위 또는 요청 범위 빈을 주입할 때 발생하는 문제를 해결한다. 그러나 proxyMode를 설명하기 전에, proxyMode 문제를 보여주는 시나리오를 살펴보자.

다음과 같이 싱글톤 StoreService 빈의 setter 메소드로 ShoppingCart 빈을 주입한다고 가정한다.

```java

@Component
public class StoreService {

    @Autowire
    public void setShoppingCart(ShoppingCart shoppingCart) {

        this.shoppingCart = shoppingCart;
    }
    ...
}

```

스프링 애플리케이션 컨텍스트가 로드될 수 있도록 StoreService 싱글톤 빈이 생성된다. 이제 스프링은 setShoppingCart() 메소드로 ShoppingCart를 주입한다. 그러나 세션 범위를 가지는 ShoppingCart는 존재하지 않는다. 사용자와 함께와서 세션이 만들어질 때까지 ShoppingCart 인스턴스는 존재하지 않는다.