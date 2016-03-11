//package com.java.indexer;
//
//import static org.elasticsearch.node.NodeBuilder.nodeBuilder;
//
//import java.io.IOException;
//import java.io.Serializable;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//
//import org.codehaus.jackson.map.ObjectMapper;
//import org.elasticsearch.action.bulk.BulkRequestBuilder;
//import org.elasticsearch.action.bulk.BulkResponse;
//import org.elasticsearch.action.index.IndexRequestBuilder;
//import org.elasticsearch.client.Client;
//import org.elasticsearch.common.settings.Settings;
//import org.elasticsearch.common.xcontent.XContentBuilder;
//import org.elasticsearch.node.Node;
//
//import com.google.gson.Gson;
//import com.mongodb.DB;
//import com.mongodb.DBCollection;
//import com.mongodb.DBCursor;
//import com.mongodb.DBObject;
//import com.mongodb.MongoClient;
//
//public class JsonIndexer implements Serializable {
//
//	// private static Client client = null;
//
//	/**
//	* 
//	*/
//	private static final long serialVersionUID = 1L;
//	public static String indexName = "socialmedia";
//	public static String indexType = "twitter";
//
//	public JsonIndexer() {
//		// this.client = client;
//	}
//
//	private static Node getNode() {
//		Node node = nodeBuilder().settings(Settings.builder().put("path.home", "C:/elasticsearch-2.2.0"))
//				.settings(Settings.settingsBuilder().put("http.enabled", false)).client(true).node();
//
//		return node;
//	}
//
//	public static void main(String[] args) {
//		try {
//			// Map<String, Object> jsonObjMap = new HashMap<String, Object>();
//			List<Map<String, Object>> jsonObjs = new ArrayList<Map<String, Object>>();
//			JsonIndexer indexer = new JsonIndexer();
//			jsonObjs = indexer.getJsonObjects();
//			/*
//			 * for(int i = 0; i<jsonObjs.size(); i++){ for(Map.Entry<String,
//			 * Object> studentEntry : jsonObjs.get(i).entrySet()){
//			 * System.out.println(studentEntry.getKey() +" :: "+
//			 * studentEntry.getValue()); } System.out.println(
//			 * "**********************************************************"); }
//			 */
//			indexer.bulkIndexTweets(jsonObjs, indexName, indexType);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * Given that streams are processed in bulks, we're making use of the
//	 * ElasticSearch capability to index bulks of documents. It takes a list of
//	 * ids and a list of texts.
//	 *
//	 * @param tweetIds
//	 * @param tweets
//	 * @throws IOException
//	 */
//	public void bulkIndex(List<Object> jsonObjs, String index, String type) throws IOException {
//		Client client = getNode().client();
//
//		// prepare index
//		BulkRequestBuilder bulkRequestBuilder = client.prepareBulk();
//
//		for (int i = 0; i < jsonObjs.size(); i++) {
//			XContentBuilder builder;
//			// ****Step 1 ******//
//			StringBuilder sb = new StringBuilder();
//			sb.append("");
//			sb.append(i);
//			String strId = sb.toString();
//
//			Gson gson = new Gson();
//			String json = gson.toJson(jsonObjs.get(i));
//			// json = json.replaceAll("\\\\", "");
//
//			// ****Step 2 ******//
//			IndexRequestBuilder request = client.prepareIndex(index, type, strId).setSource(json);
//
//			// ****Step 3 ******//
//			bulkRequestBuilder.add(request);
//
//		}
//		BulkResponse bulkResponse = bulkRequestBuilder.execute().actionGet();
//		int items = bulkResponse.getItems().length;
//		System.err.print("indexed [" + items + "] items, with failures? [" + bulkResponse.hasFailures() + "]");
//
//		// BulkResponse bulkResponse = bulkRequest.get();
//		if (bulkResponse.hasFailures()) {
//			System.out.println(bulkResponse.buildFailureMessage());
//		}
//
//		getNode().close();
//	}
//
//	public void bulkIndexTweets(List<Map<String, Object>> lines, String index, String type) {
//		Client client = getNode().client();
//		BulkRequestBuilder requestBuilder = client.prepareBulk();
//
//		for (Map<String, Object> map : lines) {
//
//			//XContentBuilder builder;
//			// try {
//			// builder = jsonBuilder()
//			// .startObject()
//			// .field("text", tweets.get(i))
//			// .field("id", tweetIds.get(i))
//			// .endObject();
//			// } catch (IOException e) {
//			// continue;
//			// }
//			IndexRequestBuilder request = client.prepareIndex(index, type).setIndex(index).setType(type).setSource(map);
//			requestBuilder.add(request);
//		}
//		
//		BulkResponse bulkResponse = requestBuilder.execute().actionGet();
//		int items = bulkResponse.getItems().length;
//		System.err.print("indexed [" + items + "] items, with failures? [" + bulkResponse.hasFailures() + "]");
//
//		if (bulkResponse.hasFailures()) {
//			System.out.println(bulkResponse.buildFailureMessage());
//		}
//		getNode().close();
//	}
//
//	/**
//	 * Read json file and add seperate json objects to list
//	 * 
//	 * @return List
//	 */
//	public List<Map<String, Object>> getJsonObjects() {
//		ObjectMapper mapper = new ObjectMapper();
//
//		List<DBObject> tweets = getAllTweets();
//
//		//Path filePath = Paths.get("k:\\twitter_gps_backups.json");
//		 List<Map<String, Object>> lines = new LinkedList<Map<String,
//		 Object>>();
//
//		for (DBObject dbObject : tweets) {
//			Map<String, Object> map = new HashMap<String, Object>();
//			map.put("id_str", dbObject.get("id_str"));
//			map.put("geo", dbObject.get("geo"));
//			map.put("text", dbObject.get("text"));
//			map.put("created_at", dbObject.get("created_at"));
//			lines.add(map);
//		}
////		if (Files.exists(filePath)) {
////			File file = filePath.toFile();
////			try (BufferedReader in = new BufferedReader(new FileReader(file))) {
////				String line = in.readLine();
////				while (line != null) {
////			
////					Map<String, Object> map = new HashMap<String, Object>();
////					map.put("id_str", tweetMap.get("id_str"));
////					map.put("geo", tweetMap.get("geo"));
////					map.put("text", tweetMap.get("text"));
////					map.put("created_at", tweetMap.get("created_at"));
////					// (line, Status.class);
////					
////					line = in.readLine();
////				}
////			} catch (IOException io) {
////				io.printStackTrace();
////			}
////
////		}
//		return lines;
//	}
//
//	public List<DBObject> getAllTweets() {
//		List<DBObject> tweetsList = new ArrayList<DBObject>();
//		MongoClient mongo = new MongoClient("localhost", 27017);
//		DB db = mongo.getDB("datafusion");
//		DBCollection collection = db.getCollection("tweets_gps");
//		DBCursor cursor = collection.find();
//		while (cursor.hasNext()) {
//			//ssSystem.out.println(cursor.next());
//			DBObject tweetObj = cursor.next();
//			tweetsList.add(tweetObj);
//		}
//		return tweetsList;
//	}
//
//}
