package io.github.incplusplus.chatroom.server;

import io.github.incplusplus.chatroom.shared.Constants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import static io.github.incplusplus.chatroom.shared.Constants.ConstantEnum.IDENTIFY;
import static io.github.incplusplus.chatroom.shared.MiscUtils.*;
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
	
	/**
	 * Provides a new registration key that does not
	 * yet exist within the current group of clients.
	 *
	 * @param handlers the current handlers
	 * @return a unique int
	 * @throws IllegalStateException if there are too many handlers
	 *                               pending registration.
	 * @implNote Because this method recurses, it would be unwise to
	 * synchronize on the handlers parameter. Therefore YOU MUST
	 * RUN THIS METHOD IN A BLOCK SYNCHRONIZED ON handlers.
	 */
	static int getNewRegKey(List<Server.ClientHandler> handlers) {
		int possiblyUsableKey = randInt(1, 100);
		if (getHandlerForKey(handlers, possiblyUsableKey) == null) {
			return possiblyUsableKey;
		}
		else {
			return getNewRegKey(handlers);
		}
	}
	
	/**
	 * Find a {@link io.github.incplusplus.chatroom.server.Server.ClientHandler} that
	 * contains the provided registration key.
	 *
	 * @param handlers the list of handlers
	 * @param key      a registration key
	 * @return a ClientHandler that contains the provided registration key; else null
	 * @implNote YOU MUST RUN THIS METHOD IN A BLOCK SYNCHRONIZED ON handlers.
	 */
	static Server.ClientHandler getHandlerForKey(List<Server.ClientHandler> handlers, int key) {
		return handlers.stream().filter(clientHandler ->
				clientHandler.clientRegistrationKey == key).findAny().orElse(null);
	}
}
