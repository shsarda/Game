package com.microsoft.test.sample.Game.Model;

import java.util.HashMap;
import java.util.Map;

public class PlayerHistory {
	
	Map<String, Integer> playerScoreMap = new HashMap<String, Integer>();

	public Map<String, Integer> getPlayerScoreMap() {
		return playerScoreMap;
	}

	public void setPlayerScoreMap(Map<String, Integer> playerScoreMap) {
		this.playerScoreMap = playerScoreMap;
	}

	
}
