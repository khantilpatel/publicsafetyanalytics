package com.publicsafety.mongodb.utility;

import com.mongodb.DBObject;
import com.publicsafety.entity.NewsArticle;

public class TransformNewsArticle {

	public static NewsArticle mongoObjectToNewsArticle(DBObject newsObjectDB) {
		NewsArticle article = new NewsArticle();

		article.setAuthor((String)newsObjectDB.get("author"));
		article.setContent((String)newsObjectDB.get("content"));
		article.setDate((String)newsObjectDB.get("date"));
		article.setLink((String)newsObjectDB.get("link"));
		article.setName((String)newsObjectDB.get("name"));
		article.setTitle((String)newsObjectDB.get("title"));

		return article;
	}

}
