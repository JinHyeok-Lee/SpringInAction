package com.springinaction.spring;

public class BraveKnight1_1 implements Knight {
	
	private Quest quest;
	private Minstrel minstrel;
	
	public BraveKnight1_1(Quest quest, Minstrel minstrel) {
		super();
		this.quest = quest;
		this.minstrel = minstrel;
	}


	@Override
	public void embarkOnQuest() {
		minstrel.singBeforeQuest();
		quest.embark();
		minstrel.singAfterQuest();
	}
}
