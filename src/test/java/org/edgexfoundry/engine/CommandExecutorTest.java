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

import java.lang.reflect.Field;

import org.edgexfoundry.engine.CommandExecutor;
import org.edgexfoundry.test.category.RequiresNone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.fail;

@Category(RequiresNone.class)
public class CommandExecutorTest {

	private CommandExecutor executor;

	@Before
	public void setup() {
		executor = new CommandExecutor();
		Class<?> executorClass = executor.getClass();
		Field temp;
		try {
			temp = executorClass.getDeclaredField("client");
			temp.setAccessible(true);
			temp.set(executor, new MockCmdClient());
		} catch (NoSuchFieldException | SecurityException e) {
			e.printStackTrace();
			fail("Could not setup executor for tests");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			fail("Could not setup executor for tests");
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			fail("Could not setup executor for tests");
		}

	}

	@Test
	public void testFireCommand() {
		executor.fireCommand("testId", "testCmdId", "test body");
	}

	@Test
	public void testFireCommandException() {
		executor = new CommandExecutor();
		executor.fireCommand("testId", "testCmdId", "test body");
	}

}
