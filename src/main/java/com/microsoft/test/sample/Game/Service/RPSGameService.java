package com.microsoft.test.sample.Game.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.microsoft.bot.builder.BotFrameworkAdapter;
import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.teams.TeamsInfo;
import com.microsoft.bot.connector.authentication.MicrosoftAppCredentials;
import com.microsoft.bot.schema.Activity;
import com.microsoft.bot.schema.Attachment;
import com.microsoft.bot.schema.ChannelAccount;
import com.microsoft.bot.schema.ConversationParameters;
import com.microsoft.bot.schema.ConversationReference;
import com.microsoft.bot.schema.teams.TeamsChannelAccount;
import com.microsoft.test.sample.Game.Controller.PlayerHistoryController;
import com.microsoft.test.sample.Game.Model.Choice;
import com.microsoft.test.sample.Game.Model.Player;
import com.microsoft.test.sample.Game.Model.PlayerHistory;
import com.microsoft.test.sample.Game.Model.RPSGame;
import com.microsoft.test.sample.Game.Util.CardGeneratorUtil;

@Component
public class RPSGameService {

	private RPSGame game;

	@Value("${MicrosoftAppId}")
	private String appId;

	@Value("${MicrosoftAppPassword}")
	private String appPswd;

	public CompletableFuture<Void> StartGame(TurnContext turnContext) {

		InitializeGame();

		// 1. send message to each user
		List<TeamsChannelAccount> listOfTeamMembers;
		try {
			listOfTeamMembers = TeamsInfo.getMembers(turnContext).get();
			listOfTeamMembers.stream()
			.filter(member -> !StringUtils.equals(member.getId(), turnContext.getActivity().getRecipient().getId()))
			.forEach(member -> {
				Player player = game.addPlayer(member.getId(), member.getName());
				try {
					SendMessageToAllPlayers(turnContext, player, member, turnContext.getActivity()).get();
				} catch (InterruptedException | ExecutionException e) {
					turnContext.sendActivity(MessageFactory.text("Something went wrong while sending message to all players. Please try again!"));
				}
			});
		} catch (InterruptedException | ExecutionException e) {
			turnContext.sendActivity(MessageFactory.text("Something went wrong while starting the game. Please try again!"));
			return null;
		}

		return turnContext.sendActivity(MessageFactory.text("Game Started!")).thenApply(send -> null);
	}

	public CompletableFuture<Void> UpdateAllPlayersWithScoreCard(TurnContext turnContext, Choice choice) {

		UpdatePlayerChoice(turnContext.getActivity().getFrom().getId(), choice);
		List<CompletableFuture<Void>> listOfConversations = new ArrayList<CompletableFuture<Void>>();

		if (isGameOver()) {
			String scoreCardString = CalculateTotalScore();
			if (scoreCardString == null)
				return turnContext.sendActivity(MessageFactory.text("Something went wrong while reading/writing in-memory store")).thenApply(send -> null);
			Attachment scoreCard = CardGeneratorUtil.CreateScoreCard(scoreCardString);
			game.getPlayers().values().stream()
			.forEach(player -> {
				BotFrameworkAdapter adapter = (BotFrameworkAdapter) turnContext.getAdapter();
				listOfConversations.add(adapter.continueConversation(
						appId, player.getUserConversationState().getConversationReference(), 
						(context) -> context.sendActivity(MessageFactory.attachment(scoreCard)).thenApply(send -> null)
						));
			});
		}
		return CompletableFuture.allOf(listOfConversations.toArray(new CompletableFuture[0]));
	}

	public boolean isGameOver() {
		for (Player player : game.getPlayers().values()) {
			if (player.getChoice().equals(Choice.NONE))
				return false;
		}
		return true;
	}

	private void InitializeGame() {
		game = new RPSGame();
	}

	private CompletableFuture<Void> SendMessageToAllPlayers(TurnContext turnContext, Player player, TeamsChannelAccount member, Activity activity) {
		String channelId = turnContext.getActivity().getChannelId();
		String serviceUrl = turnContext.getActivity().getServiceUrl();
		MicrosoftAppCredentials credentials = new MicrosoftAppCredentials(appId, appPswd);
		ConversationParameters conversationParameters = new ConversationParameters();
		conversationParameters.setBot(turnContext.getActivity().getRecipient());
		conversationParameters.setIsGroup(false);
		conversationParameters.setMembers(new ArrayList<ChannelAccount>(Arrays.asList(member)));
		conversationParameters.setTenantId(turnContext.getActivity().getConversation().getTenantId());

		BotFrameworkAdapter adapter = (BotFrameworkAdapter) turnContext.getAdapter();

		return adapter.createConversation(channelId, serviceUrl, credentials, conversationParameters, 
				(TurnContext context) -> {
					ConversationReference reference = context.getActivity().getConversationReference();
					player.getUserConversationState().setActivityId(context.getActivity().getId());
					player.getUserConversationState().setConversationReference(reference);
					player.getUserConversationState().setMember(member);
					Attachment welcomeCard = CardGeneratorUtil.CreateWelcomeCard();
					return adapter.continueConversation(appId, player.getUserConversationState().getConversationReference(), 
							(innerContext) -> innerContext.sendActivity(MessageFactory.attachment(welcomeCard))
							.thenApply(send -> null)
							);
				}).thenApply(started -> null);
	}

	private void UpdatePlayerChoice(String playerId, Choice choice) {
		Player player = game.getPlayers().get(playerId);
		player.setChoice(choice);

		switch (choice) {
		case ROCK:
			game.setTotalRock(game.getTotalRock() + 1);
			break;
		case PAPER:
			game.setTotalPaper(game.getTotalPaper() + 1);
			break;
		case SCISSORS:
			game.setTotalScissors(game.getTotalScissors() + 1);
			break;
		default:
			break;
		}
	}

	private String CalculateTotalScore() {
		StringBuilder scoreCard = new StringBuilder();
		PlayerHistory playerHistory = PlayerHistoryController.getPlayerHistoryInstance();
		Map<String, Integer> scoreMap = playerHistory.getPlayerScoreMap();
		for (Player player : game.getPlayers().values()) {
			int score = 0;
			switch (player.getChoice()) {
			case ROCK:
				score = game.getTotalScissors() - game.getTotalPaper();
				break;
			case PAPER:
				score = game.getTotalRock() - game.getTotalScissors();
				break;
			case SCISSORS:
				score = game.getTotalPaper() - game.getTotalRock();;
				break;
			default:
				break;
			}
			scoreCard.append(player.getName() + " : " + score + "</br>");

			int scoreHistory = scoreMap.getOrDefault(player.getName(), 0);
			scoreMap.put(player.getName(), scoreHistory + score);

		}
		playerHistory.setPlayerScoreMap(scoreMap);

		return scoreCard.toString();
	}
}
