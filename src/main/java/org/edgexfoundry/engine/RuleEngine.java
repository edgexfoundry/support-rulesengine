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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.PostConstruct;

import org.edgexfoundry.domain.core.Event;
import org.edgexfoundry.exception.controller.ServiceException;
import org.kie.api.KieBase;
import org.kie.api.KieBaseConfiguration;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message.Level;
import org.kie.api.definition.KiePackage;
import org.kie.api.definition.rule.Rule;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.conf.ConstraintJittingThresholdOption;
import org.kie.internal.io.ResourceFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RuleEngine {

	// Drools Rule Engine requires SLF4J implementation of logger.
	// private static final org.slf4j.Logger logger =
	// org.slf4j.LoggerFactory.getLogger(RuleEngine.class);
	// replace above logger with EdgeXLogger below
	private final static org.edgexfoundry.support.logging.client.EdgeXLogger logger = org.edgexfoundry.support.logging.client.EdgeXLoggerFactory
			.getEdgeXLogger(RuleEngine.class);

	@Value("${rules.default.path}")
	private String resourceFilePath;

	@Value("${rules.packagename}")
	private String packageName;

	@Value("${rules.fileextension}")
	private String ruleFileExtension;

	@Autowired
	private CommandExecutor executor;

	private KieBase kbase;
	private KieFileSystem kfs;

	@PostConstruct
	public void init() {
		logger.debug("initializing Drools Kies");
		initKie();
	}

	public void execute(Object object) {
		try {
			KieSession ksession = kbase.newKieSession();
			//logger.error("--->" + ((Event) object).getId() + " fire@ " + System.currentTimeMillis());
			if (!getRulenames().isEmpty()) {
				if (executor != null) {
					ksession.setGlobal("executor", executor);
					ksession.setGlobal("logger", logger);
					ksession.insert(object);
					int rulesFired = ksession.fireAllRules();
					logger.debug("Number of rules fired on event: " + rulesFired);
					if (rulesFired > 0) {
						logger.info("Event triggered " + rulesFired + "rules: " + (Event) object);
					}
				} else
					logger.error("Command Executor not available - no command sent for event: " + object);
			} else {
				logger.debug("No rules in the system - skipping firing rules");
			}
			ksession.dispose();
		} catch (Exception e) {
			logger.error("Error during rules enging processing:  " + e.getMessage());
		}
	}

	public void addRule(String newRule, String rulename) throws IOException {
		String filename = getFileName(rulename);
		writeRule(newRule, filename);
		loadRule(filename);
		logger.info("new rule:  " + rulename + " added.");
		initKie();
	}

	public void removeRule(String rulename) throws IOException {
		String filename = getFileName(rulename);
		if (kbase.getRule(packageName, rulename) != null)
			kbase.removeRule(packageName, rulename);
		kfs.delete(filename);
		// goofey - but the file won't delete but will allow to be overwritten
		// with nothing this allows the rule to go away
		writeRule("", filename);
		logger.info("rule: " + rulename + " removed.");
	}

	public List<String> getRulenames() throws Exception {
		List<String> result = new ArrayList<String>();
		try {
			KiePackage kpkg = kbase.getKiePackage(packageName);
			if (kpkg != null) {
				Collection<Rule> rules = kpkg.getRules();
				if (rules != null) {
					for (Rule rule : rules) {
						result.add(rule.getName());
					}
				}
			}
			return result;
		} catch (

		Exception ex) {
			logger.error(ex.getMessage());
			throw new ServiceException(ex);
		}
	}

	public String getResourceFilePath() {
		return resourceFilePath;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getRuleFileExtension() {
		return ruleFileExtension;
	}

	private String getFileName(String rulename) {
		createDirectory();
		return resourceFilePath + "/" + rulename + ruleFileExtension;
	}

	private void createDirectory() {
		File ruleDirectory = new File(resourceFilePath);
		if (!ruleDirectory.exists())
			ruleDirectory.mkdir();
	}

	private void writeRule(String rule, String filename) throws IOException {
		FileWriter fileWriter;
		BufferedWriter writer;
		try {
			fileWriter = new FileWriter(new File(filename));
			writer = new BufferedWriter(fileWriter);
			writer.write(rule);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			logger.error("Problem writing file:  " + filename);
			e.printStackTrace();
			throw e;
		}
		if (writer != null)
			writer.close();
	}

	private void loadRule(String fileName) {
		kfs.write(ResourceFactory.newFileResource(fileName));
	}

	private void uploadDroolFiles() {
		logger.info("Starting Drools with drl files from:  " + resourceFilePath);
		File directory = new File(resourceFilePath);
		File[] fList = directory.listFiles();
		logger.info("Uploading Drool rules...");
		if (fList != null && fList.length > 0) {
			for (File file : fList) {
				kfs.write(ResourceFactory.newFileResource(resourceFilePath + "/" + file.getName()));
				logger.info("... " + file.getName());
			}
		} else {
			logger.info("...no rules found to upload");
		}
	}

	private void initKie() {
		KieServices ks = KieServices.Factory.get();
		kfs = ks.newKieFileSystem();
		uploadDroolFiles();
		KieBuilder kbuilder = ks.newKieBuilder(kfs);
		kbuilder.buildAll();
		if (kbuilder.getResults().hasMessages(Level.ERROR)) {
			throw new IllegalArgumentException(kbuilder.getResults().toString());
		}
		KieContainer kcontainer = ks.newKieContainer(kbuilder.getKieModule().getReleaseId());
		KieBaseConfiguration kbConfig = KieServices.Factory.get().newKieBaseConfiguration();
		kbConfig.setOption(ConstraintJittingThresholdOption.get(-1));
		kbase = kcontainer.newKieBase(kbConfig);
	}
}
