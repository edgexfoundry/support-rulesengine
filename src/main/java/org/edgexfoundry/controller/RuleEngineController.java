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
package org.edgexfoundry.controller;

import java.util.List;

import org.edgexfoundry.engine.RuleCreator;
import org.edgexfoundry.engine.RuleEngine;
import org.edgexfoundry.exception.controller.ServiceException;
import org.edgexfoundry.rule.domain.Rule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rule")
public class RuleEngineController {

	// private final static Logger logger =
	// Logger.getLogger(RuleEngineController.class);
	// replace above logger with EdgeXLogger below
	private final static org.edgexfoundry.support.logging.client.EdgeXLogger logger = org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
			.getEdgeXLogger(RuleEngineController.class);

	@Autowired
	private RuleEngine engine;

	@Autowired
	private RuleCreator creator;

	@RequestMapping(method = RequestMethod.GET)
	public List<String> ruleNames() throws Exception {
		try {
			return engine.getRulenames();
		} catch (Exception e) {
			logger.error("Problem getting rule names");
			throw new ServiceException(e);
		}
	}

	@RequestMapping(method = RequestMethod.POST)
	public boolean addRule(@RequestBody Rule rule) {
		try {
			engine.addRule(creator.createDroolRule(rule), rule.getName());
			logger.info("Rule named:  " + rule.getName() + " added.");
			return true;
		} catch (Exception e) {
			logger.error("Problem adding a new rule called: " + rule.getName());
			throw new ServiceException(e);
		}
	}

	@RequestMapping(value = "/name/{rulename}", method = RequestMethod.DELETE)
	public boolean removeRule(@PathVariable String rulename) {
		try {
			engine.removeRule(rulename);
			logger.info("Rule named:  " + rulename + " removed");
			return true;
		} catch (Exception e) {
			logger.error("Problem removing the rule called: " + rulename);
			throw new ServiceException(e);
		}
	}
}
