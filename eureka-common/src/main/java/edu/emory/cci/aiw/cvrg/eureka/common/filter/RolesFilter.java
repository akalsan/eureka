/*
 * #%L
 * Eureka Common
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
package edu.emory.cci.aiw.cvrg.eureka.common.filter;

import java.io.IOException;
import java.security.Principal;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

/**
 * A filter to fetch roles from a database, and assign them to the current
 * principal.
 *
 * @author hrathod
 *
 */
@Singleton
public class RolesFilter implements Filter {

	/**
	 * The class level logger.
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(RolesFilter.class);
	/**
	 * The datasource used to fetch the roles data.
	 */
	private DataSource dataSource;
	/**
	 * The SQL to run to fetch the roles data.
	 */
	private String sql;
	/**
	 * The column name in the result set that contains the role name.
	 */
	private String colName;

	@Override
	public void init(FilterConfig inFilterConfig) throws ServletException {
		this.sql = inFilterConfig.getInitParameter("sql");
		LOGGER.debug("Got SQL {}", this.sql);
		this.colName = inFilterConfig.getInitParameter("rolecolumn");
		LOGGER.debug("Got column name {}", this.colName);

		String sourceName = inFilterConfig.getInitParameter("datasource");
		LOGGER.debug("Using datasource {}", sourceName);
		try {
			Context context = new InitialContext();
			this.dataSource = (DataSource) context.lookup(sourceName);
		} catch (NamingException e) {
			LOGGER.error(e.getMessage(), e);
			throw new ServletException(e);
		}
	}

	@Override
	public void doFilter(ServletRequest inRequest, ServletResponse inResponse,
			FilterChain inChain) throws IOException, ServletException {
		HttpServletRequest servletRequest = (HttpServletRequest) inRequest;
		Principal principal = servletRequest.getUserPrincipal();
		if (principal != null) {
			Set<String> roles = new HashSet<>();
			String name = principal.getName();
			Connection connection = null;
			PreparedStatement preparedStatement = null;
			ResultSet resultSet = null;
			try {
				connection = this.dataSource.getConnection();
				preparedStatement = connection.prepareStatement(this.sql);
				preparedStatement.setString(1, name);
				resultSet = preparedStatement.executeQuery();
				while (resultSet.next()) {
					String role = resultSet.getString(this.colName);
					if (role != null) {
						//String authority = "ROLE_" + role.toUpperCase();
						//LOGGER.debug("Assigning role {}", authority);
						LOGGER.debug("Assigning role {}", role);
						//roles.add(authority);
						roles.add(role);
					}
				}
			} catch (SQLException e) {
				LOGGER.error(e.getMessage(), e);
				throw new ServletException(e);
			} finally {
				this.close(resultSet, preparedStatement, connection);
			}
			HttpServletRequest wrappedRequest = new RolesRequestWrapper(
					servletRequest, principal, roles);
			inChain.doFilter(wrappedRequest, inResponse);
		} else {
			inChain.doFilter(inRequest, inResponse);
		}
	}

	/**
	 * Properly close the give ResultSet, Statement, and Connection.
	 *
	 * @param resultSet The result set to dispose of.
	 * @param statement The statement to dispose of.
	 * @param connection The connection to dispose of.
	 */
	private void close(ResultSet resultSet, Statement statement, Connection connection) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException sqle) {
				LOGGER.error(sqle.getMessage(), sqle);
			}
		}

		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException sqle) {
				LOGGER.error(sqle.getMessage(), sqle);
			}
		}

		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException sqle) {
				LOGGER.error(sqle.getMessage(), sqle);
			}
		}
	}

	@Override
	public void destroy() {
	}
}
