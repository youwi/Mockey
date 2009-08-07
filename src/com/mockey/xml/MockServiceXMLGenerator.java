/*
 * Copyright 2002-2006 the original author or authors.
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
 */
package com.mockey.xml;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mockey.MockServiceBean;
import com.mockey.MockServicePlan;
import com.mockey.MockServiceScenarioBean;
import com.mockey.MockServiceStore;
import com.mockey.PlanItem;

public class MockServiceXMLGenerator extends XMLGeneratorSupport {
	/** Basic logger */
	private static Logger logger = Logger.getLogger(MockServiceXMLGenerator.class);

	/**
	 * Returns an element representing a mock service definitions file in XML. 
	 * 
	 * @param document
	 *            parent DOM object of this element.
	 * @param cxmlObject
	 *            value object used to build element.
	 * @return Returns an element representing a cXML root element; if request
	 *         <code>null</code>, then empty element is returned e.g.
	 *         &lt;cXML/&gt;
	 */
	@SuppressWarnings("unchecked")
	public Element getElement(Document document, MockServiceStore store) {
		
		Element rootElement = document.createElement("mockservice");
		MockServiceScenarioBean mssb = store.getUniversalErrorResponse();		
		this.setAttribute(rootElement, "xml:lang", "en-US");
		this.setAttribute(rootElement, "version", "1.0");
		if(mssb!=null){
            this.setAttribute(rootElement,"universal_error_service_id", ""+mssb.getServiceId());
            this.setAttribute(rootElement,"universal_error_scenario_id", ""+mssb.getId());
        }

		Iterator iterator = store.getOrderedList().iterator();
		logger.debug("building DOM:");
		while (iterator.hasNext()) {
			MockServiceBean mockServiceBean = (MockServiceBean) iterator.next();
			Element serviceElement = document.createElement("service");
			rootElement.appendChild(serviceElement);

			if (mockServiceBean != null) {
				logger.debug("building XML representation for MockServiceBean:\n" + mockServiceBean.toString());
				// *************************************
				// We do NOT want to write out ID.
				// If we did, then someone uploading this xml definition may overwrite services
				// defined with the same ID.
				// serviceElement.setAttribute("id", mockServiceBean.getId());
				// *************************************
				serviceElement.setAttribute("id", ""+mockServiceBean.getId());
				serviceElement.setAttribute("name", mockServiceBean.getServiceName());
				serviceElement.setAttribute("description", mockServiceBean.getDescription());
				serviceElement.setAttribute("url", mockServiceBean.getMockServiceUrl());
				serviceElement.setAttribute("proxyurl", mockServiceBean.getRealServiceUrl());
				serviceElement.setAttribute("hang_time", "" + mockServiceBean.getHangTime());
				serviceElement.setAttribute("http_header_definition", "" + mockServiceBean.getHttpHeaderDefinition());
				serviceElement.setAttribute("default_scenario_id", "" + (mockServiceBean.getDefaultScenarioId()!=null ?  mockServiceBean.getDefaultScenarioId(): ""));
				serviceElement.setAttribute("service_response_type", "" + mockServiceBean.getServiceResponseType());

				List scenarios = mockServiceBean.getScenarios();
				Iterator iter = scenarios.iterator();

				while (iter.hasNext()) {
					MockServiceScenarioBean scenario = (MockServiceScenarioBean) iter.next();
					logger.debug("building XML representation for MockServiceScenarioBean:\n" + scenario.toString());
					Element scenarioElement = document.createElement("scenario");
					scenarioElement.setAttribute("id", scenario.getId().toString());
					scenarioElement.setAttribute("name", scenario.getScenarioName());

					Element scenarioMatchStringElement = document.createElement("scenario_match");
					CDATASection cdataMatchElement = document.createCDATASection(scenario.getMatchStringArg());
					scenarioMatchStringElement.appendChild(cdataMatchElement);
					scenarioElement.appendChild(scenarioMatchStringElement);
					// this.setText(document, scenarioElement,
					// scenario.getMatchStringArg());

					Element scenarioResponseElement = document.createElement("scenario_response");
					CDATASection cdataResponseElement = document.createCDATASection(scenario.getResponseMessage());
					scenarioResponseElement.appendChild(cdataResponseElement);
					scenarioElement.appendChild(scenarioResponseElement);
					serviceElement.appendChild(scenarioElement);
				}
			}
		}
		
		// SERVICE PLANS
		
		List servicePlans = store.getMockServicePlanList();
		if(servicePlans!=null){
			Iterator iter = servicePlans.iterator();
			while(iter.hasNext()){
				MockServicePlan servicePlan = (MockServicePlan)iter.next();
				Element servicePlanElement = document.createElement("service_plan");
				servicePlanElement.setAttribute("name", servicePlan.getName());
				servicePlanElement.setAttribute("description", servicePlan.getDescription());
				servicePlanElement.setAttribute("id", ""+servicePlan.getId());
				
				Iterator planItemIter = servicePlan.getPlanItemList().iterator();
				while(planItemIter.hasNext()){
					PlanItem  pi = (PlanItem)planItemIter.next();
					Element planItemElement = document.createElement("plan_item");
					planItemElement.setAttribute("hang_time", ""+pi.getHangTime());
					planItemElement.setAttribute("service_id", ""+pi.getServiceId());
					planItemElement.setAttribute("scenario_id", ""+pi.getScenarioId());
					planItemElement.setAttribute("service_response_type", ""+pi.getServiceResponseType());
					
					servicePlanElement.appendChild(planItemElement);
				}
				
				rootElement.appendChild(servicePlanElement);
				
			}
		}

		return rootElement;
	}
}
