package com.publicsafety.elasticsearch.utility;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.index.query.QueryBuilders;

import com.publicsafety.entity.ESQuery;

public class ElasticSearchRunQuery {

	public static SearchResponse runSocialMediaQuery(
			ElasticSearchConnection connection, String indexName,
			String indexType, ESQuery esQuery) {
		SearchResponse response = connection.getClient()
				.prepareSearch(indexName).setTypes(indexType)
				.setSearchType(SearchType.QUERY_AND_FETCH)
				.setQuery(QueryBuilders.matchQuery("text", esQuery.getQuery()))
				.setFrom(0).setSize(60).setExplain(true).execute().actionGet();

		return response;
	}

}
