package com.cdevents.java.sdk;

import java.io.IOException;

import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(name = "cde", subcommands = {ArtifactCDEvents.class}, description = "Produces Cloud Events related to Continuous Delivery", mixinStandardHelpOptions = true, version = "cde 1.0")
public class CDEventsResourceV2 implements Runnable {

	public static void main(String args[]) throws IOException {
		int rc = new CommandLine(new CDEventsResourceV2()).execute(args);
	    System.exit(rc);
	}

	@Override
	public void run() {
	    System.out.println("Subcommand needed");
	}
}
