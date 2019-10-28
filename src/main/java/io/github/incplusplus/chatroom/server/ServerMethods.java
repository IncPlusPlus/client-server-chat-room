package io.github.incplusplus.chatroom.server;

import io.github.incplusplus.chatroom.shared.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import static io.github.incplusplus.chatroom.shared.Constants.ConstantEnum.IDENTIFY;
import static io.github.incplusplus.chatroom.shared.MiscUtils.decode;
import static io.github.incplusplus.chatroom.shared.MiscUtils.getHeader;
import static io.github.incplusplus.chatroom.shared.StupidSimpleLogger.log;

/**
 * A collection of methods that I moved to here to keep {@link Server} concise.
 */
public class ServerMethods {
	/**
	 * Continuously prompts a client to supply a certain value.
	 * Specifically, this demands a certain type of payload
	 * from a client denoted by some specific header.
	 * This method is used with the following interaction in mind
	 * 1. Server sends payload containing only a header
	 * 2. Client sends back response with corresponding header AND PAYLOAD
	 *
	 * @param serverDemand   the header to send to the client which
	 *                       would indicate the expected response
	 * @param properResponse the correct response header
	 * @param outToClient    a {@link PrintWrite} to the client
	 * @param inFromClient   a {@link BufferedReader} from the client
	 * @return the string content of the reply from the client
	 */
	public static String negotiate(Constants.ConstantEnum serverDemand, Constants.ConstantEnum properResponse,
	                               PrintWriter outToClient, BufferedReader inFromClient) throws IOException {
		//tell the client what we want
		outToClient.println(serverDemand);
		//ingest client response
		String clientResponse = inFromClient.readLine();
		if (!expected(serverDemand, properResponse, clientResponse)) {
			return negotiate(serverDemand, properResponse, outToClient, inFromClient);
		}
		else return decode(clientResponse);
	}
	
	/**
	 * Utility method to safely determine if we received back the expected header.
	 *
	 * @return whether the right header was sent back
	 */
	private static boolean expected(Constants.ConstantEnum serverDemand, Constants.ConstantEnum properResponse,
	                                String clientResponse) {
		String clientHeader = clientResponse;
		if (clientResponse == null) {
			logFailedExpectations(serverDemand, properResponse, null);
			return false;
		}
		else {
			try {
				clientHeader = String.valueOf(getHeader(clientResponse));
				return true;
			}
			catch (IllegalArgumentException e) {
				logFailedExpectations(serverDemand, properResponse, clientHeader);
				return false;
			}
		}
	}
	
	private static void logFailedExpectations(Constants.ConstantEnum serverDemand,
	                                          Constants.ConstantEnum properResponse,
	                                          String actual) {
		log("Expected '" + properResponse + "' for demand '" + serverDemand + "' but got '" + actual + "' instead.");
	}
}
