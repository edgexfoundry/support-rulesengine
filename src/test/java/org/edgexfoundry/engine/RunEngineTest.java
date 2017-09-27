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

package org.edgexfoundry.engine;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.edgexfoundry.test.category.RequiresNone;
import org.junit.Before;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@Category({RequiresNone.class})
public class RunEngineTest {
  
  private static final String TEST_RESOURCE_FILE_PATH = "./src/test/resources";
  private static final String TEST_PACKAGE_NAME ="org.edgexfoundry.rules";
  private static final String TEST_RULE_FILE_EXT=".drl";

  @InjectMocks
  private RuleEngine engine;
  
  @Mock
  private CommandExecutor executor;

  @Before
  public void setup() throws IllegalAccessException {
    MockitoAnnotations.initMocks(this);
    FieldUtils.writeField(engine, "resourceFilePath", TEST_RESOURCE_FILE_PATH, true);
    FieldUtils.writeField(engine, "packageName", TEST_PACKAGE_NAME, true);
    FieldUtils.writeField(engine, "ruleFileExtension", TEST_RULE_FILE_EXT, true);
  }

}
