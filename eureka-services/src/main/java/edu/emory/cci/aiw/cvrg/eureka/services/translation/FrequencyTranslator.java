/*
 * #%L
 * Eureka Services
 * %%
 * Copyright (C) 2012 - 2013 Emory University
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

import javax.ws.rs.core.Response;


import com.google.inject.Inject;

import edu.emory.cci.aiw.cvrg.eureka.common.comm.Frequency;
import edu.emory.cci.aiw.cvrg.eureka.common.entity.FrequencyEntity;
import edu.emory.cci.aiw.cvrg.eureka.common.entity.TimeUnit;
import edu.emory.cci.aiw.cvrg.eureka.common.exception.DataElementHandlingException;
import edu.emory.cci.aiw.cvrg.eureka.services.dao.FrequencyTypeDao;
import edu.emory.cci.aiw.cvrg.eureka.services.dao.DataElementEntityDao;
import edu.emory.cci.aiw.cvrg.eureka.services.dao.TimeUnitDao;
import edu.emory.cci.aiw.cvrg.eureka.services.dao.ValueComparatorDao;
import edu.emory.cci.aiw.cvrg.eureka.services.finder.SystemPropositionFinder;
import edu.emory.cci.aiw.cvrg.eureka.services.resource.SourceConfigResource;

public final class FrequencyTranslator implements
		PropositionTranslator<Frequency, FrequencyEntity> {

	private final TimeUnitDao timeUnitDao;
	private final ValueComparatorDao valueComparatorDao;
	private final FrequencyTypeDao freqTypeDao;
	private final TranslatorSupport translatorSupport;

	@Inject
	public FrequencyTranslator(DataElementEntityDao inPropositionDao,
			TimeUnitDao inTimeUnitDao, SystemPropositionFinder inFinder,
			ValueComparatorDao inValueComparatorDao,
			FrequencyTypeDao inFrequencyTypeDao,
			SourceConfigResource inSourceConfigResource) {
		this.timeUnitDao = inTimeUnitDao;
		this.translatorSupport = new TranslatorSupport(inPropositionDao,
				inFinder, inSourceConfigResource);
		this.valueComparatorDao = inValueComparatorDao;
		this.freqTypeDao = inFrequencyTypeDao;
	}

	@Override
	public FrequencyEntity translateFromElement(Frequency element)
			throws DataElementHandlingException {
		FrequencyEntity result =
				this.translatorSupport.getUserEntityInstance(element,
				FrequencyEntity.class);

		result.setCount(element.getAtLeast());
		result.setConsecutive(element.getIsConsecutive());
		result.setExtendedProposition(PropositionTranslatorUtil
				.createOrUpdateExtendedProposition(
				result.getExtendedProposition(),
				element.getDataElement(), element.getUserId(),
				timeUnitDao, translatorSupport,
				valueComparatorDao));
		result.setInSystem(false);
		result.setFrequencyType(
				freqTypeDao.retrieve(element.getFrequencyType()));

		if (element.getIsWithin()) {
			result.setWithinAtLeast(element.getWithinAtLeast());
			Long withinAtLeastUnitsL = element.getWithinAtLeastUnits();
			if (withinAtLeastUnitsL == null) {
				throw new DataElementHandlingException(
						Response.Status.PRECONDITION_FAILED,
						"Within at least units must be specified");
			}
			result.setWithinAtLeastUnits(
					this.timeUnitDao.retrieve(withinAtLeastUnitsL));
			result.setWithinAtMost(element.getWithinAtMost());
			Long withinAtMostUnitsL = element.getWithinAtMostUnits();
			if (withinAtMostUnitsL == null) {
				throw new DataElementHandlingException(
						Response.Status.PRECONDITION_FAILED,
						"Within at most units must be specified");
			}
			result.setWithinAtMostUnits(
					this.timeUnitDao.retrieve(withinAtMostUnitsL));
		} else {
			result.setWithinAtLeast(null);
			Long withinAtLeastUnitsL = element.getWithinAtLeastUnits();
			TimeUnit withinAtLeastUnits;
			if (withinAtLeastUnitsL != null) {
				withinAtLeastUnits =
						this.timeUnitDao.retrieve(withinAtLeastUnitsL);
			} else {
				withinAtLeastUnits = this.timeUnitDao.getDefault();
			}
			result.setWithinAtLeastUnits(withinAtLeastUnits);
			result.setWithinAtMost(null);
			Long withinAtMostUnitsL = element.getWithinAtMostUnits();
			TimeUnit withinAtMostUnits;
			if (withinAtMostUnitsL != null) {
				withinAtMostUnits =
						this.timeUnitDao.retrieve(withinAtMostUnitsL);
			} else {
				withinAtMostUnits = this.timeUnitDao.getDefault();

			}
			result.setWithinAtMostUnits(withinAtMostUnits);
		}

		return result;
	}

	@Override
	public Frequency translateFromProposition(FrequencyEntity entity) {
		Frequency result = new Frequency();

		PropositionTranslatorUtil.populateCommonDataElementFields(result,
				entity);
		result.setAtLeast(entity.getCount());
		result.setIsConsecutive(entity.isConsecutive());
		result.setDataElement(PropositionTranslatorUtil
				.createDataElementField(entity.getExtendedProposition()));
		
		result.setWithinAtLeast(entity.getWithinAtLeast());
		result.setWithinAtLeastUnits(entity.getWithinAtLeastUnits().getId());
		result.setWithinAtMost(entity.getWithinAtMost());
		result.setWithinAtMostUnits(entity.getWithinAtMostUnits().getId());
		result.setFrequencyType(entity.getFrequencyType().getId());
		if (result.getWithinAtLeast() != null || result.getWithinAtMost() != null) {
			result.setIsWithin(true);
		}

		return result;
	}
}
