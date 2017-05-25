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
package org.edgexfoundry.messaging;

import static org.junit.Assert.assertEquals;

import org.edgexfoundry.messaging.ZeroMQEventSubscriber;
import org.edgexfoundry.test.category.RequiresNone;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category({ RequiresNone.class })
public class ZeroMQEventSubscriberTest {

	private ZeroMQEventSubscriber subscriber;

	@Before
	public void setup() {
		subscriber = new ZeroMQEventSubscriber();
	}

	@Test
	public void testSetters() {
		subscriber.setZeromqAddressPort("foobar");
		subscriber.setZeromqAddress("foobar");
		assertEquals("foobar", subscriber.getZeromqAddress());
		assertEquals("foobar", subscriber.getZeromqAddressPort());
	}

	// when it cant recieve it will keep trying and eventually overflow
	@Test(expected = StackOverflowError.class)
	public void testRecieve() {
		subscriber.setZeromqAddressPort("foobar");
		subscriber.setZeromqAddress("foobar");
		subscriber.receive();

	}
}
