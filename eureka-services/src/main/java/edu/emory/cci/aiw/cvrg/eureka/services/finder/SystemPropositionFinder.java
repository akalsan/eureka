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
package edu.emory.cci.aiw.cvrg.eureka.services.finder;

import java.util.List;

import org.protempa.PropositionDefinition;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import edu.emory.cci.aiw.cvrg.eureka.common.comm.SystemElement;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

@Singleton
public class SystemPropositionFinder extends AbstractPropositionFinder<String> {
	private static final String CACHE_NAME = "systemPropositions";
	private final SystemPropositionRetriever retriever;
	private final CacheManager cacheManager;
	private final Cache cache;

	@Inject
	public SystemPropositionFinder(SystemPropositionRetriever inRetriever) {
		super(inRetriever);
		this.retriever = inRetriever;
		this.cacheManager = CacheManager.create();
		this.cache = this.cacheManager.getCache(CACHE_NAME);
	}
	
	/**
	 * Finds all of the system elements given by the keys for the given user
	 * 
	 * @param sourceConfigId the ID of the source config to use for the look-up
	 * @param inKeys the keys of the system elements to look up
	 * @param withChildren whether to find the given system elements' children as well
	 * @return a {@link List} of {@link SystemElement}s
	 * @throws PropositionFindException
	 */
	public List<PropositionDefinition> findAll(
	        String sourceConfigId, List<String> inKeys, Boolean withChildren) throws PropositionFindException {
		return this.retriever.retrieveAll(sourceConfigId, inKeys, withChildren);
	}

	@Override
	protected Cache getCache() {
		return cache;
	}

	@Override
	protected CacheManager getCacheManager() {
		return cacheManager;
	}
}
