package com.microsoft.test.sample.Game.Model;

import java.util.HashMap;
import java.util.Map;

public class RPSGame {
	
	Map<String, Player> players;
	int totalRock;
	int totalPaper;
	int totalScissors;
	
	public RPSGame() {
		players = new HashMap<String,Player>();
		totalRock = 0;
		totalPaper = 0;
		totalScissors = 0;
	}
	
	public Player addPlayer(String playerId, String name) {
		Player player = new Player(playerId, name);
		players.put(playerId, player);
		return player;
	}
	
	public Map<String, Player> getPlayers() {
		return players;
	}

	public void setPlayers(Map<String, Player> players) {
		this.players = players;
	}

	public int getTotalRock() {
		return totalRock;
	}

	public void setTotalRock(int totalRock) {
		this.totalRock = totalRock;
	}

	public int getTotalPaper() {
		return totalPaper;
	}

	public void setTotalPaper(int totalPaper) {
		this.totalPaper = totalPaper;
	}

	public int getTotalScissors() {
		return totalScissors;
	}

	public void setTotalScissors(int totalScissors) {
		this.totalScissors = totalScissors;
	}

}
