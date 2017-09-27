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

import java.util.ArrayList;
import java.util.List;

import org.edgexfoundry.rule.domain.Condition;
import org.edgexfoundry.rule.domain.ValueCheck;

public interface ConditionData {

  static final String TEST_DEVICE = "56789abc";

  static Condition newTestCondition() {
    Condition condition = new Condition();
    List<ValueCheck> checks = new ArrayList<>();
    checks.add(ValueCheckData.newTestInstance());
    condition.setChecks(checks);
    condition.setDevice(TEST_DEVICE);
    return condition;
  }
}
