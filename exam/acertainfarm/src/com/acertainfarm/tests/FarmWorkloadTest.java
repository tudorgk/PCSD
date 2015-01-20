package com.acertainfarm.tests;

import com.acertainfarm.constants.FarmConstants;
import com.acertainfarm.exceptions.AttributeOutOfBoundsException;
import com.acertainfarm.exceptions.PrecisionFarmingException;
import com.acertainfarm.fieldstatus.interfaces.FieldStatus;
import com.acertainfarm.fieldstatus.proxies.FarmFieldStatusHTTPProxy;
import com.acertainfarm.fieldstatus.server.FarmFieldStatus;
import com.acertainfarm.sensoraggregator.interfaces.SensorAggregator;
import com.acertainfarm.sensoraggregator.proxies.FarmSensorAggregatorHTTPProxy;
import com.acertainfarm.sensoraggregator.server.FarmSensorAggregator;
import com.acertainfarm.tests.workload.FarmWorker;
import com.acertainfarm.tests.workload.FarmWorkerRunResult;
import com.acertainfarm.tests.workload.FarmWorkloadConfig;
import com.acertainfarm.workload.FarmAccessPoint;
import com.acertainfarm.workload.FarmClient;
import org.junit.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by tudorgk on 20/1/15.
 */
public class FarmWorkloadTest {
    private static int numConcurrentWorkloadThreads = 10;
    private static SensorAggregator sensorAggregator;
    private static FieldStatus fieldStatus;
    private static boolean localTest = false;

    private static List<FarmWorkerRunResult> workerRunResults = new ArrayList<FarmWorkerRunResult>();
    private static List<Future<FarmWorkerRunResult>> runResults = new ArrayList<Future<FarmWorkerRunResult>>();

    @BeforeClass
    public static void setUpBeforeClass() {
        try {
            String localTestProperty = System
                    .getProperty(FarmConstants.PROPERTY_KEY_LOCAL_TEST);
            localTest = (localTestProperty != null) ? Boolean
                    .parseBoolean(localTestProperty) : localTest;
            if (localTest) {
                sensorAggregator = new FarmSensorAggregator();
                fieldStatus = new FarmFieldStatus();
            } else {
                sensorAggregator = new FarmSensorAggregatorHTTPProxy(
                        "http://localhost:8081/sensoragg");
                fieldStatus = new FarmFieldStatusHTTPProxy(
                        "http://localhost:8082/fieldstatus");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //init the sensors
        initializeSensorAggregator();
    }



    public static void initializeSensorAggregator(){


//        for (FarmAccessPoint accessPoint : accessPoints){
//            try {
//                accessPoint.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }

    @After
    public void cleanupSensorAggregator(){

    }

    @Test
    public void testWorkload() throws ExecutionException, InterruptedException {

        //create a list of 5 farm access points
        List<FarmAccessPoint> accessPoints = new ArrayList<FarmAccessPoint>();
        for (int i = 0; i < FarmConstants.MAX_NO_ACCESSPOINTS; i++){
            accessPoints.add(new FarmAccessPoint(sensorAggregator,i+1));
        }

        //start the access points
        for (FarmAccessPoint accessPoint : accessPoints){
            accessPoint.start();
        }

        ExecutorService exec = Executors
                .newFixedThreadPool(numConcurrentWorkloadThreads);

        for (int i = 0; i < numConcurrentWorkloadThreads; i++) {
            FarmWorkloadConfig config = new FarmWorkloadConfig(fieldStatus);
            FarmWorker workerTask = new FarmWorker(config);
            // Keep the futures to wait for the result from the thread
            runResults.add(exec.submit(workerTask));
        }

        // Get the results from the threads using the futures returned
        for (Future<FarmWorkerRunResult> futureRunResult : runResults) {
            FarmWorkerRunResult runResult = futureRunResult.get(); // blocking call
            workerRunResults.add(runResult);
        }

        exec.shutdownNow(); // shutdown the executor

        // Finished initialization, stop the clients if not localTest
        if (!localTest) {
            ((FarmFieldStatusHTTPProxy) fieldStatus).stop();
            //stop the access points
            for (FarmAccessPoint ap : accessPoints){
                ap.join(1);
            }
        }

        reportMetric(workerRunResults);
    }

    public static void reportMetric(List<FarmWorkerRunResult> workerRunResults) {
        // DONE: aggregate metrics and output them for plotting here
        double successfulFreqInteractionRuns = 0;
        double totalFreqInteractionRuns = 0;
        double time = 0;
        for (FarmWorkerRunResult result : workerRunResults) {
            successfulFreqInteractionRuns  += result.getSuccessfulFrequentFarmInteractionRuns();
            totalFreqInteractionRuns += result.getTotalFrequentFarmInteractionRuns();
            time += result.getElapsedTimeInNanoSecs();
        }
        //average seconds elapsed for workers
        double averageSeconds = (time / workerRunResults.size()) / 1000000000.0f; //10^9
        double goodput  = successfulFreqInteractionRuns  / averageSeconds;
        double throughput = totalFreqInteractionRuns / averageSeconds;
        double latency = averageSeconds / (successfulFreqInteractionRuns  / workerRunResults.size());

		System.out.println(
				"Workers: "+workerRunResults.size() +
				", Goodput/Aggregate throughput: " + goodput +
				", Throughput: " + throughput +
				", Latency: " + latency);

//        System.out.println(
//                "" + workerRunResults.size() +
//                        "\t" + goodput +
//                        "\t" + throughput +
//                        "\t" + latency);

    }

    @AfterClass
    public static void tearDownAfterClass() throws AttributeOutOfBoundsException,PrecisionFarmingException {

    }
}
