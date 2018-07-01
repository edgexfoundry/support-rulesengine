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

package org.edgexfoundry.export.registration;

import javax.annotation.PostConstruct;
import javax.ws.rs.NotFoundException;

import org.edgexfoundry.domain.export.ExportDestination;
import org.edgexfoundry.domain.export.ExportFormat;
import org.edgexfoundry.domain.export.ExportRegistration;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.Protocol;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ExportClientImpl implements ExportClient {

  private static final org.edgexfoundry.support.logging.client.EdgeXLogger logger =
      org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
          .getEdgeXLogger(ExportClientImpl.class);

  @Value("${export.client}")
  private boolean exportClient;

  @Value("${export.client.registration.url}")
  private String url;

  @Value("${export.client.registration.name}")
  private String clientName;

  @Value("${export.client.registration.retry.time}")
  private int retryTime;

  @Value("${export.client.registration.retry.attempts}")
  private int retryAttempts;

  @PostConstruct
  public void init() throws InterruptedException {
    if (exportClient) {
      int tries = 0;
      do {
        tries++;
        try {
          if (isRegistered()) {
            logger.info("Already registered with export service");
            return;
          }
          logger.debug("Attempting to register with export service");
          if (registerRulesEngine()) {
            logger.info("Export client registration complete");
            return;
          }
        } catch (Exception e) {
          logger.debug("Problem while connecting to export client service:  " + e.getMessage());
          logger.info("Export Client registration try# :  " + tries);
        }
        retrySleep(retryTime);
      } while (tries < retryAttempts);
      logger.error("Trouble connecting to export service.  Exiting.\n");
      System.exit(1);
    } else
      logger.info("Direct receiver of messages from core.  No export client registration");
  }

  private void retrySleep(int time) throws InterruptedException {
    try {
      Thread.sleep(time);
    } catch (InterruptedException e) {
      logger.error("Problem sleeping between registration retries");
      throw e;
    }
  }

  public boolean isRegistered() {
    try {
      return (exportRegistrationByName(clientName) != null);
    } catch (NotFoundException nfE) {
      return false;
    }
  }

  public boolean registerRulesEngine() {
    logger.debug("Registering rules engine service as export client");
    String id = register(getExportRegistration());
    if (id != null && id != "")
      return true;
    logger.error("Problems registering rules engine service as export client");
    return false;
  }

  @Override
  public ExportRegistration exportRegistrationByName(String name) {
    return getClient().exportRegistrationByName(name);
  }

  @Override
  public String register(ExportRegistration registration) {
    return getClient().register(registration);
  }

  @Override
  public boolean deleteByName(String name) {
    try {
      return getClient().deleteByName(name);
    } catch (javax.ws.rs.NotFoundException notFound) {
      return false;
    }
  }

  @Override
  public boolean delete(String id) {
    try {
      return getClient().delete(id);
    } catch (javax.ws.rs.NotFoundException notFound) {
      return false;
    }
  }

  private ExportRegistration getExportRegistration() {
    ExportRegistration registration = new ExportRegistration();
    registration.setName(clientName);
    registration.setFormat(ExportFormat.SERIALIZED);
    registration.setDestination(ExportDestination.ZMQ_TOPIC);
    registration.setEnable(true);
    Addressable addressable =
        new Addressable("EdgeXRulesEngineAddressable", Protocol.ZMQ, "", "", 0);
    registration.setAddressable(addressable);
    return registration;
  }

  private ExportClient getClient() {
    ResteasyClient client = new ResteasyClientBuilder().build();
    ResteasyWebTarget target = client.target(url);
    return target.proxy(ExportClient.class);
  }

}
