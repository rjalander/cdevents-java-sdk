package com.cdevents.java.sdk;

import java.io.IOException;
import java.net.HttpURLConnection;

import io.cloudevents.CloudEvent;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(name = "artifact")
public class ArtifactCDEvents implements Runnable {
	
	@Command(name = "created")
	public Integer artifactCreated(
			@Option(names = "--id", description = "artifact id", required = true) String artifactid,
			@Option(names = "--name", description = "artifact name", required = true) String artifactname,
			@Option(names = "--version", description = "artifact version", required = true) String artifactversion
			) throws IOException {
		CloudEvent ceToSend = CDEventsUtils.createArtifactEvent(null, artifactid, artifactname, artifactversion);
		HttpURLConnection httpUrlConnection = CDEventsUtils.createHttpURLConnection("http://localhost:8090/");
        
		for (String attr: ceToSend.getAttributeNames()) {
			System.out.println("CloudEvent attribute --> " +attr);
		}
		CDEventsUtils.sendEvent(ceToSend, httpUrlConnection);
		return 0;
	}
	
	@Override
	public void run() {
		System.out.println("Sending artifact events.");
	}

}
