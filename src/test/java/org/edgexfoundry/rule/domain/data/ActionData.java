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

package org.edgexfoundry.rule.domain.data;

import org.edgexfoundry.rule.domain.Action;

public interface ActionData {

  static final String TEST_BODY = "{\\\"value\\\":\\\"300\\\"}";
  static final String TEST_CMD = "12345edf";
  static final String TEST_DEVICE = "56789abc";


  static Action newTestInstance() {
    Action action = new Action();
    action.setBody(TEST_BODY);
    action.setCommand(TEST_CMD);
    action.setDevice(TEST_DEVICE);
    return action;
  }
}
