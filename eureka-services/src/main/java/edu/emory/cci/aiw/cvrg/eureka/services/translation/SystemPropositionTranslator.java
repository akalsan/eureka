/*
 * #%L
 * Eureka Services
 * %%
 * Copyright (C) 2012 - 2015 Emory University
 * %%
 * This program is dual licensed under the Apache 2 and GPLv3 licenses.
 * 
 * Apache License, Version 2.0:
 * 
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
 * 
 * GNU General Public License version 3:
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */
package edu.emory.cci.aiw.cvrg.eureka.services.translation;

import java.util.ArrayList;
import java.util.List;

import org.protempa.PropertyDefinition;
import org.protempa.PropositionDefinition;

import com.google.inject.Inject;
import edu.emory.cci.aiw.cvrg.eureka.common.comm.SourceConfigParams;

import edu.emory.cci.aiw.cvrg.eureka.common.comm.SystemElement;
import edu.emory.cci.aiw.cvrg.eureka.common.entity.SystemProposition;
import edu.emory.cci.aiw.cvrg.eureka.common.exception.DataElementHandlingException;
import edu.emory.cci.aiw.cvrg.eureka.common.exception.HttpStatusException;
import edu.emory.cci.aiw.cvrg.eureka.services.dao.DataElementEntityDao;
import edu.emory.cci.aiw.cvrg.eureka.services.finder.PropositionFindException;
import edu.emory.cci.aiw.cvrg.eureka.services.finder.SystemPropositionFinder;
import edu.emory.cci.aiw.cvrg.eureka.services.resource.SourceConfigResource;
import edu.emory.cci.aiw.cvrg.eureka.services.util.PropositionUtil;
import javax.ws.rs.core.Response;

public class SystemPropositionTranslator implements
		PropositionTranslator<SystemElement, SystemProposition> {

	private final SystemPropositionFinder finder;
	private final SourceConfigResource sourceConfigResource;
	private final TranslatorSupport translatorSupport;

	@Inject
	public SystemPropositionTranslator(DataElementEntityDao inPropositionDao,
			SourceConfigResource inSourceConfigResource,
			SystemPropositionFinder inFinder) {
		finder = inFinder;
		this.sourceConfigResource = inSourceConfigResource;
		this.translatorSupport =
				new TranslatorSupport(inPropositionDao, inFinder, inSourceConfigResource);
	}

	@Override
	public SystemProposition translateFromElement(SystemElement element)
			throws DataElementHandlingException {
		SystemProposition proposition =
				this.translatorSupport.getUserEntityInstance(element,
				SystemProposition.class);
		proposition.setSystemType(element.getSystemType());
		return proposition;
	}

	@Override
	public SystemElement translateFromProposition(
			SystemProposition proposition) {
		SystemElement element = new SystemElement();
		try {
			PropositionTranslatorUtil.populateCommonDataElementFields(element,
					proposition);
			/*
			 * Hack to get an ontology source that assumes all Protempa configurations
			 * for a user point to the same knowledge source backends. This will go away.
			 */
			List<SourceConfigParams> scps = this.sourceConfigResource.getParamsList();
			if (scps.isEmpty()) {
				throw new HttpStatusException(Response.Status.INTERNAL_SERVER_ERROR, "No source configs");
			}
			PropositionDefinition propDef = finder.find(scps.get(0).getId(),
					proposition.getKey());
			List<SystemElement> children = new ArrayList<>();
			for (String child : propDef.getInverseIsA()) {
				PropositionDefinition childDef = finder.find(
						scps.get(0).getId(), child);
				SystemElement childElement =
						PropositionUtil.toSystemElement(scps.get(0).getId(), childDef, true,
						finder);

				children.add(childElement);
			}
			element.setChildren(children);

			List<String> properties = new ArrayList<>();
			for (PropertyDefinition property : propDef.getPropertyDefinitions()) {
				properties.add(property.getId());
			}
			element.setProperties(properties);
			element.setSystemType(proposition.getSystemType());
		} catch (PropositionFindException ex) {
			throw new AssertionError(
					"Error getting proposition definitions: " + ex.getMessage());
		}

		return element;
	}
}
