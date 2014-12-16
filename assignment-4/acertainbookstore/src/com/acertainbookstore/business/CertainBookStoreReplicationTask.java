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

		String listISBNsxmlString = BookStoreUtility
				.serializeObjectToXMLString(request);
		Buffer requestContent = new ByteArrayBuffer(listISBNsxmlString);

		ContentExchange exchange = new ContentExchange();
		String urlString = url + "/"
				+ BookStoreMessageTag.REPLICATE;
		exchange.setMethod("POST");
		exchange.setURL(urlString);
		exchange.setRequestContent(requestContent);

		try {
			BookStoreUtility.SendAndRecv(this.client, exchange);
		} catch (BookStoreException e) {
			return new ReplicationResult(url, false);
		}
		return new ReplicationResult(url, true);
	}

}
