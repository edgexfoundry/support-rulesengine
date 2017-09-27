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

import org.edgexfoundry.rule.domain.ValueCheck;

public interface ValueCheckData {

  static final String TEST_PARAM = "rpm";
  static final String TEST_OP1 = "Integer.parseInt(value)";
  static final String TEST_OP2 = "900";
  static final String TEST_OPERATION = ">";

  static ValueCheck newTestInstance() {
    ValueCheck check = new ValueCheck();
    check.setOperand1(TEST_OP1);
    check.setOperand2(TEST_OP2);
    check.setOperation(TEST_OPERATION);
    check.setParameter(TEST_PARAM);
    return check;
  }
}
