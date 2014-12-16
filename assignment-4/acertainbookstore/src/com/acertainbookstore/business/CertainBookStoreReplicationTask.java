package com.acertainbookstore.business;

import com.acertainbookstore.utils.BookStoreException;
import com.acertainbookstore.utils.BookStoreMessageTag;
import com.acertainbookstore.utils.BookStoreUtility;
import org.eclipse.jetty.client.ContentExchange;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.io.Buffer;
import org.eclipse.jetty.io.ByteArrayBuffer;
import org.eclipse.jetty.util.thread.QueuedThreadPool;


import java.util.Set;
import java.util.concurrent.Callable;

/**
 * CertainBookStoreReplicationTask performs replication to a slave server. It
 * returns the result of the replication on completion using ReplicationResult
 */
public class CertainBookStoreReplicationTask implements
		Callable<ReplicationResult> {

	private String slaveServer;
	private ReplicationRequest request;
	private HttpClient httpClient;


	public CertainBookStoreReplicationTask(String slaveServer, ReplicationRequest request, HttpClient httpClient) {
		this.slaveServer = slaveServer;
		this.request = request;
		this.httpClient = httpClient;
	}

	@Override
	public ReplicationResult call() throws Exception {
		BookStoreMessageTag messageTag = request.getMessageType();
		String xmlString = BookStoreUtility.serializeObjectToXMLString(request.getDataSet());
		Buffer requestContent = new ByteArrayBuffer(xmlString);
		ContentExchange exchange = new ContentExchange();

		String urlString = slaveServer + messageTag;

		exchange.setMethod("POST");
		exchange.setURL(urlString);
		exchange.setRequestContent(requestContent);

		try {
			BookStoreUtility.SendAndRecv(httpClient, exchange);
		} catch (BookStoreException e) {
			return new ReplicationResult(slaveServer, false);
		}
		// TODO: Do I need to worry about snapshotIDs here?
		return new ReplicationResult(slaveServer, true);

	}

	}
