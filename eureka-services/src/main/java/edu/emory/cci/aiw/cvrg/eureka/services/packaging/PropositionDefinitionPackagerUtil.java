/*
 * #%L
 * Eureka Services
 * %%
 * Copyright (C) 2012 Emory University
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package edu.emory.cci.aiw.cvrg.eureka.services.packaging;

import edu.emory.cci.aiw.cvrg.eureka.common.entity.TimeUnit;
import org.protempa.proposition.value.AbsoluteTimeUnit;

/**
 *
 */
public class PropositionDefinitionPackagerUtil {
	private PropositionDefinitionPackagerUtil() {
		// to prevent instantiation
	}

	static AbsoluteTimeUnit unit(TimeUnit unit) {
		return unit != null ? AbsoluteTimeUnit.nameToUnit(unit.getName()) :
				null;
	}
}
