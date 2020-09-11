package com.microsoft.test.sample.Game.Util;

import java.util.ArrayList;
import java.util.List;

import com.microsoft.bot.schema.ActionTypes;
import com.microsoft.bot.schema.Attachment;
import com.microsoft.bot.schema.CardAction;
import com.microsoft.bot.schema.HeroCard;

public class CardGeneratorUtil {

	public static Attachment CreateWelcomeCard() {

		List<CardAction> buttons = new ArrayList<CardAction>();
		buttons.add(new CardAction() {{
			setType(ActionTypes.MESSAGE_BACK);
			setTitle("ROCK");
			setText("rock");
		}});
		buttons.add(new CardAction() {{
			setType(ActionTypes.MESSAGE_BACK);
			setTitle("PAPER");
			setText("paper");
		}});
		buttons.add(new CardAction() {{
			setType(ActionTypes.MESSAGE_BACK);
			setTitle("SCISSORS");
			setText("scissors");
		}});

		HeroCard card = new HeroCard() {{
			setTitle("Select Rock/Paper/Scissors");
			setButtons(buttons);
		}};

		return card.toAttachment();

		//		try {
		//			InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream("WelcomeCard.json");
		//			String adaptiveCardJson = IOUtils.toString(input, StandardCharsets.UTF_8.toString());
		//			return new Attachment() {{
		//				setContentType("application/vnd.microsoft.card.hero");
		//                setContent(Serialization.jsonToTree(adaptiveCardJson));
		//			}};
		//		} catch (IOException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//			return new Attachment();
		//		}

	}

	public static Attachment CreateScoreCard(String scoreCard) 
	{
		HeroCard card = new HeroCard() {{
			setTitle("Score Card");
			setText(scoreCard);
		}};

		return card.toAttachment();
	}

}
