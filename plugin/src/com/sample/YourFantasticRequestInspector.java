/*
 * This file is part of Mockey, a tool for testing application 
 * interactions over HTTP, with a focus on testing web services, 
 * specifically web applications that consume XML, JSON, and HTML.
 *  
 * Copyright (C) 2012  Authors:
 * 
 * chad.lafontaine (chad.lafontaine AT gmail DOT com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package com.sample;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import com.mockey.plugin.IRequestInspector;

/**
 * This is a sample implementation of the <code>IRequestInspector</code>
 * 
 * @author chadlafontaine
 * 
 */
public class YourFantasticRequestInspector implements IRequestInspector {
	private static Logger logger = Logger.getLogger(YourFantasticRequestInspector.class);

	private final String FANTASTIC = "fantastic";
	private String errorMessage = null;

	/**
	 * Any time someone passes in a 'foobarKey' as a parameter, then this
	 * inspector will build a message.
	 */
	public void analyze(HttpServletRequest request) {
		if (request.getParameter(FANTASTIC) != null) {
			this.errorMessage = "Howdy! This isn't a real error. "
					+ "This is here to show you that your request inspector '" + this.getClass().getName()
					+ "' is working. You passed in the request parameter key '" + FANTASTIC + "'.";
			logger.error(this.errorMessage);
		}

	}

	/**
	 * 
	 * @return true if the message is not null or empty, otherwise false
	 * @see #getPostAnalyzeResultMessage()
	 */
	public boolean hasPostAnalyzeMessage() {
		if (this.errorMessage != null) {
			return true;
		} else {
			return false;
		}

	}

	/**
	 * @return String if a message is available, otherwise null
	 */
	public String getPostAnalyzeResultMessage() {
		return this.errorMessage;
	}

	/**
	 * 
	 * 
	 * @return false.
	 */
	public boolean isGlobal() {
		// Set to true if applicable to all incoming requests
		return false;
	}

}
