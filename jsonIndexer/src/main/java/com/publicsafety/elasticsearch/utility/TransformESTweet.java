package com.publicsafety.elasticsearch.utility;

import java.util.ArrayList;
import java.util.Map;

import org.elasticsearch.search.SearchHit;

import com.publicsafety.entity.ESTweet;

public class TransformESTweet {

	public static ArrayList<ESTweet> transformRawTweettoESTweet(SearchHit[] results) {

		ArrayList<ESTweet> esTweets = new ArrayList<ESTweet>(0);
		for (SearchHit hit : results) {
			//System.out.println("------------------------------");
			Map<String, Object> result = hit.getSource();
			//System.out.println("Score:" + hit.getScore() + " result:" + result);
			ESTweet esTweet = new ESTweet();
			esTweet.setScore(hit.getScore());
			// esTweets.setText((String)result.get("text"));
			esTweet.setDate((String) result.get("text"));
			esTweet.setTweet_id((String) result.get("id_str"));

			esTweets.add(esTweet);
		}
		
		return esTweets;
	}
}
