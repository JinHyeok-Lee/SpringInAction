package com.springinaction.spring;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class KnightMain {
	
	public static void main(String[] args) {
		ClassPathXmlApplicationContext context = 
				new ClassPathXmlApplicationContext("resources/spring/knight.xml");
		Knight knight = context.getBean("knight",Knight.class);
		knight.embarkOnQuest();
		context.close();
	}
}
