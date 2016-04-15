/*
 * #%L
 * Eureka WebApp
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
package edu.emory.cci.aiw.cvrg.eureka.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

import edu.emory.cci.aiw.cvrg.eureka.common.comm.clients.ServicesClient;
import edu.emory.cci.aiw.cvrg.eureka.servlet.worker.ServletWorker;
import edu.emory.cci.aiw.cvrg.eureka.servlet.worker.useracct
		.ListUserAcctWorker;
import edu.emory.cci.aiw.cvrg.eureka.servlet.worker.useracct.SaveUserAcctInfoWorker;
import edu.emory.cci.aiw.cvrg.eureka.servlet.worker.useracct
		.SaveUserAcctWorker;

public class UserAcctManagerServlet extends HttpServlet {

	private static final Logger LOGGER = LoggerFactory.getLogger
			(UserAcctManagerServlet.class);
	private final ServicesClient servicesClient;

	@Inject
	public UserAcctManagerServlet (ServicesClient inClient) {
		this.servicesClient = inClient;
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		doGet(req, resp);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		String action = req.getParameter("action");
		ServletWorker worker;

		if (action != null && action.equals("save")) {
			String id = req.getParameter("id");  
			if(id!=null && !id.isEmpty()){   
				LOGGER.info("Saving user info");
				worker = new SaveUserAcctInfoWorker(this.getServletContext(), this.servicesClient);                             
			} else {
				LOGGER.info("Saving user password");
				worker = new SaveUserAcctWorker(this.getServletContext(), this.servicesClient);                    
			}

		} else {
			LOGGER.info("Listing user");
			worker = new ListUserAcctWorker(this.servicesClient);
		}
		worker.execute(req, resp);
	}
}
