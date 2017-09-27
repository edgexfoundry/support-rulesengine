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
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.edgexfoundry.controller.impl.RuleEngineControllerImpl;
import org.edgexfoundry.engine.RuleCreator;
import org.edgexfoundry.engine.RuleEngine;
import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.rule.domain.Rule;
import org.edgexfoundry.rule.domain.data.RuleData;
import org.edgexfoundry.test.category.RequiresNone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import freemarker.template.TemplateException;

@Category({RequiresNone.class})
public class RuleEngineControllerTest {

  private static final String TEST_RULE1 = "Rule1";
  private static final String TEST_RULE2 = "Rule2";

  @InjectMocks
  private RuleEngineControllerImpl controller;

  @Mock
  private RuleEngine engine;

  @Mock
  private RuleCreator creator;


  @Before
  public void setup() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void testGetRuleNames() {
    List<String> ruleNames = new ArrayList<>();
    ruleNames.add(TEST_RULE1);
    ruleNames.add(TEST_RULE2);
    Mockito.when(engine.getRulenames()).thenReturn(ruleNames);
    assertEquals("Rule names not returned as expected", ruleNames, controller.ruleNames());
  }

  @Test(expected = ServiceException.class)
  public void testGetRuleNamesException() {
    Mockito.when(engine.getRulenames()).thenThrow(new RuntimeException());
    controller.ruleNames();
  }

  @Test
  public void testAddRule() {
    assertTrue("Rule was not added as expected", controller.addRule(RuleData.newTestInstance()));
  }

  @Test(expected = ServiceException.class)
  public void testAddRuleException() throws TemplateException, IOException {
    Rule rule = RuleData.newTestInstance();
    Mockito.when(creator.createDroolRule(rule)).thenThrow(new RuntimeException());
    controller.addRule(rule);
  }

  @Test
  public void testRemoveRule() {
    assertTrue("Rule was not removed as expected", controller.removeRule(TEST_RULE1));
  }

  @Test(expected = ServiceException.class)
  public void testRemoveRuleException() throws IOException {
    Mockito.when(engine.removeRule(TEST_RULE1)).thenThrow(new RuntimeException());
    controller.removeRule(TEST_RULE1);
  }
}
