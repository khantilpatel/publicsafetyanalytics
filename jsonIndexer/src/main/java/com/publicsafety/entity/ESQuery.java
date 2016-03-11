package com.publicsafety.entity;

import java.util.ArrayList;

public class ESQuery {
	
	String query;
	String queryType;
	ArrayList<ESTweet> esTweets;
	
	public ESQuery(String queryType, String query) {
		super();
		this.query = query;
		this.queryType = queryType;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public ArrayList<ESTweet> getEsTweets() {
		return esTweets;
	}

	public void setEsTweets(ArrayList<ESTweet> esTweets) {
		this.esTweets = esTweets;
	}

	public String getQueryType() {
		return queryType;
	}

	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}
	
	
	
	
	
	

}
