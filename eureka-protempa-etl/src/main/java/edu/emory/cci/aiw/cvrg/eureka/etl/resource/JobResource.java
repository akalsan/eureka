/*
 * #%L
 * Eureka Protempa ETL
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
package edu.emory.cci.aiw.cvrg.eureka.etl.resource;

import java.net.URI;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.protempa.PropositionDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import edu.emory.cci.aiw.cvrg.eureka.common.comm.Job;

import edu.emory.cci.aiw.cvrg.eureka.common.comm.JobFilter;
import edu.emory.cci.aiw.cvrg.eureka.common.comm.JobRequest;
import edu.emory.cci.aiw.cvrg.eureka.common.comm.JobSpec;
import edu.emory.cci.aiw.cvrg.eureka.common.entity.EtlUser;
import edu.emory.cci.aiw.cvrg.eureka.common.entity.JobEntity;
import edu.emory.cci.aiw.cvrg.eureka.common.exception.HttpStatusException;
import edu.emory.cci.aiw.cvrg.eureka.etl.dao.EtlUserDao;
import edu.emory.cci.aiw.cvrg.eureka.etl.dao.JobDao;
import edu.emory.cci.aiw.cvrg.eureka.etl.job.TaskManager;
import edu.emory.cci.aiw.cvrg.eureka.etl.validator.PropositionValidator;
import edu.emory.cci.aiw.cvrg.eureka.etl.validator.PropositionValidatorException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Date;
import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response.Status;
import org.apache.commons.lang3.StringUtils;
import org.protempa.backend.dsb.filter.DateTimeFilter;
import org.protempa.proposition.value.AbsoluteTimeGranularity;

@Path("/protected/jobs")
@RolesAllowed({"researcher"})
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class JobResource {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(JobResource.class);
	private final JobDao jobDao;
	private final EtlUserDao etlUserDao;
	private final PropositionValidator propositionValidator;
	private final TaskManager taskManager;

	@Inject
	public JobResource(JobDao inJobDao, TaskManager inTaskManager,
			PropositionValidator inValidator, EtlUserDao inEtlUserDao) {
		this.jobDao = inJobDao;
		this.taskManager = inTaskManager;
		this.propositionValidator = inValidator;
		this.etlUserDao = inEtlUserDao;
	}

	@GET
	public List<Job> getAll(@Context HttpServletRequest request,
			@QueryParam("order") String order) {
		JobFilter jobFilter = new JobFilter(null,
				request.getUserPrincipal().getName(), null, null, null);
		List<Job> jobs = new ArrayList<>();
		List<JobEntity> jobEntities;
		if (order == null) {
			jobEntities = this.jobDao.getWithFilter(jobFilter);
		} else if (order.equals("desc")) {
			jobEntities = this.jobDao.getWithFilterDesc(jobFilter);
		} else {
			throw new HttpStatusException(Response.Status.PRECONDITION_FAILED, "Invalid value for the order parameter: " + order);
		}
		for (JobEntity jobEntity : jobEntities) {
			jobs.add(jobEntity.toJob());
		}
		return jobs;
	}

	@GET
	@Path("/{jobId}")
	public Job getJob(@Context HttpServletRequest request,
			@PathParam("jobId") Long inJobId) {
		JobFilter jobFilter = new JobFilter(inJobId,
				request.getUserPrincipal().getName(), null, null, null);
		List<JobEntity> jobEntities = this.jobDao.getWithFilter(jobFilter);
		if (jobEntities.isEmpty()) {
			throw new HttpStatusException(Status.NOT_FOUND);
		} else if (jobEntities.size() > 1) {
			throw new HttpStatusException(Status.INTERNAL_SERVER_ERROR, jobEntities.size() + " jobs returned for job id " + inJobId);
		} else {
			JobEntity jobEntity = jobEntities.get(0);
			return jobEntity.toJob();
		}
	}

	@POST
	public Response submit(@Context HttpServletRequest request,
			JobRequest inJobRequest) {
		throwHttpStatusExceptionIfInvalid(inJobRequest);
		Long jobId = doCreateJob(inJobRequest, request);
		return Response.created(URI.create("/" + jobId)).build();
	}
	
	private Long doCreateJob(JobRequest inJobRequest, HttpServletRequest request) {
		JobSpec job = inJobRequest.getJobSpec();
		JobEntity jobEntity = 
				newJobEntity(job, toEtlUser(request.getUserPrincipal()));
		this.taskManager.queueTask(jobEntity.getId(), 
				inJobRequest.getUserPropositions(),
				inJobRequest.getPropositionIdsToShow(), 
				new DateTimeFilter(
				new String[]{job.getDateRangeDataElementKey()},
				job.getEarliestDate(), AbsoluteTimeGranularity.DAY,
				job.getLatestDate(), AbsoluteTimeGranularity.DAY,
				job.getEarliestDateSide(), job.getLatestDateSide()));
		return jobEntity.getId();
	}

	@GET
	@RolesAllowed({"admin"})
	@Path("/status")
	public List<Job> getJobStatus(@QueryParam("filter") JobFilter inFilter) {
		List<Job> jobs = new ArrayList<>();
		for (JobEntity jobEntity : this.jobDao.getWithFilter(inFilter)) {
			jobs.add(jobEntity.toJob());
		}
		return jobs;
	}

	private EtlUser toEtlUser(Principal userPrincipal) {
		String username = userPrincipal.getName();
		EtlUser etlUser = this.etlUserDao.getByUsername(username);
		if (etlUser == null) {
			etlUser = new EtlUser();
			etlUser.setUsername(username);
			this.etlUserDao.create(etlUser);
		}
		return etlUser;
	}

	private JobEntity newJobEntity(JobSpec job, EtlUser etlUser) {
		JobEntity jobEntity = new JobEntity();
		jobEntity.setSourceConfigId(job.getSourceConfigId());
		jobEntity.setDestinationId(job.getDestinationId());
		jobEntity.setCreated(new Date());
		jobEntity.setEtlUser(etlUser);
		this.jobDao.create(jobEntity);
		return jobEntity;
	}

	private Response throwHttpStatusExceptionIfInvalid(JobRequest jobRequest) {
		JobSpec jobSpec = jobRequest.getJobSpec();
		List<PropositionDefinition> definitions = jobRequest.getUserPropositions();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Validating {} proposition definitions", definitions.size());
			for (PropositionDefinition pd : definitions) {
				LOGGER.debug("PropDef: {}", pd);
			}
		}
		propositionValidator.setConfigId(jobSpec.getSourceConfigId());
		propositionValidator.setUserPropositions(definitions);
		try {
			if (!propositionValidator.validate()) {
				throw new HttpStatusException(Status.BAD_REQUEST, 
						StringUtils.join(propositionValidator.getMessages(), '\n'));
			}
		} catch (PropositionValidatorException e) {
			throw new HttpStatusException(Status.INTERNAL_SERVER_ERROR, e);
		}
		return null;
	}
}
