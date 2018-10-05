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

import com.google.gson.Gson;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.Base64;

import org.edgexfoundry.domain.core.Event;
import org.edgexfoundry.engine.RuleEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.zeromq.ZMQ;

/**
 * Export data message ingestion bean - gets messages out of ZeroMQ from export service.
 */
@Component
public class ZeroMQEventSubscriber {

  private static final org.edgexfoundry.support.logging.client.EdgeXLogger logger =
      org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
          .getEdgeXLogger(ZeroMQEventSubscriber.class);

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

  {
    context = ZMQ.context(1);
  }

  public void receive() {
    getSubscriber();
    String exportString = null;
    byte[] exportBytes = null;
    Event event;
    logger.info("Watching for new exported Event messages...");
    try {
      while (!Thread.currentThread().isInterrupted()) {
        if (exportClient) {
          if (serializedJava) { // supporting legacy Java export distro that shipped Serialized eventString
            exportString = subscriber.recvStr();
            event = toEvent(exportString);
          } else { // supporting new Go export distro that ships JSON
            exportBytes = subscriber.recv();
            event = toEvent(exportBytes);
          }
        } else {
          exportBytes = subscriber.recv();
          event = toEvent(exportBytes);
        }
        engine.execute(event);
        logger.info("Event sent to rules engine for device id:  " + event.getDevice());
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
        subscriber = context.socket(ZMQ.SUB);
        subscriber.connect(zeromqAddress + ":" + zeromqAddressPort);
        subscriber.subscribe("".getBytes());
      } catch (Exception e) {
        logger.error("Unable to get a ZMQ subscriber.  Error:  " + e);
        subscriber = null;
      }
    }
    return subscriber;
  }

  private Event toEvent(String eventString) throws IOException, ClassNotFoundException {
    byte[] data = Base64.getDecoder().decode(eventString);
    ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data));
    Event event = (Event) in.readObject();
    in.close();
    return event;
  }

  private static Event toEvent(byte[] eventBytes) throws IOException, ClassNotFoundException {
    try {
      Gson gson = new Gson();
      String json = new String(eventBytes);
      return gson.fromJson(json, Event.class);
    } catch (Exception e) {
      // Try to degrade to deprecated serialization functionality gracefully
      ByteArrayInputStream bis = new ByteArrayInputStream(eventBytes);
      ObjectInput in = new ObjectInputStream(bis);
      Event event = (Event) in.readObject();
      return event;
    }
  }

}
