package com.publicsafety.elasticsearch.search;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import java.util.Map;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.node.Node;
import org.elasticsearch.search.SearchHit;

public class TestFullTextSearch {
	
	public static String indexName = "socialmedia";
	public static String indexType = "twitter";
	

	public static void main(String[] args) {
		//Node node = nodeBuilder().node();
		
		Client client = getNode().client();
		
		searchDocument(client, indexName, indexType, "text", "youth crimes");
		
	}

	public static void searchDocument(Client client, String index, String type,
			String field, String value) {
		SearchResponse response = client.prepareSearch(index).setTypes(type)
				.setSearchType(SearchType.QUERY_AND_FETCH)
				.setQuery(QueryBuilders.matchQuery(field, value)).setFrom(0).setSize(60)
				.setExplain(true).execute().actionGet();
		SearchHit[] results = response.getHits().getHits();
		System.out.println("Current results: " + results.length);
		for (SearchHit hit : results) {
			System.out.println("------------------------------");
			Map<String, Object> result = hit.getSource();
			System.out.println(result);
		}
	}
	
	private static Node getNode() {
		Node node = nodeBuilder().settings(Settings.builder().put("path.home", "/usr/share/elasticsearch"))
				.settings(Settings.settingsBuilder().put("http.enabled", false)).client(true).node();

		return node;
	}
}
