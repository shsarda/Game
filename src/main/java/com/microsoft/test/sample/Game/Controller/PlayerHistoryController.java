package com.microsoft.test.sample.Game.Controller;

import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.microsoft.test.sample.Game.Model.PlayerHistory;

@Controller
public class PlayerHistoryController {
	
	private static PlayerHistory playerHistory = new PlayerHistory();
	
	public static PlayerHistory getPlayerHistoryInstance() {
		return playerHistory;
	}
	
	@ResponseBody
	@RequestMapping(value = "/playerhistory", method = RequestMethod.GET, produces = "application/json")
	public Map<String, Integer> getPlayerHistory() {
		return playerHistory.getPlayerScoreMap();
	}

}
