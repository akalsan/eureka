/*
 * #%L
 * Eureka Services
 * %%
 * Copyright (C) 2012 - 2013 Emory University
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
package edu.emory.cci.aiw.cvrg.eureka.services.translation;

import edu.emory.cci.aiw.cvrg.eureka.common.comm.DataElement;
import edu.emory.cci.aiw.cvrg.eureka.common.entity.Proposition;
import edu.emory.cci.aiw.cvrg.eureka.common.exception.DataElementHandlingException;
import edu.emory.cci.aiw.cvrg.eureka.services.dao.PropositionDao;
import edu.emory.cci.aiw.cvrg.eureka.services.finder.PropositionFindException;
import edu.emory.cci.aiw.cvrg.eureka.services.finder.SystemPropositionFinder;
import edu.emory.cci.aiw.cvrg.eureka.services.util.PropositionUtil;
import java.util.Date;
import javax.ws.rs.core.Response;
import org.protempa.PropositionDefinition;

/**
 *
 * @author Andrew Post
 */
final class TranslatorSupport {
	private final PropositionDao propositionDao;
	private final SystemPropositionFinder finder;

	public TranslatorSupport(PropositionDao propositionDao, 
			SystemPropositionFinder finder) {
		this.propositionDao = propositionDao;
		this.finder = finder;
	}
	
	/**
	 * Retrieves the user or system entity if it exists, otherwise creates an
	 * entity from a system proposition with with the given user id and key.
	 * 
	 * @param userId
	 * @param key
	 * @return
	 * @throws DataElementHandlingException 
	 */
	Proposition getSystemEntityInstance(Long userId, String key) 
			throws DataElementHandlingException {
		Proposition abstractedFrom =
				propositionDao.getByUserAndKey(userId,key);
		if (abstractedFrom == null) {
			try {
				PropositionDefinition propDef = this.finder.find(userId, key);
				abstractedFrom = PropositionUtil.toSystemProposition(propDef, 
						userId);
			} catch (PropositionFindException ex) {
				throw new DataElementHandlingException(
						Response.Status.PRECONDITION_FAILED,
						"No system entity with name " + key, ex);
			}
		}
		return abstractedFrom;
	}
	
	/**
	 * Creates a new user entity if one with the provided data element's user 
	 * id and key does not already exist.
	 * 
	 * @param <P>
	 * @param element
	 * @param cls
	 * @return 
	 */
	<P extends Proposition> P getUserEntityInstance(DataElement element, 
			Class<P> cls) {
		String key;
		if (element.getKey() != null) {
			key = element.getKey();
		} else {
			key = element.getAbbrevDisplayName();
		}
		
		Date now = new Date();

		P result;
		Proposition oldEntity = propositionDao.getByUserAndKey(
				element.getUserId(), key);
		if (cls.isInstance(oldEntity)) {
			result = cls.cast(oldEntity);
		} else {
			try {
				result = cls.newInstance();
			} catch (InstantiationException ex) {
				throw new AssertionError("Could not instantiate entity " + key 
						+ ": " + ex.getMessage());
			} catch (IllegalAccessException ex) {
				throw new AssertionError("Could not instantiate entity " + key 
						+ ": " + ex.getMessage());
			}
			result.setCreated(now);
		}

		result.setInSystem(false);
		result.setLastModified(now);
		
		PropositionTranslatorUtil.populateCommonPropositionFields(result,
				element);
		
		return result;
	}
}
