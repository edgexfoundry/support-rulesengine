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

package org.edgexfoundry.controller;

import static org.junit.Assert.assertEquals;

import org.edgexfoundry.controller.impl.PingControllerImpl;
import org.edgexfoundry.test.category.RequiresNone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(RequiresNone.class)
public class PingControllerTest {

  private static final String PING_RESP = "pong";

  private PingControllerImpl controller;

  @Before
  public void setup() {
    controller = new PingControllerImpl();
  }

  @Test
  public void testPing() {
    assertEquals("Ping controller ping test responded incorrectly", PING_RESP, controller.ping());
  }

}
