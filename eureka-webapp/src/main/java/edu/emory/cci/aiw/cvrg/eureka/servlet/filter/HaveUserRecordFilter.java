package edu.emory.cci.aiw.cvrg.eureka.servlet.filter;

/*
 * #%L
 * Eureka WebApp
 * %%
 * Copyright (C) 2012 - 2014 Emory University
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
import java.io.IOException;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.sun.jersey.api.client.ClientResponse.Status;

import edu.emory.cci.aiw.cvrg.eureka.common.comm.User;
import edu.emory.cci.aiw.cvrg.eureka.common.comm.clients.ClientException;
import edu.emory.cci.aiw.cvrg.eureka.common.comm.clients.ServicesClient;
import edu.emory.cci.aiw.cvrg.eureka.webapp.config.WebappProperties;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author Andrew Post
 */
@Singleton
public class HaveUserRecordFilter implements Filter {

	private static final Logger LOGGER
			= LoggerFactory.getLogger(MessagesFilter.class);

	private final ServicesClient servicesClient;
	private final WebappProperties properties;

	@Inject
	public HaveUserRecordFilter(ServicesClient inServicesClient, WebappProperties inProperties) {
		this.servicesClient = inServicesClient;
		this.properties = inProperties;
	}

	@Override
	public void init(FilterConfig inFilterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest inRequest, ServletResponse inResponse, FilterChain inFilterChain) throws IOException, ServletException {
		HttpServletRequest servletRequest = (HttpServletRequest) inRequest;
		HttpServletResponse servletResponse = (HttpServletResponse) inResponse;
		String remoteUser = servletRequest.getRemoteUser();
		if (!StringUtils.isEmpty(remoteUser)) {
			try {
				User user = this.servicesClient.getMe();
				if (!user.isActive()) {
					HttpSession session = servletRequest.getSession(false);
					if (session != null) {
						session.invalidate();
					}
					sendForbiddenError(servletResponse, servletRequest, true);
				} else {
					inRequest.setAttribute("user", user);
					inFilterChain.doFilter(inRequest, inResponse);
				}
			} catch (ClientException ex) {
				if (Status.FORBIDDEN.equals(ex.getResponseStatus())) {
					HttpSession session = servletRequest.getSession(false);
					if (session != null) {
						session.invalidate();
					}
					sendForbiddenError(servletResponse, servletRequest, false);
				} else if (Status.UNAUTHORIZED.equals(ex.getResponseStatus())) {
					HttpSession session = servletRequest.getSession(false);
					if (session != null) {
						session.invalidate();
					}
					servletResponse.sendRedirect(servletRequest.getContextPath() + "/logout?goHome=true");
				} else {
					throw new ServletException("Error getting user "
							+ servletRequest.getRemoteUser(), ex);
				}
			}
		} else {
			inFilterChain.doFilter(inRequest, inResponse);
		}
	}

	private void sendForbiddenError(HttpServletResponse servletResponse, HttpServletRequest servletRequest, boolean created) throws IOException {
		if (this.properties.isRegistrationEnabled()) {
			servletResponse.setStatus(HttpServletResponse.SC_FORBIDDEN);
			if (created) {
				servletResponse.sendRedirect(servletRequest.getContextPath() + "/logout?awaitingActivation=true");
			} else {
				servletResponse.sendRedirect(servletRequest.getContextPath() + "/logout?notRegistered=true");
			}
		} else {
			servletResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
		}
	}

	@Override
	public void destroy() {
	}
	
}
