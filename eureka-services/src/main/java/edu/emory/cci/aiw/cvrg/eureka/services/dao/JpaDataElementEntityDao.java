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
package edu.emory.cci.aiw.cvrg.eureka.services.dao;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Provider;

import edu.emory.cci.aiw.cvrg.eureka.common.dao.GenericDao;
import edu.emory.cci.aiw.cvrg.eureka.common.entity.DataElementEntity;
import edu.emory.cci.aiw.cvrg.eureka.common.entity.DataElementEntity_;

/**
 * An implementation of the {@link DataElementEntityDao} interface, backed by
 * JPA entities and queries.
 *
 * @author hrathod
 */
public class JpaDataElementEntityDao extends GenericDao<DataElementEntity, Long>
		implements DataElementEntityDao {

	private static Logger LOGGER
			= LoggerFactory.getLogger(JpaDataElementEntityDao.class);

	/**
	 * Create an object with the given entity manager provider.
	 *
	 * @param inProvider An entity manager provider.
	 */
	@Inject
	public JpaDataElementEntityDao(Provider<EntityManager> inProvider) {
		super(DataElementEntity.class, inProvider);
	}

	@Override
	public DataElementEntity getByUserAndKey(Long inUserId, String inKey) {
		return getByUserAndKey(inUserId, inKey, true);
	}

	@Override
	public DataElementEntity getUserOrSystemByUserAndKey(Long inUserId, String inKey) {
		return getByUserAndKey(inUserId, inKey, false);
	}

	@Override
	public List<DataElementEntity> getByUserId(Long inUserId) {
		List<DataElementEntity> result;
		EntityManager entityManager = this.getEntityManager();
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<DataElementEntity> criteriaQuery = builder.createQuery(
				DataElementEntity.class);
		Root<DataElementEntity> root = criteriaQuery.from(DataElementEntity.class);
		Predicate userPredicate = builder.equal(
				root.get(
						DataElementEntity_.userId), inUserId);
		Predicate notInSystemPredicate = builder.equal(root.get(DataElementEntity_.inSystem), false);
		TypedQuery<DataElementEntity> typedQuery = entityManager.createQuery(
				criteriaQuery.where(
						builder.and(userPredicate, notInSystemPredicate)));

		result = typedQuery.getResultList();

		return result;
	}

	private DataElementEntity getByUserAndKey(Long inUserId, String inKey, boolean excludeSystemElements) {
		DataElementEntity result = null;
		EntityManager entityManager = this.getEntityManager();
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<DataElementEntity> criteriaQuery = builder.createQuery(
				DataElementEntity.class);
		Root<DataElementEntity> root = criteriaQuery.from(DataElementEntity.class);
		
		List<Predicate> predicates = new ArrayList<Predicate>(3);
		Predicate userPredicate = builder.equal(
				root.get(
						DataElementEntity_.userId), inUserId);
		predicates.add(userPredicate);
		Predicate keyPredicate = builder.equal(root.get(DataElementEntity_.key),
				inKey);
		predicates.add(keyPredicate);
		if (excludeSystemElements) {
			Predicate notInSystemPredicate = builder.equal(root.get(DataElementEntity_.inSystem),
					false);
			predicates.add(notInSystemPredicate);
		}
		
		TypedQuery<DataElementEntity> typedQuery = entityManager.createQuery(
					criteriaQuery.where(
							builder.and(predicates.toArray(
									new Predicate[predicates.size()]))));

		try {
			result = typedQuery.getSingleResult();
		} catch (NonUniqueResultException nure) {
			LOGGER.warn("Result not unique for user id = {} and key = {}",
					inUserId, inKey);
		} catch (NoResultException nre) {
			LOGGER.warn("Result not existent for user id = {} and key = {}",
					inUserId, inKey);
		}

		return result;
	}
}
