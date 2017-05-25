/*******************************************************************************
 * Copyright 2017 Dell Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @microservice:  support-rulesengine
 * @author: Jim White, Dell
 * @version: 1.0.0
 *******************************************************************************/
package org.edgexfoundry.controller.integration.web;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.edgexfoundry.Application;
import org.edgexfoundry.controller.RuleEngineController;
import org.edgexfoundry.engine.RuleEngine;
import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.rule.domain.Action;
import org.edgexfoundry.rule.domain.Condition;
import org.edgexfoundry.test.category.RequiresExportClientRunning;
import org.edgexfoundry.test.category.RequiresMongoDB;
import org.edgexfoundry.test.category.RequiresSpring;
import org.edgexfoundry.test.category.RequiresWeb;
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
 *         note: Make sure the /edgex/rules folder contains no drl files (or just
 *         the testname.drl file) before starting this test
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration("src/test/resources")
@Category({ RequiresSpring.class, RequiresWeb.class, RequiresMongoDB.class, RequiresExportClientRunning.class })
public class RuleEngineControllerTest {

	private static final String TEST_RULE_NAME = "testname";

	private static final String TEST_LOG = "testlog";
	private static final String TEST_NAME = "testname";
	private static final String TEST_BODY = "testbody";
	private static final String TEST_CMD = "testcommand";
	private static final String TEST_DEVICE = "testdevice";

	@Autowired
	RuleEngineController controller;

	@Autowired
	RuleEngine engine;

	@Before
	public void setup() throws IOException {
		String ruleContent = new String(Files.readAllBytes(Paths.get("./src/test/resources/rule.txt")));
		engine.addRule(ruleContent, TEST_RULE_NAME);
	}

	@After
	public void cleanup() throws Exception {
		Class<?> clazz = controller.getClass();
		Field eng = clazz.getDeclaredField("engine");
		eng.setAccessible(true);
		eng.set(controller, engine);
		
		List<String> names = engine.getRulenames();
		for (String name : names) {
			engine.removeRule(name);
		}
	}

	@Test
	public void testGetRuleNames() throws Exception {
		List<String> names = controller.ruleNames();
		assertEquals("Rule names not returning list with rule name", 1, names.size());
		assertEquals("Rule name not in list of rule names", TEST_RULE_NAME, names.get(0));
	}

	@Test(expected = ServiceException.class)
	public void testGetRuleNamesException() throws Exception {
		Class<?> clazz = controller.getClass();
		Field eng = clazz.getDeclaredField("engine");
		eng.setAccessible(true);
		eng.set(controller, null);
		controller.ruleNames();
	}

	@Test
	public void testAddRule() {
		assertTrue("New rule did not get added correctly", controller.addRule(createRule()));
	}

	@Test(expected = ServiceException.class)
	public void testAddRuleException() throws Exception {
		Class<?> clazz = controller.getClass();
		Field eng = clazz.getDeclaredField("engine");
		eng.setAccessible(true);
		eng.set(controller, null);
		controller.addRule(createRule());
	}

	@Test
	public void testRemoveRule() {
		assertTrue("Rule did not get removed correctly", controller.removeRule(TEST_RULE_NAME));
	}

	@Test(expected = ServiceException.class)
	public void testRemoveRuleException() throws Exception {
		Class<?> clazz = controller.getClass();
		Field eng = clazz.getDeclaredField("engine");
		eng.setAccessible(true);
		eng.set(controller, null);
		controller.removeRule(TEST_RULE_NAME);
	}

	private org.edgexfoundry.rule.domain.Rule createRule() {
		org.edgexfoundry.rule.domain.Rule rule = new org.edgexfoundry.rule.domain.Rule();
		rule.setLog(TEST_LOG);
		rule.setName(TEST_NAME);
		Action action = new Action();
		action.setBody(TEST_BODY);
		action.setCommand(TEST_CMD);
		action.setDevice(TEST_DEVICE);
		rule.setAction(action);
		Condition cond = new Condition();
		cond.setDevice(TEST_DEVICE);
		rule.setCondition(cond);
		return rule;
	}

}
