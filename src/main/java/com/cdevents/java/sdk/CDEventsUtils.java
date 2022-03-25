package com.cdevents.java.sdk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.UUID;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.core.v03.CloudEventBuilder;
import io.cloudevents.http.HttpMessageFactory;

public class CDEventsUtils {
	
	public static HttpURLConnection createHttpURLConnection(String urlToConnect)
			throws MalformedURLException, IOException, ProtocolException {
		URL url = new URL(urlToConnect);
        HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
        httpUrlConnection.setRequestMethod("POST");
        httpUrlConnection.setDoOutput(true);
        httpUrlConnection.setDoInput(true);
		return httpUrlConnection;
	}

	public static CloudEvent createArtifactEvent(String eventType, String artifactid, String artifactname, String artifactversion) {
		CloudEvent ceToSend = new CloudEventBuilder()
	            .withId(UUID.randomUUID().toString())
	            .withSource(URI.create("java-cli"))
	            .withType(CDEventEnums.ArtifactCreatedEventV1.getEventType())
	            .withData("{ \"msg\" : \"hello client\" }".getBytes(StandardCharsets.UTF_8))
	            .withTime(OffsetDateTime.now())
	            .withExtension("artifactid", artifactid)
	            .withExtension("artifactname", artifactname)
	            .withExtension("artifactversion", artifactversion)
	            .build();
		return ceToSend;
	}

	public static void sendEvent(CloudEvent ceToSend, HttpURLConnection httpUrlConnection) throws IOException {
		MessageWriter messageWriter = createMessageWriter(httpUrlConnection);
        messageWriter.writeBinary(ceToSend);
        
        if (httpUrlConnection.getResponseCode() / 100 != 2) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ httpUrlConnection.getResponseCode());
		}
	}

	public static void createBufferReader(HttpURLConnection httpURLConnection) throws IOException {
		try(BufferedReader br = new BufferedReader(
      		  new InputStreamReader(httpURLConnection.getInputStream(), "utf-8"))) {
      		    StringBuilder response = new StringBuilder();
      		    String responseLine = null;
      		    while ((responseLine = br.readLine()) != null) {
      		        response.append(responseLine.trim());
      		    }
      		    System.out.println(response.toString());
      		}
	}
	private static MessageWriter createMessageWriter(HttpURLConnection httpUrlConnection) {
        return HttpMessageFactory.createWriter(
            httpUrlConnection::setRequestProperty,
            body -> {
                try {
                    if (body != null) {
                        httpUrlConnection.setRequestProperty("content-length", String.valueOf(body.length));
                        try (OutputStream outputStream = httpUrlConnection.getOutputStream()) {
                            outputStream.write(body);
                        }
                    } else {
                        httpUrlConnection.setRequestProperty("content-length", "0");
                    }
                } catch (IOException t) {
                    throw new UncheckedIOException(t);
                }
            });
    }
}
