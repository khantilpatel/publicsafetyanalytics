package com.publicsafety.elasticsearch.utility;

import static org.elasticsearch.node.NodeBuilder.nodeBuilder;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;

public class ElasticSearchConnection {
	
	Node node;
	Client client;
	String path_home;
	public ElasticSearchConnection(String path_home) {
		super();
		// TODO Auto-generated constructor stub
		
	this.path_home = path_home;
	}
	
	private static Node getNode() {
		Node node = nodeBuilder()
				.settings(
						Settings.builder().put("path.home",
								"/usr/share/elasticsearch"))
				.settings(Settings.settingsBuilder().put("http.enabled", false))
				.client(true).node();

		return node;
	}
	
	public void connect()
	{
		node = nodeBuilder()
					.settings(
							Settings.builder().put("path.home",path_home
									))
					.settings(Settings.settingsBuilder().put("http.enabled", false))
					.client(true).node();

		client = getNode().client();
	}
	
	public void disconnect()
	{
		
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public void setNode(Node node) {
		this.node = node;
	}
	
	

}
