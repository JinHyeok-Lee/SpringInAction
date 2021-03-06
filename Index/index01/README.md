# 1. 코어 스프링

> 목차
1. [자바 개발 간소화](#자바-개발-간소화)
2. [빈을 담는 그릇, 컨테이너](#빈을-담는-그릇,-컨테이너)
3. [스프링의 현황](#스프링의-현황)

스프링이 제공하는 기능은 매우 다양하지만, 핵심을 파고들면 스프링의 주요 기능은 종속 객체 주입(DI)와 애스펙트 지향 프로그램(AOP)으로 귀결된다.  
  
1장 스프링 속으로에서는 DI와 AOP의 기본적인 개념을 살펴보고, 두 기능이 애플리케이션 객체간의 결합도를 줄이는 데 어떻게 기여 하는지 알아본다.  
  
2장 빈 와이어링에서는 애플리케이션 컴포넌트들에 대해서 더 깊이 살펴본다. 스프링에서 제공되는 자동 설정, 자바 기반 설정 XML 선정 옵션에 대해 알아본다.  
  
3장 고급 와이어링에서는 오토와이어링, 스코핑 및 스프링 표현언어를 취급하고 조건적 설정 방법을 포함하여 스프리으이 최대 효과를 이끌어 낼 수 있는 몇 가지 트릭과 테크닉을 소개한다.  

4장 애스펙트 지향 스프링에서는 스프링의 AOP기능을 이용해 시스템 전반에 걸친 서비스와 그 서비스를 받는 객체 간의 결합도를 줄이는 방법을 살펴본다.  
그리고 4장에서는 9장, 13장, 14장에서 다루는 서술적 보안과 캐싱을 위한 스프링 AOP레버리지 방법의 기초를 다룬다.

- - - -

>이장에서 다룰 내용

* 스프링의 빈 컨테이너
* 스프링 코어 모듈 살펴보기
* 더 훌룡해진 스프링 에코 시스템
* 스프링의 새로워진 점

- - - -

자바 개발자가 되기에 좋은 시절이다.  

## 자바 개발 간소화

스프링은 로드 존슨의 책 Expert One-on-One을 통해 소개한 오픈 소스 프레임워크로서 엔터프라이즈 애플리케이션 개발의 복잡함을 해소하기 위해 만들어 졌다.  
스프링은 EJB로만 할 수 있었던 작업을 평범한 자바빈을 사용해 가능하게 한다.  
하지만 스프링이 서버 측 개발에만 유용한 것은 아니다.  
스프링을 사용하는 모든 자바 애플리케이션은 간소함, 테스트 용이, 낮은 결합도라는 이득을 얻는다.  

스프링이 애플리케이션 컴포넌트를 참조하면서 빈과 자바빈을 사용하지만, 반드시 자바빈의 스펙을 따라야 한다는 것을 의미하지는 않는다. 스프링 컴포넌트는 POJO 타입을 가진다. 여기서는 자바빈을 느슨하게 정의하며, POJO와 동일하다고 간주한다.  
  
이 책을 통해 스프링의 다양한 내용을 살펴보겠지만, 스프링이 제공하는 거의 모든 기본 사상은 몇 가지 기초적인 개념으로 귀결되며, 이는 스프링의 기본 임무인 '자바 개발 간소화'의 모든 초점을 맞춘다.
  
이것이 핵심이다. 특정 기능을 간소화 하는 프레임워크는 많이 존재한다. 하지만 스프링의 목적은 자바 개발을 폭넓게 간소화 하는데 있다.
이 내용에 관하여서는 더 많은 설명이 필요하다.
그러면 어떻게 자바 개발을 간소화 할 수 있을까?  

자바 복잡도 간소화를 지원하기 위해 스프링은 네 가지 주요 전략을 사용한다.  

* POJO를 이용한 가볍고 비 침투적인 개발  
* DI와 인터페이스 지향을 통한 느슨한 결합도
* 애스펙트와 공통 규약을 통한 선언적 프로그래밍
* 애스펙트와 템플릿을 통한 반복적인 코드제거

스프링이 수행하는 거의 모든 작업은 이 네 가지 전략 중 하나 이상에 속한다.
앞으로 스프링이 자바 개발을 간소화 하는 방법에 대한 구체적인 예제를 살펴보면서 이와 같은 사상을 확장해 나갈 것이다.  
먼저 스프링이 POJO 지향 개발을 통해 어떻게 침투적 개발을 최소화 할 수 있는지부터 알아보자.  

### POJO의 힘

장기간의 자바 개발 경험이 있다면 인터페이스의 구현이나 클래스의 확장을 강요하는 프레임워크를 본 적이 있고, 심지어 이런 프레임워크로 작업한 경험도 있으리라 생각된다.  
이런 침략적인 프로그래밍 모델의 쉬운 예는 EJB 2시대의 무상태 세션 빈이다.  
초창기 EJBs가 침투적 프로그래밍의 쉬운 예라고는 하지만, 침투적 프로그래밍은 스트러츠와 웹워크 태피스트리 및 수 많은 다른 자바 명세와 프레임워크 초기 버전에서 쉽게 발견된다.  
  
스프링은 API를 이용하여 애플리케이션 코드의 분산을 가능한 한 막는다. 스프링은 스프링에 특화된 인터페이스 구현이다. 스프링 자체에 의존성이 높은 클래스 확장을 거의 요구하지 않는다. 스프링 기반 애플리케이션의 클래스에는 스프링이 사용한다는 표시고 거의 없다. 최악의 경우, 클래스에 스프링 애너테이션이 붙지만 그렇지 않은 경우에는 POJO이다.

이해를 위해 아래의 HelloWorldBean 클래스를 살펴보자.
  
[예제](./../../sources/SpringInAction/src/com/springinaction/spring/HelloWorldBean.java)
  
보다시피 이것은 간단하고 흔한 자바 클래스, 즉 POJO다.
스프링 컴포넌트라고 가리키는 것외에 마땅한 특징은 없다. 스프링의 비 침투적 프로그래밍 모델에서, 이 클래스는 스프링 애플리케이션 외에서도 잘 동작한다.  

간단한 형태에세도 불구하고 POJO는 매우 강력하다. 스프링이 POJO에 힘을 불어 넣는 방법 중 하나는 DI를 활용하는 조립이다. 그럼 DI를 통해 애플리케이션 객체 상호 간의 결합도를 낮추는 방법을 알아보자.

### 종속객체 주입

종속 객체 주입이라는 문구가 다소 위협적으로 들려서 복잡한 프로그래밍 기술의 개념이나 디자인 패턴이 떠오른다. 하지만 DI는 생각 만큼 복잡하지 않다. 사실 프로젝트에 DI를 적용해 보면 코드가 훨씬 더 간단해지고 이해하기 쉬우며, 테스트하기도 쉬워진다.

#### DI동작 방법

HelloWorld예제보다 복잡한 실제 애플리케이션에서는 두 개 이상의 클래스가 서로 협력하여 비즈니스 로직을 수행한다. 이떄 각 객체는 협력하는 객체에 대한 레퍼런스를 얻을 책임이 있다. 그 결과 결합도가 높아지고 테스트하기 힘든 코드가 만들어지기 쉽다.  

예를 들어 코드 1.2에 있는 Knight 클래스를 생각해 보자.  
[예제](./../../sources/SpringInAction/src/com/springinaction/spring/DamselRescuingKnight.java)

보다시피 DameslRescuingKnight는 생성자 안에 RescueDamselQuest를 생성한다. 이것은 DameselRescuingKnight가 RescueDamselQuest와 강하게 결합되도록 하며, Knight의 원정 출정 목록을 심각하게 제한한다. 도움이 필요한 여성이 있는 곳에 기사도 있다. 하지만 용을 물리처야 하거나 원탁이 필요하다면, 또한 순회하려면 그냥 앉아만 있어야 한다.  

게다가 DameslRescuingKnight에 대한 단위 테스트를 작성하기도 몹시 어렵다. 단위 테스트에서는 기사의 embarkOnQuest() 메서드가 호출 될 떄 quest의 embark() 메서드 호출을 확인하고 싶다. 하지만 여기서는 이를 수행하는 명확한 방법이 없다, 안타깝게도 DamselRescuingKnight는 테스트 하지 못한 채로 남아 있다.  

결합도가 높은 코드는 한편으로 테스트와 재활용이 어렵고 이해하기도 어려우며, 오류를 하나 수정하면 다른 오류가 발생하는 경향도 있다. 반면에 전혀 결합이 없는 코드는 아무것도 할 수 없다. 무언가를 쓸 만한 일을 하려면 클래스들끼리 어떻게든 서로 알고 있어야 한다. 결합은 필요하지만 주의해서 관리해야 한다.  

DI를 이용하면 객체는 시스템에서 각 객체를 조율하는 제 3자에 의해 생성 시점에 종속객체가 의존성이 부여된다. 객체는 종속객체를 생성하거나 얻지 않아야 되며, 종속객체는 종속객체가 필요한 객체에 등록 되는 것이 DI이다.  

이 의미를 설명하기 위해 코드 아래 예제에 있는 BraveKnight를 살펴보자. 이 클래스는 용감할 뿐만 아니라 발생한 어떤 종류의 원정도 떠날 수 있다.  
[예제](./../../sources/SpringInAction/src/com/springinaction/spring/BraveKnight.java)  

보다시피 DamselRescuingKnight와 달리 BraveKnight는 자신의 원정을 생성하지 않는다. 그 대신 생성 시점에 생성자 인자에 원정이 부여 된다. 이와 같은 종류의 종속객체 주입은 생성자 주입 이다.  

개다가 부여된 원정은 모든 원정 구현 인터페이스인 Quest 타입으로 제공된다. 따라서 BraveKnight는 RescueDamselQuest, SlayDragonQuest 등 부여된 다른 Quest를 구현할 수 있다.  

요점은 BraveKnight가 Quest의 특정 구현체에 결합되지 않는다는 사실이다. Quest인터페이스를 구현하기만 하면 기사에서 어떤 종류의원정을 떠나고록 요청하든 문제가 되지 않는다. 이 것이 바로 DI의 주요 이점인 느슨한 결합도이다. 어떤 객체가 자신이 필요러 하는 종속 객체를 인터페이스를 통해서만 알고 있다면 사용하는 객체 쪽에서 아무런 변경 없이 종속 객체(의존성)을 다른 구현체로 바꿀 수 있다.  

실제 종속 객체를 바꾸는 가장 일반적인 방법 중 하나는 테스트 하는 동안의 모의 구현체를 이용하는 것이다. 강한 결합도로 인해 DamselRescuingKnight를 적절히 테스트할 수 없지만. 아래 예제와 같이 Quest의 모의 구현체를 제공하여 BraveKnight를 쉽게 테스트 할 수 있다.  
[예제](./../../sources/SpringInAction/src/com/springinaction/spring/BraveKnightTest.java)  

여기서는 Quest 인터페이스의 모의 구현체를 만들기 위해 Mockito로 알려진 모의 객체 프레임워크를 사용했다. 모의 객체가 생겼으면 BraveKnight의 새로운 인스턴스를 생성하고 생성자를 통해 모의 Quest를 주입한다. embarkOnQuest 메서드를 호출한 후에는 Mockito에게 Quest의 embark메서드가 정확히 한 번 호출 됐는지 확인한다.  

#### 기사에게 원정 임무 주입

이렇게 해서 BraveKnight 클래스는 모든 원정 임무를 부여받을 수있게 됐다. 그러면 이제 BraveKnight 클래스에 어떤 Quest를 부여할지를 어떻게 지정할 수있을까? 예를 들어 BraveKnight가 용을 물리치는 원정에 출정하길 원한다고 가정하자. 아마도 아래 예제에 보이는 SlayDragonQuest가 적절할 것이다.  
[예제](./../../sources/SpringInAction/src/com/springinaction/spring/SlayDragonQuest.java) 

보다시피 SlayDragonQuest는 Quest 인터페이스를 구현하지만 BraveKnight에 적합하다. 또한 대부분의 시작용 자바 샘플처럼 System.out.println()에 의존하기보다는 SlayDragonQuest는 좀더 일반적인 방법으로 생성자를 통해 PringStream에 요청한다는 것을 눈치챘을지도 모르겠다. 여기서 중요한 질문은 바로 "어떻게 BraveKnight에게 SlayDragonQuest를 줄 수 있을 가?"그리고 어떻게 SlayDragonQuest에게 PrintStream을 줄 수 있을까 하는 것이다.  

애플리케이션 컴포넌트 간의 관계를 정의 하는 것을 와이어링 이라고 한다. 스프링에서 컴포넌트를 와이링 하는 방법은 여러 가지가 있지만. 가장 일반적인 방법은 XML을 이용하는 방법이다. 아래 코드는 스프링 설정 파일을 작성하는 방법이다.  
[예제](./../../sources/SpringInAction/src/com/springinaction/spring/KnightConfig.java) 

여기서 BraveKnight와 SlayDragonQuest가 스프링의 빈으로 선언되었다. BraveKnight빈의 경우 생성되면서 레퍼런스를 SlayDragonQuest빈에게 넘겨주며 생성자의 인자가 된다. 반면 SlayDragonQuest 빈 선언은 스프링의 표현언어를 사용하여 System.out을 SlayDragonQuest의 생성자에게 넘긴다.  

만약 XML 설정 방법이 마음에 들지 않으면 스프링에서는 자바를 이용하여 설정 할 수 있다.
그 예로 아래 예제는 XML을 이용한 설정과 동일하다.  
[예제](./../../sources/SpringInAction/src/com/springinaction/spring/SlayDragonQuest.java) 

XML 기반의 설정이든 자바 기반의 설정이든 DI의 이점은 같다. 비록 BraveKnight가 Quest에 의존적이기는 하지만 어떤 타입의 Quest가 주어질지 또는 그 Quest가 어디에서부터 올지는 모르는 것이다. 마찬가지로 SlayDragonQuest가 PrintStream에 의존적이기는 하지만. PrintStream이 어떻게 돌아 가는지 알고서 코딩되는 것은 아니다. 오직 스프링만이 모든 조각이 어떻게 합쳐지는지 설정을 통해 아는 것이다. 이것을 통하여 종속된 클래스를 수정하지 않으면서도 종속성 수정이 가능하다.  

이 예제는 스프링에서 빈을 와이어링 하는 간단한 예를 보여준다. 지금은 자세한 내용에 신경 쓰지 않아도 된다. 자세한 내용은 2장에서 스프링이 빈을 와이어링하는 또 다른 방법과 스프링이 자동으로 빈을 찾고 빈의 간계를 생성하는 바법을 살펴볼 예정이다.  

BraveKnight와 Quest 사이의 관계를 정의 했으니 XML 설정 파일을 로드하여 애플리케이션을 구동해 볼 차례다.

#### 실행해 보기

스프링 애플리케이션에서 애플리케이션 컨텍스트 는 빈에 관한 정의들을 바탕으로 빈들을 엮어 준다.  
스프링 애플리케이션 컨텍스트는 애플리케이션을 구성하는 객체의 생성과 과이어링을 전적으로 책임진다.  
스프링에서는 애플리케이션의 여러 구현체가 존재하며, 각각의 주요 차이점은 오직 설정을 로드하는 방법에 있다.  

knight.xml에서 빈들이 XML파일에 선언되어 있을 떄, 애플리케이션 컨텍스트로 ClassPathXmlApplicationContext()를 사용하면 좋다(자바기반 설정에서 스프링은 AnnotationConfigApplicationContext을 제공한다.) 이 스프링 컨텍스트 구현체는 애플리케이션의 클래스 패스에 있는 하나 이상의 XML 팡일에서 스프링 컨텍스트를 로드한다. 코드 1.8에 있는 main() 메서드는 ClassPathXmlApplicationContext를 사용해 knight.xml을 로드하고 Knight 객체에 대한 레퍼런스를 얻는다.  
[예제](./../../sources/SpringInAction/src/com/springinaction/spring/KnightMain.java)  
여기서 main() 메서드는 knight.xml 파일을 기반으로 스프링 애플리케이션 컨텍스트를 생성한다. 그런 다음 Knight.class의 구현체를 조회하기 위해 팩토리로 애플리케이션 컨텍스트를 사용한다. Knight 객체에 대한 레퍼런스를 얻은 후에 간단히 embarkOnQuest() 메서드를 호출해 기사를 주어진 원정의 길로 떠나 보낸다. 이 클래스는 기사가 어떤 유형의 Quest를 떠나는 지에 대해서는 아무것도 알지 못한다. BraveKnight를 다룬다는 사실도 알지 못한다. knight.xml 파일만이 어떤 구현체인지 확실히 알고 있다.  

지금까지 DI를 간단히 살펴보았다. 앞으로도 이 책을 통해 DI에 대한 더 많은 내용을 알아보겠다. 하지만 DI를 더 자세히 알아 보고 싶다면 DI를 더 상세히 다루는 단지 R프라사나의 Dependency Injection을 읽어 보기 바란다.  

이제는 또 다른 스프링의 자바 간소화 전량인 애스팩트를 통한 선언적 프로그래밍을 알아보자.  

### 애스펙트 적용

DI가 소프트웨어 컴포넌트의 결합도를 낮춰 준다면, 에스펙트 지향 프로그래밍은 애플리케이션 전체에 걸쳐 사용되는 기능을 재사용할 수 있는 컴포넌트에 담는다.  

애스펙트 지향 프로그래밍은 소프트웨어 시스템 내부의 관심사들을 서로 분리(SOC)하는 기술이라고 설명한다. 시스템은 보통 특정한 기능을 책임지는 여러 개의 컴포넌트로 구성된다. 그러나 각 컴포넌트는 대채로 본연의 특정한 기능 외에 로깅이나 트랜잭션관리, 보안 등의 시스템 서비스도 수행해야 하는 경우가 많다. 이러한 시스템 서비스는 여러 컴포넌트에 관련되는 경향이 있으므로 횡단 관심사라고 한다.  

이러한 관심사가 여러 컴포넌트에 퍼지게 되면 코드는 다음 두 가지 차원에서 복잡해진다.

1. 시스템 전반에 걸친 관심사를 구현하는 코드가 여러 컴포넌트에 중복되어 나타난다. 이 때 문제는 이 관심사의 구현을 변경해야 하는 경우 여러 컴포넌트를 모두 변경해야 한다는 점이다. 이 관심사를 별도의 모듈로 추상화해서 각 컴포넌트에서 하나의 메서드만 호출할 수 있도록 만든다고 하더라도, 여전히 이 메소드가 여러 컴포넌트에 중복되어 나타나는 문제는 동일하다.
2. 컴포넌트의 코드가 본연의 기능과 관련 없는 코드로 인해 지저분해진다. 주소록에 주소를 등록하는 메소드는 보안 상태가 유지됬는지 아닌지에는 신경 쓸 필요 없이 주소를 등록하는 방법에만 관여하는 것이 좋다.

아래 그림은 위의 설명한 복잡성을 그림으로 나타냔 것이다. 왼쪽에 있는 비즈니스 객체들은 시스템 서비스와 매우 긴밀하게 관련을 맷고 있다. 각 객체는 본연의 책임을 수행할 뿐만 아니라 로깅과 보안, 트랜잭션 컨텍스트에 대해서도 파악해야 한다.

<img src="./../../sources/SpringinAction/src/resources/image/1-1.png"></img>

AOP는 시스템 서비스를 모듈화해서 컴포넌트에 선언적으로 적용한다. AOP를 이용하면 시스템 서비스에 대해서는 전혀 알지 못하지만. 응집도가 높고 본연의 관심사에 집중하는 컴포넌트를 만든다. 다시 말해 애스펙트는 확실히 POJO를 단순화한다.  

아래 그림에서 묘사하고 있는 것처럼 애스펙스를 애플리케이션의 여러 컴포넌트를 덮는 담요처럼 생각하면 개념을 잡는 데 도움이 된다. 이 그림에서 애플리케이션의 핵심은 비즈니스 기능을 구현하는 모듈들로 구성되어 있고, AOP를 이용해서 이 핵심 기능을 구현하는 모듈에는 아무런 변화도 가하지 않고 추가적인 기능을 선언적으로 적용한다. 이 개념은 매우 강력한 개념으로서 보안, 트랜잭션, 로깅 등을 처리하고, 애플리케이션의 핵심 비즈니스 로직이 지저분해지는 것을 막는다.  

기사 예제로 다시 돌아가, 기본적인 스프링 애스펙트를 추가해보면서 스프링에서 애스펙트를 적용하는 방법을 구체적으로 살펴보자.  

<img src="./../../sources/SpringinAction/src/resources/image/1-2.png"></img>

#### AOP실습
음유시인으로 알려지고 음악을 좋아하는 이야기꾼에 의해 기사의 업적이 노래로 기록되므로 음유시인의 서비스를 이용하여 BraveKnight의 출정과 복귀를 기록하고 싶다고 가정해보자. 사용할 Minstrel 클래스의 코드는 아래와 같다.  
[코드](./../../sources/SpringInAction/src/com/springinaction/spring/Minstrel.java)

보다시피 Minstrel은 두 개의 메소드가 있는 간단한 클래스다. singBeforeQuest() 메서드는 기사가 원정을 떠나기 전에 호출되며. singAfterQuest() 메서드는 기사가 원정을 ㄷ마친 후에 호출된다. 두 가지 경우 모두에서 Minstrel은 생성자를 통해 주입된 PrintStream을 거쳐 기사의 행동을 노래한다.  

코드에서 작업하기 위해서는 간단해야 한다. BraveKnight에다 주입만 하면 된다. 안 그런가? Minstrel을 사용하기 위해서 BraveKnight를 적절히 수정하다. 아래 코드는 첫 번째 시도로 BraveKnight와 Minstrel을 합치는 것을 보여 준다.  
[코드](./../../sources/SpringInAction/src/com/springinaction/spring/BraveKnight1_1.java)  
기교를 부려 봤다, 이제 필요한 것은 스프링 설정으로 가서 Minstrel의 빈을 선언하고 그것을 BraveKnight 빈의 생성자에 주입하는 것이다.  
그렇지만 일부는 적절해 보이지 않는다. 정말고 기사의 관심 범위 내에서 음유시인을 관리해야 할까? 음유시인은 요청 없이도 그의 일을 수행한다. 궁극적으로 음유시인의 일은 기사의 업을에 대해 노래하는 것이다. 왜 기사가 음유시인을 상기시켜야 하는가?  

게다가 기사는 음유시인에 대해서 알아야 하므로 강제로 Minstrel을 BraveKnight에 주입한다. 이는 BraveKnight의 코드를 복잡하게 만들 뿐만 아니라 음유시인이 없는 기사를 원하는 경우 당황스러워진다. 만일 Minstrel이 null이라면? 이와 같은 상황을 다루기 위해 몇 가지 null 체크 로직을 도입해야 할까?  

간단한 BraveKnight 클래스가 점차 복잡해지기 시작하고 nullMinstrel 시나리오까지 처리야하 한다면 그 복잡함은 더해진다. 하지만 AOP를 사용하면 기사의 원정에 대해 노래할 음유시인을 선언할 수 있으며, 기사는 Minstrel 메서드를 직접 처리해야 하는 일에서 해방된다.  

Minstrel을 애스펙트로 바꾸려면 스프링 설정 파일의 하나로 선언하기만 하면 된다. 여기서는 knight.xml 파일을 수정하여 Minstral을 애스펙트로 선언하였다.  

[예제](./../../sources/SpringInAction/src/resources/spring/minstrel.xml)  

여기서 스프링의 aop 설정 네임스페이스를 사용하여 Minstrel빈이 애스펙트라고 선언한다. 먼저 Minstrel을 빈으로 선언한다 그런 다음 `<aop:aspect>` 요소에서 빈을 참조한다. 애스펙트를 더 정의해 보자면, embarkOnQuest() 메소드가 실행되기 전에 Minstrel의 singBeforeQuest() 메소드가 호출되어야 한다고 선언한다. 이것을 비포 어브바이스라고 부른다. 그리고 embarkOnQuest() 메소드가 실행한 후에 singAfterQuest() 메소드가 호출되어야 한다고 선언한다. 이것을 애프터 어드바이스라고 부른다. 양쪽 경우 모두 point-ref 애트리뷰트는 embark라는 이름의 포인트컷을 참조한다. 이 포인트컷은 앞에 있는 `<pointcut>` 요소에 어드바이스가 적용될 위치를 선택하는 expression 애트리뷰트와 함께 정의되어 있다. expression 구문은 AspectJ의 포인트컷 표현식언어다.  

AspectJ나 AspectJ 포인트컷 표현식의 작성 바법을 자세히 모른다고 해도 걱정할 필요는 없다. 4장에서 스프링의 AOP에 대해 저 자세히 논의 하며 지금은 스프링으 BraveKinght가 원정을 떠나기 전과 후에 Minstrel의 singBeforeQuest()와 singAfterQuest() 메소드를 호출한다는 정도만 알아도 충분하다.

이렇게 해서 몇 중 안되는 XML설정으로 Minstrel을 스프링 애스펙트로 바꿧다.지금 완전히 이해가 되지 않더라도 걱정할 필요는 없다. 4장에서 다양한 스프링 AOP 예제를 통해 충분히 이해할 수 있는 시간을 갖게 된다. 지금은 이 예제에서 다음에 설명하는 두 가지만 분명히 알고 넘어 가면 된다.  

첫 번째는 Minstrel이 여전이 POJO라는 점이다. Minstrel이 애스펙트로 사용될 것임을 나타내는 내용은 Minstrel에 전혀 포함되어 있지 않다. 스프링 컨텍스트에서 선언적으로 애스펙트가 된다.  

두 번째이면서 가장 중요한 점은 BraveKnight가 Minstrel을 명시적으로 호출하지 않아도 Minstrel이 BraveKnight에 적용된다는 사실이다. 실제로 BraveKnight는 Minstrel의 존재를 전혀 인식하지 못한다.  

또한 중요한 것은 Minstrel을 애스펙트로 바꾸기 위해 몇 가지 스프링 마법을 사용했지만. 먼저 `<bean>`으로 선언되어애 한다는 사실이다. 여기서의 핵심은 종속 객체 주입 같은 다음 스프링 빈과 함꼐 수행 할 수 있는 스프링 애스팩트를 이용하면 무엇이든 가능하다는 점이다.  

기사에 대해 노래하기 위해 애스펙트를 사용하는 일도 즐거울 수 있지만 스프링의 AOP는 더 실용적인 목적으로 사용된다. 나중에 보겠지만 스프링 AOP는 선언적 트랜잭션과 보안 등의 서비스를 제공하지 위해 도입한다.  

하지만 지금은 스프링이 자바 개발을 간소화시켜 주는 또 하나의 방법을 알아보자.  

#### 템플릿을 이용한 상투적 코드 제거

코드를 작성하다가 이전에 이미 작성했던 코드 같다고 느꺄 본 적이 있는가? 이것은 데자뷰가 아니다. 이것이 바로 상투적인 코드 이다. 즉 공통 작업이나 간단한 작업을 위헤 반복적으로 작성해야 하는 코드이다. 안타깝게도 자바 API에는 상투적인 코드가 많이 포함되어 있다. 상투적 코드의 대표적인 예는 데이터베이스의 데이터를 조회하는 JDBC작업이다. 이전에 JDBC로 작업 한 적이 있다면 다음과 유사한 코드를 작성해야 하는 경우를 본적이 있을 것이다.  

[예제](./../../sources/SpringInAction/src/com/springinaction/spring/JdbcSample.java)

보다시피 JDBC 코드는 직원의 이름과 급여를 데이터베이스에서 조회한다. 하지만 조회하는 부분을 찾으려면 열심히 살펴봐야 한다. 몇 줄 안되는 직원 조회 코드가 JDBC 형식의 더미에 묻혀 버렸기 떄문이다. 먼저 커넥션을 생성한 후에 질의객체를 생성하여 최종적으로 결과를 조회한다. 그리고 예외를 던졌을 때 수행할 수 있는 작업이 많지 않아도 JDBC의 분노를 달래기 위해 검사형 예외인 SQLException이 발생한다.  

마지막으로 모든 작업을 완료한 후에는 커넥션과 질의객체(PrepareStatment) 그리고 결합객체(Resultset)를 닫는 정리 작업을 수행해야 한다. 이 또한 분노를 일으킨다. 따라서 여기서도 별도로 SQLException을 잡아 줘야 한다.  

코드 1.11에서 가중 주목할 부분은 거의 모든 JDBC 작업을 위해 작성했던 코드와 완벽히 동일하다는 사실이다. 직원 조회를 수행하는 작업은 극히 일부분이다. 대부분은 JDBC의 상투적인 코드다.  

상투적인 코드 작업에 JDBC만 있는 것은 아니다 많은 기능은 종종 유사한 상투적인 코드를 요구한다. JMS, JNDI 그리고 REST 서비스의 소비에는 수 많은 공통적인 반봅 코드가 포함되어 있다.  

스프링은 템플릿에 상투적인 코드를 캡슐화하여 반복적인 코드를 제거하는 방법을 찾는다. 스프링으 JdbcTemplate은 전통적인 JDBC에서 필요한 모든 형식 없이도 데이터베이스 작업을 수행할 수 있게 한다.  

예를 들어 스프링의 JdbcTemplate은 사용하며 JDBC API가 요구하는 작업이 아는 직원 데이터를 조회하는 작업에 초점을 맞추도록 getEployerById() 메서드는 아래 코드와 같다.  

[예제](./../../sources/SpringInAction/src/com/springinaction/spring/SpringJdbcTemplate.java)

보다시피 새롭게 수정된 getEmployeeById() 는 매우 간단하면서도 실제로 데이터베이스에서 직원을 조회하는 작업에 초점을 마준다. 템플릿의 queryForObject() 메소드는 SQL 쿼리와 RowMapper() 그리고 쿼리 파라미터가 부여된다.
getEmployeeById() 에서는 이전에 보았던 JDBC의 상투적인 코드가 보이지 않는다. 모든 것은 템플릿 내부에서 처리된다.  

지금까지 POJO 지향 개발, DI, 에스펙트 그리고 템플릿을 이용하여 자바 개발의 복잡성을 공격하는 방법을 살펴보았다. 또한 XML기반 설정 파일에서 빈과 애스펫트를 설정하는 방법도 살펴보았다. 그런데 이러한 파일을 어떻게 로드할 수 있을까? 이제부터 애플리케이션의 빈이 위치하는 스프링 컨테이너를 살펴보자.  

### 빈을 담는 그릇, 컨테이너

스프링 기반 애플리케이션에서는 스프링 컨테이너 안에서 객체가 태어나고, 자라고 소멸한다. 아래 그림과 같이 스프링 컨테이너는 객체를 생성하고, 서로 엮어주고, 이들의 전체 생명주기를 관리한다.  

<img src="./../../sources/SpringinAction/src/resources/image/1-3.png"></img>

스프링 컨테이너가 어떤 객체를 생성하고 서로 엮어 줘야 하는지 알 수 있게 설정하는 방법은 다음 장에서 설명하겠다. 우선은 객체들의 삶의 터전인 스프링 컨테이너 자체부터 이해 하는 것이 더 중요하다. 스프링 컨테이너에 대한 이해를 토대로 객체가 어떻게 관리되는지를 이해해야 한다.  

스프링 컨테이너는 스프링 프레임워크의 핵심부에 위치한다. 스프릉컨테이너는 종속객체 주입을 이용해서 애플리케이션을 구성하는 컴포넌트를 관리하며, 협력 컴포넌트 간 연관관계의 형성도 여기에서 이뤄진다. 이러한 짐을 컨테이너에 덜어 버린 객체들은 더 명확하고, 이해하기 쉬우며, 재사용을 촉진하고, 단위 테스트가 용이해진다.  

스프링 컨테이너는 여러 가지가 있다. 스프링에서는 여러 컨테이너 구혀네가 존재하며, 이들은 크게 두 가지로 분류된다. 첫 번쨰 부류는 빈 팩토리로 이는 DI에 대한 기본적인 자원을 제공하는 가장 단순한 컨테이너디. 두 번쨰 부류는 애플리케이션 컨텍스트로 빈 팩토리를 확장해 프로퍼티 파일에 텍스트 메시지를 일고 해당 이벤트 리스너에 대한 이벤트 발행 같은 애플리케이션 프레임워크 서비스를 제공하는 컨테이너이다.  

스프링으로 작업할 떄 빈 팻토리나 애플리케이션 컨텍스트 중에 아무거나 사용해도 되지만. 빈 팩토리는 대부분의 애플리케이션에 대하여 지나치게 저수준의 기능을 제공한다. 따라서 빈 팻토리 보다는 애플리케이션 컨텍스트를 더 선호한다. 따라서 앞으로는 애플리케이션 컨텍스트에 초점을 맞춰 작업할 예정이며, 빈 팩토리에 대해서는 많은 시간을 할해하지 않겠다.  

#### 또 하나의 컨테이너, 애플리케이션 컨텍스트

스프링에는 다양한 종류의 애플리케이션 컨텍스트가 있다. 그 중에서 가장 많이 접하게 될 몇가지는 다음과 같다.  

* AnnotationConfigApplicationContext - 하나 이상의 자바 기반 설정 클래스에서 스프링 애플리케이션 컨텍스트를 로드한다.
* AnnotationConfigWebApplicationContext - 하나 이상의 자바 기반 설정 클래스에서 스프링 웹 애플리케이션 컨텍스트를 로드한다.
* ClassPathXmlApllicationContext - 클래스패스에 위치한 XML파일에서 컨텍스트 저으이 내용을 로드한다.
* FileSystemXmlApplicationContext - 파일 시스템에서 파일 경로로 지정된 XML 파일에서 컨텍스트 정의 내용을 로드한다.  
* XmlWebApplicationContext - 웹 애플리케이션에 포함된 XML 파일에서 컨텍스트 정의 내용을 로드한다.

AnnotationConfigWebApplicationContext와 xmlWebApplicationContext는 8장에서 웹 기반 스프링 애플리케이션을 설명할 떄 살펴보기로 한다. 지금은 간단히 FileSystemXmlApplicationContext를 이용해서 파일 시스템에서 애플리케이션 컨텍스트를 로드하거나. ClassPathXmlApplicationContext를 이용해서 클래스 패스에서 애플리케이션 컨텍스트를 로드하자.  

파일 시스템이나 클래스 패스에서 애플리케이션 컨텍스트를 로드 하는 방법은 빈 팩토리 떄와 유사하다. 다음은 FileSystemXmlApplicationContext를 로드하는 코드다.

<pre>
    ApplicationContext context = 
        new FileSystemXmlApplicationContext("c:/Knight.xml");
</pre>
클래스 패스에서 로드하는 ClassPathXmlApplicationContext도 다를 바 없다.
<pre>
    ApplicationContext context =
        new ClassPathXmlApplicationContext("Knight.xml");
</pre>
둘 사이에 차이가 있다면 FileSystemXmlApplicationContext의 경우 knight을 파일 시스템에서 지정된 경로 "c:\knight"에서 찾고 ClassPathXmlApplicationContext는 클래스패스에 포함된 모든 경로에서 찾으려 한다는 점이다.  

그렇지 않고 자바 설정에서 애플리케이션 컨텍스트를 로드하려고 한다면. AnnotationConfigApplicationContext를 사용한다.
<pre>
    ApplicationContext context = new AnnotationConfigApplicationContext(KnightConfig.class);
</pre>
스프링 애플리케이션 컨텍스트를 로드하기 위한 XML 파일을 명시하는 대신, 빈을 로드하기 위한 설정 클래스를 AnnotationConfigApplicationContext가 받았다.  

애플리케이션 컨텍스트를 얻은 다음, 컨텍스트의 getBean() 메서드를 호출하여 스프링 컨테이너에서 빈을 조회한다.  

이제 스프링 컨테이너를 생성하는 기초 방법을 익혔으니 컨테이너 내부 빈의 생명주기를 좀더 살펴보자.  

#### 빈의 일생

보통의 자바 애플리케이션에서 빈의 생명주기는 매우 단순하다. 자바의 new 키워드를 이용해 빈을 인스턴스화하고 이를 바로 사용한다. 빈이 더 이상 사용되지 않으면 가비지 컬렉션 후보가 되어 언젠가는 메모리 덩어기가 됬다가 허공속으로 사라질 것이다.  

빈면에 스프링 컨테이너 내에서 빈의 생명주기는 좀 더 정교하다. 빈이 생성될 떄 스프링이 제공하는 커스터마이징 기회를 이용하려면 스프링의 빈 생명 주기를 이해해야 한다. 아래 그림은 BeanFactory 컨테이너 내에서 빈이 갖는 구동 생명주기를 보야준다.  

<img src="./../../sources/SpringinAction/src/resources/image/1-4.png"></img>

보다시피 빈 펙토리는 빈이 사용 가능한 상태가 되기 전에 몇 가지 준비 과정을 수행한다 그림의 각 과정을 상세히 설명하면 다음과 같다.  

1. 스프링이 빈을 인스턴스화한다.
2. 스프링이 값과 빈의 레퍼런스를 빈의 프로퍼티에 주입힌다.
3. 빈이 BeanNameAware를 구현하면 스프링이 빈의 ID를 setBeanName() 메서드에 넘긴다.
4. 빈이 BeanFactoryAware를 구현하면 setBeanFactory() 메서드를 호출하여 빈 팩토리 자체를 넘긴다.
5. 빈이 ApplicationContextAware를 구현하면 스프링이 setApplicationContext() 메서드를 호출하고 둘러싼 애플리케이션 컨텍스트에 대한 참조를 넘긴다.
6. 빈이 BeanPostProcessor 인터페이스를 구현하면 postProcessBeforeInitialization() 메소드를 호출한다.
7. 빈이 InitializingBean 인터페이스를 구현하면 스프링은 afterPropertiesSet() 메소드를 호출한다. 마찬가지로 빈인 init-method와 함꼐 선언됐으면 지정한 초기화 메소드가 호출된다.
8. 빈이 BeanPostProcessor를 구현하면 스프링은 postProcessAfterInitialization() 메소드를 호출한다.
9. 이 상태가 되면 빈은 애플리케이션에서 사용할 분비가 된 것이며, 애플리케이션 컨텍스트가 소멸될 때까지 애플리케이션 컨텍스트로 남아 있다.
10. 빈이 DisposableBean 인터페이스를 구현하면 스프링은 destroy() 메소드를 호출한다. 마찬가지로 빈이 destroy-method와 한께 선언됐으면 지정된 메소드가 호출된다.

이제 스프링 컨테이너가 어떻게 생성되고 로드 되는지 알게 됬지만 우리는 아직 컨테이너에 아무것도 넣지 않았으므로 별 쓸모가 없다. 스프링 DI를 활용하려면 컨테이너에 애플리케이션 객체를 넣고 서로 연결해 줘야 한다, 빈 와이어링은 2장에서 좀 더 상세히 살펴보겠다.  

우선 스프링 프레임워크가 무엇으로 구성되어 있고 최신 버전의 스프링이 제공하는 기능이 무엇인지 현재의 스프링 현황을 조사해 보자.  