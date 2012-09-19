package org.jclouds.bugs;

import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jclouds.ContextBuilder;
import org.jclouds.blobstore.BlobStoreContext;
import org.jclouds.cloudfiles.CloudFilesAsyncClient;
import org.jclouds.cloudfiles.CloudFilesClient;
import org.jclouds.rest.RestContext;

public class ObjectInfo  implements ServletContextListener{
	private final String cloudUsername = "user";
	private final String cloudPassword = "key";
	private final String provider = "cloudfiles-us";
	private BlobStoreContext blobStoreContext;
	private RestContext<CloudFilesClient, CloudFilesAsyncClient> restContext;
	private CloudFilesClient rackspaceClient;

	public void init() {
		System.out.println("started");
		setupCloudClients();
		getNameOfFirstContainerThatIsNotFullAndPositionTheCounterAtItsIndex();
		System.out.println("ended");
	}


	private String getNameOfFirstContainerThatIsNotFullAndPositionTheCounterAtItsIndex(){
		AtomicInteger counter= new AtomicInteger(-1);
	    String containerName = null;
	    boolean pointingAtExistingFullContainer = true;
	    while(pointingAtExistingFullContainer) {
	        counter.incrementAndGet();
	        containerName = "temp_dev"+counter;
	        pointingAtExistingFullContainer = doesContainerExist(containerName) && isContainerFull(containerName);
	    } 
	    return containerName;
	}
	
	private boolean doesContainerExist(String containerName) {
		return blobStoreContext.getBlobStore().containerExists(containerName);
	}

	private boolean isContainerFull(String containerName){
		return blobStoreContext.getBlobStore().countBlobs(containerName)>300000;
	}
	
	private void setupCloudClients() {
		ContextBuilder contextBuilder = setupContextBuilder();
		setupBlobStoreContext(contextBuilder);
		setupRackpaceClient(contextBuilder);
	}

	private void setupRackpaceClient(ContextBuilder contextBuilder) {
		restContext = contextBuilder.build();
		rackspaceClient = restContext.getApi();
	}

	private void setupBlobStoreContext(ContextBuilder contextBuilder) {
		blobStoreContext = contextBuilder.build(BlobStoreContext.class);
	}

	private ContextBuilder setupContextBuilder() {
		ContextBuilder contextBuilder = ContextBuilder.newBuilder(provider);
		contextBuilder.credentials(cloudUsername, cloudPassword);
		return contextBuilder;
	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		init();
		
	}

}
