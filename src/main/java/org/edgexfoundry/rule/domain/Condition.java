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
package org.edgexfoundry.rule.domain;

import java.util.ArrayList;
import java.util.List;

public class Condition {

	private String device;
	private List<ValueCheck> checks;

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public List<ValueCheck> getChecks() {
		return checks;
	}

	public void setChecks(List<ValueCheck> checks) {
		this.checks = checks;
	}

	public void addCheck(ValueCheck check) {
		if (checks == null)
			checks = new ArrayList<ValueCheck>();
		checks.add(check);
	}

}
