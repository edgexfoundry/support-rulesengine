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

package org.edgexfoundry.controller.impl;

import org.edgexfoundry.controller.PingController;
import org.edgexfoundry.exception.controller.ServiceException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ping")
public class PingControllerImpl implements PingController {

  private static final org.edgexfoundry.support.logging.client.EdgeXLogger logger =
      org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
          .getEdgeXLogger(PingControllerImpl.class);

  /**
   * Test service providing an indication that the service is available.
   * 
   * @throws ServcieException (HTTP 503) for unknown or unanticipated issues
   */
  @Override
  @RequestMapping(method = RequestMethod.GET)
  public String ping() {
    try {
      return "pong";
    } catch (Exception e) {
      logger.error("Error on ping:  " + e.getMessage());
      throw new ServiceException(e);
    }
  }
}
