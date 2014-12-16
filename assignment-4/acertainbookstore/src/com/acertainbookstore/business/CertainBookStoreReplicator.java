package com.acertainbookstore.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

import com.acertainbookstore.client.BookStoreClientConstants;
import com.acertainbookstore.interfaces.Replicator;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.thread.QueuedThreadPool;

/**
 * CertainBookStoreReplicator is used to replicate updates to slaves
 * concurrently.
 */
public class CertainBookStoreReplicator implements Replicator {
   	private ExecutorService executorService;
	private HttpClient replicationClient;

	public CertainBookStoreReplicator(int maxReplicatorThreads){
		// create an executor service for the requests
		executorService = Executors.newFixedThreadPool(maxReplicatorThreads);

		replicationClient = new HttpClient();
		replicationClient.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
		replicationClient.setMaxConnectionsPerAddress(BookStoreClientConstants.CLIENT_MAX_CONNECTION_ADDRESS);
		replicationClient.setThreadPool(new QueuedThreadPool(BookStoreClientConstants.CLIENT_MAX_THREADSPOOL_THREADS));
		replicationClient.setTimeout(BookStoreClientConstants.CLIENT_MAX_TIMEOUT_MILLISECS);

		try {
			replicationClient.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<Future<ReplicationResult>> replicate(Set<String> slaveServers,
													 ReplicationRequest request) {
		List<Future<ReplicationResult>> results = new ArrayList<Future<ReplicationResult>>();

		for (String server : slaveServers)
		{
			CertainBookStoreReplicationTask task =
					new CertainBookStoreReplicationTask(server, request, replicationClient);
			results.add(executorService.submit(task));
		}

		return results;
	}

}
