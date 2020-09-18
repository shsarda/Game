package com.microsoft.test.sample.Game.Bot;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.codepoetics.protonpack.collectors.CompletableFutures;
import com.microsoft.bot.builder.MessageFactory;
import com.microsoft.bot.builder.TurnContext;
import com.microsoft.bot.builder.teams.TeamsActivityHandler;
import com.microsoft.bot.schema.ChannelAccount;
import com.microsoft.bot.schema.HeroCard;
import com.microsoft.bot.schema.teams.MessagingExtensionAttachment;
import com.microsoft.bot.schema.teams.MessagingExtensionQuery;
import com.microsoft.bot.schema.teams.MessagingExtensionResponse;
import com.microsoft.bot.schema.teams.MessagingExtensionResult;
import com.microsoft.test.sample.Game.Model.Choice;
import com.microsoft.test.sample.Game.Service.RPSGameService;
import com.microsoft.test.sample.Game.Util.CardGeneratorUtil;

/**
 * 
 * @author shsarda
 * 
 * Steps to start the game:
 * 1. In the channel, enter command - StartGame
 * 2. Everyone in the channel should receive a card with three options - Rock, Paper, Scissors. Choose one.
 * 3. Once each of the team members reply with their choices, a score card is sent to each player.
 * 
 * Total score is calculated based on the following rule:
 * For each person who beat you, you score -1. For each person you beat, you score 1.
 * The GameBot reports the total score, and no one is eliminated.
 *
 */

@Component
public class GameBot extends TeamsActivityHandler
{
	private static final String START_GAME = "STARTGAME";

	@Autowired
	RPSGameService gameService;
	
	@Override
	protected CompletableFuture<Void> onMessageActivity(TurnContext turnContext) {

		String inputCommand = turnContext.getActivity().removeRecipientMention().toUpperCase();

		if (inputCommand.equals(START_GAME)) {
			return gameService.StartGame(turnContext);
		} 

		turnContext.sendActivity(MessageFactory.text("You choice has been recorded : " + inputCommand));
		Choice choice = Choice.valueOf(inputCommand);
		return gameService.UpdateAllPlayersWithScoreCard(turnContext, choice);
	}

	@Override
	protected CompletableFuture<Void> onMembersAdded(List<ChannelAccount> membersAdded, TurnContext turnContext) {
		String id = turnContext.getActivity().getRecipient().getId();
		return membersAdded.stream()
				.filter(member -> !StringUtils.equals(member.getId(), id))
				.map(channel -> turnContext.sendActivity(MessageFactory.text("Welcome! To start the game, enter \"StartGame\"")))
				.collect(CompletableFutures.toFutureList())
				.thenApply(send -> null);
	}

	@Override
	protected CompletableFuture<MessagingExtensionResponse> onTeamsMessagingExtensionQuery(TurnContext turnContext,
			MessagingExtensionQuery query) {

		MessagingExtensionAttachment attachments = new MessagingExtensionAttachment() {{
			setContentType(HeroCard.CONTENTTYPE);
			setContent(CardGeneratorUtil.MessagingExtenstionCard());
		}};

		MessagingExtensionResult result = new MessagingExtensionResult() {{
			setAttachmentLayout("list");
			setType("result");
			setAttachments(Collections.singletonList(attachments));
		}};

		return CompletableFuture.completedFuture(new MessagingExtensionResponse(result));

	}

	@Override
	protected CompletableFuture<Void> onTeamsMessagingExtensionCardButtonClicked(TurnContext turnContext,
			Object cardData) {
		return gameService.StartGame(turnContext);
	}

}
