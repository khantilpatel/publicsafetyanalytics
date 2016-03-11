package com.publicsafety.entity;

import java.util.ArrayList;

public class NewsArticle {

	String title;
	String content;
	String name;
	String link;
	String date;
	String Author;

	ArrayList<ESQuery> esQueries;

	public NewsArticle() {
		super();
		esQueries = new ArrayList<ESQuery>(0);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getAuthor() {
		return Author;
	}

	public void setAuthor(String author) {
		Author = author;
	}

	public ArrayList<ESQuery> getEsQueries() {
		return esQueries;
	}

	public void setEsQueries(ArrayList<ESQuery> esQueries) {
		this.esQueries = esQueries;
	}

	public void addQuery(ESQuery e) {
		esQueries.add(e);
	}

	public boolean isNull() {
		boolean result = false;
		if ( this.content == null || this.title == null
				|| this.date == null || this.name == null || this.link == null) {
			result = true;
		}
		return result;
	}

	@Override
	public String toString() {
		return "NewsArticle [title=" + title + ", content=" + content
				+ ", name=" + name + ", link=" + link + ", date=" + date
				+ ", Author=" + Author + "]";
	}

}
