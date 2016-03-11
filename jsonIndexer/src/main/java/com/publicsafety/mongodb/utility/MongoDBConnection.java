package com.publicsafety.mongodb.utility;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

public class MongoDBConnection {

	String host;
	int port;
	String mongoDB_collection;
	String mongoDB_DB;
	MongoClient mongo;

	public MongoDBConnection(String host, int port, String mongoDB_collection,
			String mongoDB_DB) {
		super();
		this.host = host;
		this.port = port;
		this.mongoDB_collection = mongoDB_collection;
		this.mongoDB_DB = mongoDB_DB;
	}

	public void connect() {
		mongo = new MongoClient(host, port);
		DB db = mongo.getDB(this.mongoDB_DB);
		DBCollection collection = db.getCollection(this.mongoDB_collection);
	}

	public void disconnect() {
		mongo.close();
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getMongoDB_collection() {
		return mongoDB_collection;
	}

	public void setMongoDB_collection(String mongoDB_collection) {
		this.mongoDB_collection = mongoDB_collection;
	}

	public String getMongoDB_DB() {
		return mongoDB_DB;
	}

	public void setMongoDB_DB(String mongoDB_DB) {
		this.mongoDB_DB = mongoDB_DB;
	}

	public MongoClient getMongo() {
		return mongo;
	}

	public void setMongo(MongoClient mongo) {
		this.mongo = mongo;
	}
	
	

}
