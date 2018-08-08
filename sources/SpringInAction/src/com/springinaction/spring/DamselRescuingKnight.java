package com.springinaction.spring;

public class DamselRescuingKnight implements Knight {
	
	private RescueDamselQuest quest;
	
	public DamselRescuingKnight() {
		quest = new RescueDamselQuest();
	}
	
	@Override
	public void embarkOnQuest() {
		quest.embark();
	}
	
}
