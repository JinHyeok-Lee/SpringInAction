package com.springinaction.spring;

import org.junit.Test;
import org.springframework.context.support.GenericXmlApplicationContext;

public class BraveKnightTest1_1 {
	
	@Test
	public void knightShouldEmbarkOnQuest() {
		GenericXmlApplicationContext context =
				new GenericXmlApplicationContext("classpath:/resources/spring/Minstrel.xml");
		Knight night = context.getBean(Knight.class);
		night.embarkOnQuest();
	}
}
