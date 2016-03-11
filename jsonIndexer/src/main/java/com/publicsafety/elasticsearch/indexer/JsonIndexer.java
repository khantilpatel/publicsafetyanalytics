package com.publicsafety.elasticsearch.indexer;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.publicsafety.elasticsearch.utility.ElasticSearchConnection;

public class JsonIndexer implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Configuration for MongoDB
	 */
	String mongoDB_collection = "newstweets";
	String mongoDB_DB = "publicsafety";
	final int LIMIT = 10000;
	/**
	 * Configuration for ElasticSearch
	 */
	String elasticsearch_index = "socialmedia";
	String elasticsearch_type = "twitter";
	String elasicsearch_path_home = "/usr/share/elasticsearch";

	ElasticSearchConnection elasticSearchConnenction;

	public JsonIndexer() {
		// this.client = client;
		elasticSearchConnenction = new ElasticSearchConnection(
				elasicsearch_path_home);
		elasticSearchConnenction.connect();
	}

	public static void main(String[] args) {
		try {
			JsonIndexer indexer = new JsonIndexer();
			indexer.insertFromMongoDBtoES();

			indexer.elasticSearchConnenction.disconnect();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Given that streams are processed in bulks, we're making use of the
	 * ElasticSearch capability to index bulks of documents. It takes a list of
	 * ids and a list of texts.
	 *
	 * @param tweetIds
	 * @param tweets
	 * @throws IOException
	 */
	public void bulkIndex(List<Object> jsonObjs, String index, String type)
			throws IOException {
		Client client = this.elasticSearchConnenction.getClient();

		// prepare index
		BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();

		for (int i = 0; i < jsonObjs.size(); i++) {
			XContentBuilder builder;
			// ****Step 1 ******//
			StringBuilder sb = new StringBuilder();
			sb.append("");
			sb.append(i);
			String strId = sb.toString();

			Gson gson = new Gson();
			String json = gson.toJson(jsonObjs.get(i));
			// json = json.replaceAll("\\\\", "");

			// ****Step 2 ******//
			IndexRequestBuilder request = client.prepareIndex(index, type,
					strId).setSource(json);

			// ****Step 3 ******//
			bulkRequestBuilder.add(request);

		}
		BulkResponse bulkResponse = bulkRequestBuilder.execute().actionGet();
		int items = bulkResponse.getItems().length;
		System.err.print("indexed [" + items + "] items, with failures? ["
				+ bulkResponse.hasFailures() + "]");

		// BulkResponse bulkResponse = bulkRequest.get();
		if (bulkResponse.hasFailures()) {
			System.out.println(bulkResponse.buildFailureMessage());
		}

		// getNode().close();
	}

	public void bulkIndexTweets(List<Map<String, Object>> lines, String index,
			String type) {
		BulkRequestBuilder requestBuilder = this.elasticSearchConnenction
				.getClient().prepareBulk();

		for (Map<String, Object> map : lines) {

			IndexRequestBuilder request = this.elasticSearchConnenction
					.getClient().prepareIndex(index, type).setIndex(index)
					.setType(type).setSource(map);
			requestBuilder.add(request);
		}

		BulkResponse bulkResponse = requestBuilder.execute().actionGet();
		int items = bulkResponse.getItems().length;
		System.err.print("indexed [" + items + "] items, with failures? ["
				+ bulkResponse.hasFailures() + "]");

		if (bulkResponse.hasFailures()) {
			System.out.println(bulkResponse.buildFailureMessage());
		}
	}

	/**
	 * Read json file and add seperate json objects to list
	 * 
	 * @return List
	 */
	public List<Map<String, Object>> insertIntoES(List<DBObject> tweets) {

		// Path filePath = Paths.get("k:\\twitter_gps_backups.json");
		List<Map<String, Object>> lines = new LinkedList<Map<String, Object>>();

		for (DBObject dbObject : tweets) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id_str", dbObject.get("id_str"));
			map.put("geo", dbObject.get("geo"));
			map.put("text", dbObject.get("text"));
			map.put("created_at", dbObject.get("created_at"));
			lines.add(map);
		}

		this.bulkIndexTweets(lines, elasticsearch_index, elasticsearch_type);
		return lines;
	}

	public List<DBObject> insertFromMongoDBtoES() {
		
		List<DBObject> tweetsList = new ArrayList<DBObject>();
		MongoClient mongo = new MongoClient("localhost", 27017);
		DB db = mongo.getDB(this.mongoDB_DB);
		DBCollection collection = db.getCollection(this.mongoDB_collection);
		int numbCollections = 0;
		Object id = null;
		BasicDBObject whereQuery = new BasicDBObject();
		BasicDBObject orderBy = new BasicDBObject("_id", 1);
		int countTotal = collection.find().count();
		do {
			DBCursor cursor = collection.find(whereQuery).limit(LIMIT)
					.sort(orderBy);
			if (id != null) {
				whereQuery.put("_id", new BasicDBObject("$gt", id));
				cursor = collection.find(whereQuery).limit(LIMIT);
			}
			numbCollections = cursor.count();
			while (cursor.hasNext()) {
				// ssSystem.out.println(cursor.next());
				DBObject tweetObj = cursor.next();
				id = tweetObj.get("_id");
				tweetsList.add(tweetObj);
			}
			if (!tweetsList.isEmpty()) {
				insertIntoES(tweetsList);
				tweetsList.clear();
				System.out.println("\n::::: Inserted: "
						+ (countTotal - numbCollections) + "/" + countTotal
						+ " :::::");
			}
		} while (numbCollections > 0);

		System.out.println("\n::::: Done adding " + countTotal
				+ " records into mongoDB! :::::");
		return tweetsList;
	}
}
