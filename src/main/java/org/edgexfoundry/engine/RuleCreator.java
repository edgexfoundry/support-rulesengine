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

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.edgexfoundry.rule.domain.Rule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

@Component
public class RuleCreator {

	// private static final Logger logger = Logger.getLogger(RuleCreator.class);
	// replace above logger with EdgeXLogger below
	private final static org.edgexfoundry.support.logging.client.EdgeXLogger logger = org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
			.getEdgeXLogger(RuleCreator.class);

	@Value("${rules.template.path}")
	private String templateLocation;

	@Value("${rules.template.name}")
	private String templateName;

	@Value("${rules.template.encoding}")
	private String templateEncoding;

	private Configuration cfg;

	@PostConstruct
	public void init() throws IOException {
		try {
			cfg = new Configuration(Configuration.getVersion());
			cfg.setDirectoryForTemplateLoading(new File(templateLocation));
			cfg.setDefaultEncoding(templateEncoding);
			cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		} catch (IOException e) {
			logger.error("Problem getting rule template location." + e.getMessage());
			throw e;
		}
	}

	private Map<String, Object> createMap(Rule rule) {
		Map<String, Object> map = new HashMap<>();
		map.put("rulename", rule.getName());
		map.put("conddeviceid", rule.getCondition().getDevice());
		map.put("valuechecks", rule.getCondition().getChecks());
		map.put("actiondeviceid", rule.getAction().getDevice());
		map.put("actioncommandid", rule.getAction().getCommand());
		map.put("commandbody", rule.getAction().getBody());
		map.put("log", rule.getLog());
		return map;
	}

	public String createDroolRule(Rule rule) throws TemplateException, IOException {
		try {
			Template temp = cfg.getTemplate(templateName);
			Writer out = new StringWriter();
			temp.process(createMap(rule), out);
			return out.toString();
		} catch (IOException iE) {
			logger.error("Problem getting rule template file." + iE.getMessage());
			throw iE;
		} catch (TemplateException tE) {
			logger.error("Problem writing Drool rule." + tE.getMessage());
			throw tE;
		} catch (Exception e) {
			logger.error("Problem creating rule: " + e.getMessage());
			throw e;
		}
	}

}
