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

package org.edgexfoundry.engine.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.edgexfoundry.Application;
import org.edgexfoundry.domain.core.Event;
import org.edgexfoundry.engine.RuleEngine;
import org.edgexfoundry.test.category.RequiresExportClientRunning;
import org.edgexfoundry.test.category.RequiresMongoDB;
import org.edgexfoundry.test.category.RequiresSpring;
import org.edgexfoundry.test.category.RequiresWeb;
import org.edgexfoundry.test.data.EventData;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * note: Make sure the /edgex/rules folder contains no drl files (or just the testname.drl file)
 * before starting this test
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration("src/test/resources")
@Category({RequiresSpring.class, RequiresWeb.class, RequiresMongoDB.class,
    RequiresExportClientRunning.class})
public class RuleEngineTest {

  private static final String TEST_RULE_NAME = "testname";
  private String ruleContent;

  @Autowired
  private RuleEngine engine;

  @Before
  public void setup() throws IOException {
    ruleContent = new String(Files.readAllBytes(Paths.get("./src/test/resources/rule.txt")));
    engine.addRule(ruleContent, TEST_RULE_NAME);
  }

  @After
  public void cleanup() throws Exception {
    List<String> names = engine.getRulenames();
    for (String name : names) {
      engine.removeRule(name);
    }
  }

  @Test
  public void testAddRule() throws Exception {
    engine.addRule(ruleContent, TEST_RULE_NAME);
    String droolfile = engine.getResourceFilePath() + "/" + TEST_RULE_NAME + ".drl";
    String content = new String(Files.readAllBytes(Paths.get(droolfile)));
    assertTrue("Rule did not get added properly", content.length() > 1);
  }

  @Test
  public void testRemoveRule() throws IOException {
    engine.removeRule(TEST_RULE_NAME);
    String droolfile = engine.getResourceFilePath() + "/" + TEST_RULE_NAME + ".drl";
    String content = new String(Files.readAllBytes(Paths.get(droolfile)));
    assertTrue("Rule did not get removed properly", content.length() == 0);
  }

  @Test
  public void testGetRuleNames() throws Exception {
    assertTrue("No rules should exist", !engine.getRulenames().isEmpty());
    assertEquals("Existing rule name not found", TEST_RULE_NAME, engine.getRulenames().get(0));
  }

  @Test
  public void testExcecute() {
    Event event = EventData.newTestInstance();
    engine.execute(event);
  }

}
