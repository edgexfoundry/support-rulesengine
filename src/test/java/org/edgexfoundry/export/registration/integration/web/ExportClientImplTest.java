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
package org.edgexfoundry.export.registration.integration.web;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.edgexfoundry.Application;
import org.edgexfoundry.domain.export.ExportFormat;
import org.edgexfoundry.domain.export.ExportRegistration;
import org.edgexfoundry.domain.meta.Addressable;
import org.edgexfoundry.domain.meta.Protocol;
import org.edgexfoundry.export.registration.ExportClientImpl;
import org.edgexfoundry.test.category.RequiresExportClientRunning;
import org.edgexfoundry.test.category.RequiresMongoDB;
import org.edgexfoundry.test.category.RequiresSpring;
import org.edgexfoundry.test.category.RequiresWeb;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration("src/test/resources")
@Category({ RequiresSpring.class, RequiresWeb.class, RequiresMongoDB.class, RequiresExportClientRunning.class })
public class ExportClientImplTest {

	private static final String TEST_CLIENT_NAME = "testclient";
	private static final ExportFormat TEST_FORMAT = ExportFormat.SERIALIZED;
	private static final boolean TEST_ENABLE = true;
	private static final Addressable TEST_ADDR = new Addressable("testaddress", Protocol.ZMQ, "", "", 0);

	@Autowired
	private ExportClientImpl client;

	@Value("${export.client.registration.name}")
	private String clientName;

	@Test
	public void testIsRegistered() {
		assertTrue(client.isRegistered());
	}

	@Test
	public void testRegisterRulesEngine() {
		assertTrue(client.deleteByName(clientName));
		assertTrue(client.registerRulesEngine());
	}

	@Test
	public void testExportRegistrationByNameNoneFound() {
		assertNull(client.exportRegistrationByName("foo"));
	}

	@Test
	public void testExportRegistrationByName() {
		assertNotNull(client.exportRegistrationByName(clientName));
	}

	@Test
	// also tests delete by id
	public void testRegister() {
		String id = client.register(newInstance());
		assertNotNull(id);
		assertTrue(client.delete(id));
	}
	
	@Test
	public void testDeleteByIdNoneFound(){
		assertFalse(client.delete("foobar"));		
	}

	@Test
	public void testDeleteByName() {
		client.register(newInstance());
		assertTrue(client.deleteByName(TEST_CLIENT_NAME));
	}

	@Test
	public void testDeleteByNameNotFound() {
		assertFalse(client.deleteByName("foo"));
	}

	private ExportRegistration newInstance() {
		ExportRegistration registration = new ExportRegistration();
		registration.setName(TEST_CLIENT_NAME);
		registration.setFormat(TEST_FORMAT);
		registration.setEnable(TEST_ENABLE);
		registration.setAddressable(TEST_ADDR);
		return registration;
	}

}
