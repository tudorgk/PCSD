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
	private HttpClient client;

	public CertainBookStoreReplicator(int maxReplicatorThreads){
		// create an executor service for the requests
		executorService = Executors.newFixedThreadPool(maxReplicatorThreads);

		//instantiate a client to make the requests
		client = new HttpClient();
		client.setConnectorType(HttpClient.CONNECTOR_SELECT_CHANNEL);
		client.setMaxConnectionsPerAddress(maxReplicatorThreads);
		client.setThreadPool(new QueuedThreadPool(maxReplicatorThreads));
		client.setTimeout(BookStoreClientConstants.CLIENT_MAX_TIMEOUT_MILLISECS);
		try {
			client.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<Future<ReplicationResult>> replicate(Set<String> slaveServers,
			ReplicationRequest request) {
		CertainBookStoreReplicationTask rep;
		FutureTask<ReplicationResult> futureTask;
		List<Future<ReplicationResult>> results = new ArrayList<Future<ReplicationResult>>();

		for( String addr : slaveServers){
			rep = new CertainBookStoreReplicationTask(request, addr, client);
			futureTask = new FutureTask<ReplicationResult>(rep);
			executorService.execute(futureTask);
			results.add(futureTask);
		}

		return results;	}

}
