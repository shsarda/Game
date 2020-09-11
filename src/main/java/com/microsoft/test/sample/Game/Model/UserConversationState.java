package com.microsoft.test.sample.Game.Model;

import com.microsoft.bot.schema.ConversationReference;
import com.microsoft.bot.schema.teams.TeamsChannelAccount;

public class UserConversationState {

	TeamsChannelAccount member;
	ConversationReference conversationReference;
	String activityId;
	
	public TeamsChannelAccount getMember() {
		return member;
	}
	public void setMember(TeamsChannelAccount member) {
		this.member = member;
	}
	public ConversationReference getConversationReference() {
		return conversationReference;
	}
	public void setConversationReference(ConversationReference conversationReference) {
		this.conversationReference = conversationReference;
	}
	public String getActivityId() {
		return activityId;
	}
	public void setActivityId(String activityId) {
		this.activityId = activityId;
	}
}
