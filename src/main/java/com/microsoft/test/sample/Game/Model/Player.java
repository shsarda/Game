package com.microsoft.test.sample.Game.Model;

public class Player {

	String playerId;
	String name;
	Choice choice;
	int score;
	UserConversationState userConversationState;

	Player(String playerId, String name) {
		this.playerId = playerId;
		choice = Choice.NONE;
		score = 0;
		userConversationState = new UserConversationState();
		this.name = name;
	}
	
	public String getPlayerId() {
		return playerId;
	}
	public void setPlayerId(String playerId) {
		this.playerId = playerId;
	}
	public Choice getChoice() {
		return choice;
	}
	public void setChoice(Choice choice) {
		this.choice = choice;
	}
	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public UserConversationState getUserConversationState() {
		return userConversationState;
	}
	public void setUserConversationState(UserConversationState userConversationState) {
		this.userConversationState = userConversationState;
	}
}
