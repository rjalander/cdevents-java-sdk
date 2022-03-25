package com.cdevents.java.sdk;

import java.io.IOException;
import java.net.HttpURLConnection;

import io.cloudevents.CloudEvent;

public class CDEventsResource {
	
	public CDEventsResource() {
		
	}

	public static void main(String args[]) throws IOException {
		CloudEvent ceToSend = null;
		String command = args[0]; //artifact-created
		if (command.equals("artifact-created")) {
			String data = args[4];
			ceToSend = CDEventTypes.createArtifactEvent(CDEventEnums.ArtifactCreatedEventV1.getEventType(), args[1], args[2], args[3], data);
		}else if (command.equals("artifact-packaged")) {
			
		}else if (command.equals("artifact-published")) {
			
		}else {
			System.out.println("Invalid command entered");
			return;
		}
		
		HttpURLConnection httpUrlConnection = CDEventsUtils.createHttpURLConnection("http://localhost:8090/");
        
		for (String attr: ceToSend.getAttributeNames()) {
			System.out.println("CloudEvent attribute --> " +attr);
		}

		CDEventsUtils.sendEvent(ceToSend, httpUrlConnection);

		CDEventsUtils.createBufferReader(httpUrlConnection);
        
    }
}
