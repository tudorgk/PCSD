package com.acertainbookstore.business;

import com.acertainbookstore.utils.BookStoreException;
import com.acertainbookstore.utils.BookStoreMessageTag;
import com.acertainbookstore.utils.BookStoreResult;
import com.acertainbookstore.utils.BookStoreUtility;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.ByteArrayBuffer;

import java.util.concurrent.Callable;

/**
 * CertainBookStoreReplicationTask performs replication to a slave server. It
 * returns the result of the replication on completion using ReplicationResult
 */
public class CertainBookStoreReplicationTask implements
		Callable<ReplicationResult> {

	private ReplicationRequest request;
	private String url;
	private HttpClient client;


	public CertainBookStoreReplicationTask(ReplicationRequest request, String url, HttpClient client) {
		this.request = request;
		this.url = url;
		this.client = client;
	}
	@Override
	public ReplicationResult call() throws Exception {

		// TODO Auto-generated method stub
		String listISBNsxmlString = BookStoreUtility
				.serializeObjectToXMLString(request.getDataSet());
		Buffer requestContent = new ByteArrayBuffer(listISBNsxmlString);

		ContentExchange exchange = new ContentExchange();
		//add a replication message tag
		String urlString = url
				+ request.getMessageType();
		exchange.setMethod("POST");
		exchange.setURL(urlString);
		exchange.setRequestContent(requestContent);

		System.out.println("Message tag: " + request.getMessageType().toString());
		System.out.println("Client:" + this.client);

		try{
			BookStoreUtility.SendAndRecv(this.client, exchange);
		} catch (BookStoreException e) {
			return new ReplicationResult(url, false);
		}
		return new ReplicationResult(url, true);
	}

}
