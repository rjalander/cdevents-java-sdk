package com.cdevents.java.sdk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import io.cloudevents.CloudEvent;
import io.cloudevents.core.message.MessageWriter;
import io.cloudevents.core.v03.CloudEventBuilder;
import io.cloudevents.http.HttpMessageFactory;

public class CDEventsResource {

	public static void main(String args[]) throws IOException {
		
		CloudEvent ceToSend = new CloudEventBuilder()
	            .withId("my-id")
	            .withSource(URI.create("/myClient"))
	            .withType("cd.artifact.created.v1")
	            .withData("{ \"msg\" : \"hello\" }".getBytes(StandardCharsets.UTF_8))
	            .build();
		URL url = new URL("http://localhost:8090/");
        HttpURLConnection httpUrlConnection = (HttpURLConnection) url.openConnection();
        httpUrlConnection.setRequestMethod("POST");
        httpUrlConnection.setDoOutput(true);
        httpUrlConnection.setDoInput(true);
        
        for (String attr: ceToSend.getAttributeNames()) {
			System.out.println("CloudEvent attribute --> " +attr);
		}

        MessageWriter messageWriter = createMessageWriter(httpUrlConnection);
        messageWriter.writeBinary(ceToSend);
        
        if (httpUrlConnection.getResponseCode() / 100 != 2) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ httpUrlConnection.getResponseCode());
		}

        createBufferReader(httpUrlConnection);
        
    }

	private static void createBufferReader(HttpURLConnection httpURLConnection) throws IOException {
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
