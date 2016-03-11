package com.publicsafety.elasticsearch.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.publicsafety.elasticsearch.utility.ElasticSearchConnection;
import com.publicsafety.elasticsearch.utility.ElasticSearchRunQuery;
import com.publicsafety.elasticsearch.utility.TransformESTweet;
import com.publicsafety.entity.ESQuery;
import com.publicsafety.entity.ESTweet;
import com.publicsafety.entity.NewsArticle;
import com.publicsafety.mongodb.utility.MongoDBConnection;
import com.publicsafety.mongodb.utility.TransformNewsArticle;

public class FullTextSearch {

	String host = "127.0.0.1";
	int port = 27017;
	String mongoDB_collection = "rss_feeds";
	String mongoDB_DB = "publicsafety";
	public static String indexName = "socialmedia";
	public static String indexType = "twitter";
	String elasicsearch_path_home = "/usr/share/elasticsearch";
	ElasticSearchConnection elasticSearchConnection;
	MongoDBConnection mongoDBConnection;

	public FullTextSearch() {
		super();

		elasticSearchConnection = new ElasticSearchConnection(
				elasicsearch_path_home);
		elasticSearchConnection.connect();

		mongoDBConnection = new MongoDBConnection(host, port,
				mongoDB_collection, mongoDB_DB);
		mongoDBConnection.connect();
	}

	public static void main(String[] args) {

		FullTextSearch fullTextSearch = new FullTextSearch();

		fullTextSearch.fetchMongoDB();
		// fullTextSearch.searchDocument(indexName, indexType, "text",
		// "youth crimes");
	}

	public void retriveSocialMedia(NewsArticle article) {

		ESQuery q1_title = new ESQuery("Title", article.getTitle());
		ESQuery q2_author = new ESQuery("Author", article.getAuthor());
		ESQuery q3_content = new ESQuery("Content", article.getContent());
		ESQuery q4_link = new ESQuery("Link", article.getLink());

		// 1. Search for q1_title
		SearchResponse response = ElasticSearchRunQuery.runSocialMediaQuery(
				elasticSearchConnection, indexName, indexType, q1_title);
		SearchHit[] results = response.getHits().getHits();
		q1_title.setEsTweets(TransformESTweet
				.transformRawTweettoESTweet(results));
		article.addQuery(q1_title);

		// 2. Search for q2_author
		if (q2_author.getQuery() != null) {
			response = ElasticSearchRunQuery.runSocialMediaQuery(
					elasticSearchConnection, indexName, indexType, q2_author);
			results = response.getHits().getHits();
			q2_author.setEsTweets(TransformESTweet
					.transformRawTweettoESTweet(results));
			article.addQuery(q2_author);
		}
		// 3. Search for q2_author
		response = ElasticSearchRunQuery.runSocialMediaQuery(
				elasticSearchConnection, indexName, indexType, q3_content);
		results = response.getHits().getHits();
		q3_content.setEsTweets(TransformESTweet
				.transformRawTweettoESTweet(results));
		article.addQuery(q3_content);

		// 4. Search for q2_author
		response = ElasticSearchRunQuery.runSocialMediaQuery(
				elasticSearchConnection, indexName, indexType, q4_link);
		results = response.getHits().getHits();
		q4_link.setEsTweets(TransformESTweet
				.transformRawTweettoESTweet(results));
		article.addQuery(q4_link);

	}

	public void performLateFusion(NewsArticle newsArticle) {
		ArrayList<ESQuery> esQueries = newsArticle.getEsQueries();

		HashMap<String, Double> scoreList = new HashMap<String, Double>(0);

		ArrayList<String> processedQueryTypes = new ArrayList<String>(0);

		for (ESQuery esQuery : esQueries) {
			ArrayList<ESTweet> esTweets = esQuery.getEsTweets();
			String currentQueryType = esQuery.getQueryType();

			if (!processedQueryTypes.contains(currentQueryType)) {
				processedQueryTypes.add(currentQueryType);
				for (ESTweet esTweet : esTweets) {
					String current_tweet_id = esTweet.getTweet_id();
					if (!scoreList.containsKey(current_tweet_id)) {
						scoreList.put(current_tweet_id, esTweet.getScore());
					}
					for (ESQuery esQuery_nest : esQueries) {
						if (!processedQueryTypes.contains(esQuery_nest
								.getQueryType())) {
							// if (currentQueryType !=
							// esQuery_nest.getQueryType()) {
							ArrayList<ESTweet> esTweets_nest = esQuery_nest
									.getEsTweets();
							for (ESTweet esTweet_nest : esTweets_nest) {
								if (current_tweet_id.equals(esTweet_nest
										.getTweet_id())) {
									// Check and update the Map
									if (scoreList.containsKey(current_tweet_id)) {
										double updateScore = scoreList
												.get(current_tweet_id);
										updateScore += esTweet_nest.getScore();
										// System.out.println(current_tweet_id+":: "+updateScore);
										scoreList.put(current_tweet_id,
												updateScore);
									}

								}

							}
							// }
						}
					}
				}
			}
		}

		Map<String, Double> sortedMap = sortByComparator(scoreList);
		double aggregatedScore = 0;
		for (Map.Entry<String, Double> entry : sortedMap.entrySet()) {

			if (entry.getValue() >= 0.5) {
				aggregatedScore += entry.getValue();
			}
		}
System.out.println("*********************************************************");
		System.out.println(" Social influence Score " + aggregatedScore
				+ " | Article: " + newsArticle.getTitle() + " | Content: "
				+ newsArticle.getContent());

	}

	private static Map<String, Double> sortByComparator(
			Map<String, Double> unsortMap) {

		// Convert Map to List
		List<Map.Entry<String, Double>> list = new LinkedList<Map.Entry<String, Double>>(
				unsortMap.entrySet());

		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1,
					Map.Entry<String, Double> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});

		// Convert sorted map back to a Map
		Map<String, Double> sortedMap = new LinkedHashMap<String, Double>();
		for (Iterator<Map.Entry<String, Double>> it = list.iterator(); it
				.hasNext();) {
			Map.Entry<String, Double> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	public void fetchMongoDB() {
		DB db = mongoDBConnection.getMongo().getDB(this.mongoDB_DB);
		DBCollection collection = db.getCollection(this.mongoDB_collection);
		DBCursor cursor = collection.find();

		// 1. Get the list of news

		while (cursor.hasNext()) {
			// ssSystem.out.println(cursor.next());
			DBObject tweetObj = cursor.next();
			NewsArticle newsArticle = TransformNewsArticle
					.mongoObjectToNewsArticle(tweetObj);

			// 2. For each news, generate queries for elasticsearch and store
			// the
			// result in arraylist of arraylist.
			if (!newsArticle.isNull()) {
				this.retriveSocialMedia(newsArticle);
				performLateFusion(newsArticle);
			} else {
				System.out.println("FOUND NULL::" + newsArticle);
			}
			// 3. Skiping this step

			// 4. Determine overall score

		}

		// 3. Process the arraylist in each news item, to determine the
		// Derichlet
		// Score.

		//

	}
}
