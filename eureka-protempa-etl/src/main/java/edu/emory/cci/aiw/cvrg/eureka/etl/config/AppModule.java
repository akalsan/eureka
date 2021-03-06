package edu.emory.cci.aiw.cvrg.eureka.etl.config;

/*
 * #%L
 * Eureka Protempa ETL
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

import com.google.inject.AbstractModule;

import edu.emory.cci.aiw.cvrg.eureka.etl.dao.DestinationDao;
import edu.emory.cci.aiw.cvrg.eureka.etl.dao.DeidPerPatientParamDao;
import edu.emory.cci.aiw.cvrg.eureka.etl.dao.EtlGroupDao;
import edu.emory.cci.aiw.cvrg.eureka.common.dao.AuthorizedUserDao;
import edu.emory.cci.aiw.cvrg.eureka.etl.dao.JobDao;
import edu.emory.cci.aiw.cvrg.eureka.etl.dao.JobEventDao;
import edu.emory.cci.aiw.cvrg.eureka.etl.dao.JpaDestinationDao;
import edu.emory.cci.aiw.cvrg.eureka.etl.dao.JpaDestinationOffsetDao;
import edu.emory.cci.aiw.cvrg.eureka.etl.dao.JpaEtlGroupDao;
import edu.emory.cci.aiw.cvrg.eureka.etl.dao.JpaEtlUserDao;
import edu.emory.cci.aiw.cvrg.eureka.etl.dao.JpaJobDao;
import edu.emory.cci.aiw.cvrg.eureka.etl.dao.JpaJobEventDao;
import edu.emory.cci.aiw.cvrg.eureka.etl.dao.JpaSourceConfigDao;
import edu.emory.cci.aiw.cvrg.eureka.etl.dao.SourceConfigDao;
import edu.emory.cci.aiw.cvrg.eureka.etl.job.Task;
import edu.emory.cci.aiw.cvrg.eureka.etl.job.TaskProvider;

/**
 * @author hrathod
 */
public class AppModule extends AbstractModule {
	@Override
	protected void configure() {
		bind(JobDao.class).to(JpaJobDao.class);
		bind(JobEventDao.class).to(JpaJobEventDao.class);
		bind(AuthorizedUserDao.class).to(JpaEtlUserDao.class);
		bind(EtlGroupDao.class).to(JpaEtlGroupDao.class);
		bind(DestinationDao.class).to(JpaDestinationDao.class);
		bind(DeidPerPatientParamDao.class).to(JpaDestinationOffsetDao.class);
		bind(SourceConfigDao.class).to(JpaSourceConfigDao.class);
		bind(Task.class).toProvider(TaskProvider.class);
	}
}
