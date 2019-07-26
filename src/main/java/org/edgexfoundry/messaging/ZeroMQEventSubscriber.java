/*******************************************************************************
 * Copyright 2017 Dell Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 *
 * @microservice: support-rulesengine
 * @author: Jim White, Dell
 * @version: 1.0.0
 *******************************************************************************/

package org.edgexfoundry.messaging;

import java.io.IOException;

import org.edgexfoundry.domain.core.Event;
import org.edgexfoundry.engine.RuleEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zeromq.SocketType;
import org.zeromq.ZFrame;
import org.zeromq.ZMQ;
import org.zeromq.ZMsg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

/**
 * Export data message ingestion bean - gets messages out of ZeroMQ from export
 * service.
 */
@Component
public class ZeroMQEventSubscriber {

	private static final org.edgexfoundry.support.logging.client.EdgeXLogger logger = org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
			.getEdgeXLogger(ZeroMQEventSubscriber.class);

	public static final String NO_ENVELOPE = "no_envelope";
	public static final String JSON = "application/json";
	public static final String CBOR = "application/cbor";
	public static final String CONTENT_TYPE = "ContentType";

	@Value("${export.zeromq.port}")
	private String zeromqAddressPort;
	@Value("${export.zeromq.host}")
	private String zeromqAddress;
	@Value("${export.client}")
	private boolean exportClient;
	@Value("${expect.serializedjava}")
	private boolean serializedJava;

	@Autowired
	RuleEngine engine;

	private ZMQ.Socket subscriber;
	private ZMQ.Context context;
	private ObjectMapper mapper = new ObjectMapper();

	{
		context = ZMQ.context(1);
	}

	public void receive() {
		getSubscriber();
		JsonNode node;
		ZMsg zmsg;
		ZFrame[] parts;
		logger.info("Watching for new exported Event messages...");
		try {
			while (!Thread.currentThread().isInterrupted()) {
				zmsg = ZMsg.recvMsg(subscriber);
				parts = new ZFrame[zmsg.size()];
				zmsg.toArray(parts);
				logger.debug("Message has " + parts.length + " parts.");

				if (parts.length < 2) {// if the message is not a multi-part message as delivered by core data
					try {
						node = mapper.readTree(parts[0].getData());
					} catch (JsonProcessingException jsonE) {  // if can't parse the data from the message, assume it is CBOR
						processCborEvent(parts[0]);
						break;
					}
				} else // if the message is multi-part message
					node = mapper.readTree(parts[1].getData());
				switch (payloadType(node)) {
				case NO_ENVELOPE:
					processEvent(node);
					break;
				case JSON:
					processJsonEvent(node);
					break;
				case CBOR:
					processCborEvent(node);
					break;
				default:
					logger.error("Unknown payload type received");
					break;
				}
			}
		} catch (Exception e) {
			logger.error("Unable to receive messages via ZMQ: " + e.getMessage());
		}
		logger.error("Shutting off Event message watch due to error!");
		if (subscriber != null)
			subscriber.close();
		subscriber = null;
		// try to restart
		logger.debug("Attempting restart of Event message watch.");

		receive();

	}

	private String payloadType(JsonNode node) throws JsonProcessingException, IOException {
		JsonNode contentType = node.get(CONTENT_TYPE);
		if (contentType == null)
			return NO_ENVELOPE;
		else
			return node.get(CONTENT_TYPE).asText();
	}

	public String getZeromqAddress() {
		return zeromqAddress;
	}

	public void setZeromqAddress(String zeromqAddress) {
		this.zeromqAddress = zeromqAddress;
	}

	public String getZeromqAddressPort() {
		return zeromqAddressPort;
	}

	public void setZeromqAddressPort(String zeromqAddressPort) {
		this.zeromqAddressPort = zeromqAddressPort;
	}

	private ZMQ.Socket getSubscriber() {
		if (subscriber == null) {
			try {
				subscriber = context.socket(SocketType.SUB);
				subscriber.connect(zeromqAddress + ":" + zeromqAddressPort);
				subscriber.subscribe("".getBytes());
			} catch (Exception e) {
				logger.error("Unable to get a ZMQ subscriber.  Error:  " + e);
				subscriber = null;
			}
		}
		return subscriber;
	}

	private void processJsonEvent(JsonNode node) throws IOException {
		logger.info("JSON event received");
		Event event = toEvent(node.get("Payload").binaryValue());
		logger.debug("Event: " + event);
		executeOnEvent(event);
	}

	private void processEvent(JsonNode node) {
		logger.info("Event received");
		Event event = mapper.convertValue(node, Event.class);
		logger.debug("Event: " + event);
		executeOnEvent(event);
	}

	private void executeOnEvent(Event event) {
		engine.execute(event);
		logger.info("Event sent to rules engine for device id:  " + event.getDevice());
	}

	private void processCborEvent(Object data) {
		logger.info("CBOR received.  CBOR is unsupported in the rules engine at this time; message being ignored");
	}

	private static Event toEvent(byte[] eventBytes) throws IOException {
		Gson gson = new Gson();
		String json = new String(eventBytes);
		return gson.fromJson(json, Event.class);
	}
}
