package com.springinaction.beanwireing;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
public class SgtPeppers implements CompactDisc {

	private String title  = "Sgt. Pepper's Lonely Hearts Clud Band";
	private String artist = "The Beatles";
	
	@Override
	public void play() {
		System.out.println("Playing "+title+" by "+artist);
	}
	
}
