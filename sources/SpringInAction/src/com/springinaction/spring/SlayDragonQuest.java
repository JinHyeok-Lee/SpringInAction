package com.springinaction.spring;

import java.io.PrintStream;

public class SlayDragonQuest implements Quest{
	
	private PrintStream stream;
	
	public SlayDragonQuest(PrintStream stream) {
		this.stream = stream;
	}
	
	public void embark() {
		stream.println("Embarking on Quest to slay the dragon!");
	}
}
