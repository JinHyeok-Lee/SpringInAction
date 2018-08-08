package com.springinaction.spring;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.Test;

public class BraveKnightTest {
	@Test
	public void knightShouldEmbarkOnQuest() {
		Quest quest = mock(Quest.class);
		assertNotNull(quest);
		BraveKnight knight = new BraveKnight(quest);
		knight.embarkOnQuest();
	}

}
