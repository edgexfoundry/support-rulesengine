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

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.edgexfoundry.rule.domain.Rule;
import org.edgexfoundry.rule.domain.data.RuleData;
import org.edgexfoundry.test.category.RequiresNone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import freemarker.template.TemplateException;

@Category(RequiresNone.class)
public class RuleCreatorTest {

  private static final String FIELD_LOC = "templateLocation";
  private static final String FIELD_NAME = "templateName";
  private static final String FIELD_ENC = "templateEncoding";
  private static final String TEMP_LOC = "./src/test/resources";
  private static final String TEMP_NAME = "rule-template.drl";
  private static final String TEMP_ENC = "UTF-8";

  @InjectMocks
  private RuleCreator creator;

  private Rule rule;

  @Before
  public void setup() throws IllegalAccessException, IOException {
    MockitoAnnotations.initMocks(this);
    FieldUtils.writeField(creator, FIELD_LOC, TEMP_LOC, true);
    FieldUtils.writeField(creator, FIELD_NAME, TEMP_NAME, true);
    FieldUtils.writeField(creator, FIELD_ENC, TEMP_ENC, true);
    creator.init();
    rule = RuleData.newTestInstance();
  }

  @Test(expected = IOException.class)
  public void testInitException() throws NoSuchFieldException, SecurityException,
      IllegalArgumentException, IllegalAccessException, IOException {
    FieldUtils.writeField(creator, FIELD_LOC, "foobar", true);
    creator.init();
  }

  @Test
  public void testCreateDroolRule() throws TemplateException, IOException {
    String ruleContent = new String(Files.readAllBytes(Paths.get("./src/test/resources/rule.txt")));
    assertEquals("Drool file not created properly", ruleContent, creator.createDroolRule(rule));
  }

  @Test(expected = IOException.class)
  public void testCreateDroolRuleIOException()
      throws IllegalAccessException, TemplateException, IOException {
    FieldUtils.writeField(creator, FIELD_NAME, "foobar", true);
    creator.createDroolRule(rule);
  }

  @Test(expected = TemplateException.class)
  public void testCreateDroolRuleTemplateException() throws TemplateException, IOException,
      NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
    rule.getAction().setDevice(null);
    creator.createDroolRule(rule);
  }

  @Test(expected = Exception.class)
  public void testCreateDroolRuleException()
      throws IllegalAccessException, TemplateException, IOException {
    FieldUtils.writeField(creator, "cfg", null, true);
    creator.createDroolRule(rule);
  }

}
