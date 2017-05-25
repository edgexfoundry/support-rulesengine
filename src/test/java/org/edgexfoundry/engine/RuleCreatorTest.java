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
package org.edgexfoundry.engine;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.edgexfoundry.engine.RuleCreator;
import org.edgexfoundry.rule.domain.Action;
import org.edgexfoundry.rule.domain.Condition;
import org.edgexfoundry.rule.domain.Rule;
import org.edgexfoundry.rule.domain.ValueCheck;
import org.edgexfoundry.test.category.RequiresNone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import freemarker.template.TemplateException;

@Category(RequiresNone.class)
public class RuleCreatorTest {

	private RuleCreator creator;
	private static final String FIELD_LOC = "templateLocation";
	private static final String FIELD_NAME = "templateName";
	private static final String FIELD_ENC = "templateEncoding";
	private static final String TEMP_LOC = "./src/test/resources";
	private static final String TEMP_NAME = "rule-template.drl";
	private static final String TEMP_ENC = "UTF-8";

	private static final String TEST_LOG = "slow motor down";
	private static final String TEST_NAME = "testname";
	private static final String TEST_BODY = "{\\\"value\\\":\\\"300\\\"}";
	private static final String TEST_CMD = "12345edf";
	private static final String TEST_DEVICE = "56789abc";
	private static final String TEST_PARAM = "rpm";
	private static final String TEST_OP1 = "Integer.parseInt(value)";
	private static final String TEST_OP2 = "900";
	private static final String TEST_OPERATION = ">";

	@Before
	public void setup() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException, IOException {

		creator = new RuleCreator();
		Class<?> clazz = creator.getClass();
		Field loc = clazz.getDeclaredField(FIELD_LOC);
		loc.setAccessible(true);
		loc.set(creator, TEMP_LOC);
		Field nm = clazz.getDeclaredField(FIELD_NAME);
		nm.setAccessible(true);
		nm.set(creator, TEMP_NAME);
		Field enc = clazz.getDeclaredField(FIELD_ENC);
		enc.setAccessible(true);
		enc.set(creator, TEMP_ENC);
		creator.init();
	}

	@Test(expected = IOException.class)
	public void testInitException() throws NoSuchFieldException, SecurityException, IllegalArgumentException,
			IllegalAccessException, IOException {
		Class<?> clazz = creator.getClass();
		Field nm = clazz.getDeclaredField(FIELD_LOC);
		nm.setAccessible(true);
		nm.set(creator, "foobar");
		creator.init();
	}

	@Test
	public void testCreateDroolRule() throws TemplateException, IOException {
		Rule rule = createRule();
		String ruleContent = new String(Files.readAllBytes(Paths.get("./src/test/resources/rule.txt")));
		assertEquals("Drool file not created properly", ruleContent, creator.createDroolRule(rule));
	}

	@Test(expected = IOException.class)
	public void testCreateDroolRuleIOException() throws NoSuchFieldException, SecurityException,
			IllegalArgumentException, IllegalAccessException, TemplateException, IOException {
		Class<?> clazz = creator.getClass();
		Field nm = clazz.getDeclaredField(FIELD_NAME);
		nm.setAccessible(true);
		nm.set(creator, "foobar");
		Rule rule = createRule();
		creator.createDroolRule(rule);
	}

	@Test(expected = TemplateException.class)
	public void testCreateDroolRuleTemplateException() throws TemplateException, IOException, NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		Rule rule = createRule();
		rule.getAction().setDevice(null);
		creator.createDroolRule(rule);
	}

	@Test(expected = Exception.class)
	public void testCreateDroolRuleException() throws TemplateException, IOException, NoSuchFieldException,
			SecurityException, IllegalArgumentException, IllegalAccessException {
		Rule rule = createRule();
		Class<?> clazz = creator.getClass();
		Field cfg = clazz.getDeclaredField("cfg");
		cfg.setAccessible(true);
		cfg.set(creator, null);
		creator.createDroolRule(rule);
	}

	private Rule createRule() {
		Rule rule = new Rule();
		rule.setLog(TEST_LOG);
		rule.setName(TEST_NAME);
		Action action = new Action();
		action.setBody(TEST_BODY);
		action.setCommand(TEST_CMD);
		action.setDevice(TEST_DEVICE);
		rule.setAction(action);
		Condition cond = new Condition();
		cond.setDevice(TEST_DEVICE);
		ValueCheck check = new ValueCheck();
		check.setParameter(TEST_PARAM);
		check.setOperand1(TEST_OP1);
		check.setOperand2(TEST_OP2);
		check.setOperation(TEST_OPERATION);
		cond.addCheck(check);
		rule.setCondition(cond);
		return rule;
	}

}
