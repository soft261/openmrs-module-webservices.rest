/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */
package org.openmrs.module.webservices.rest.web.resource;

import java.util.ArrayList;
import java.util.List;

import org.openmrs.Person;
import org.openmrs.PersonName;
import org.openmrs.annotation.Handler;
import org.openmrs.api.context.Context;
import org.openmrs.module.webservices.rest.web.RequestContext;
import org.openmrs.module.webservices.rest.web.RestConstants;
import org.openmrs.module.webservices.rest.web.annotation.Resource;
import org.openmrs.module.webservices.rest.web.annotation.SubResource;
import org.openmrs.module.webservices.rest.web.representation.DefaultRepresentation;
import org.openmrs.module.webservices.rest.web.representation.FullRepresentation;
import org.openmrs.module.webservices.rest.web.representation.Representation;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingResourceDescription;
import org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource;
import org.openmrs.module.webservices.rest.web.response.ResponseException;

/**
 * {@link Resource} for PersonNames, supporting standard CRUD operations
 */
@SubResource(parent = PersonResource.class, path = "names")
@Handler(supports = PersonName.class, order = 0)
public class PersonNameResource extends DelegatingSubResource<PersonName, Person, PersonResource> {
	
	@Override
	public DelegatingResourceDescription getRepresentationDescription(Representation rep) {
		if (rep instanceof DefaultRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("givenName");
			description.addProperty("middleName");
			description.addProperty("familyName");
			description.addProperty("familyName2");
			description.addSelfLink();
			description.addLink("full", ".?v=" + RestConstants.REPRESENTATION_FULL);
			return description;
		} else if (rep instanceof FullRepresentation) {
			DelegatingResourceDescription description = new DelegatingResourceDescription();
			description.addProperty("givenName");
			description.addProperty("middleName");
			description.addProperty("familyName");
			description.addProperty("familyName2");
			description.addProperty("auditInfo", findMethod("getAuditInfo"));
			description.addSelfLink();
			return description;
		}
		return null;
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#getParent(java.lang.Object)
	 */
	@Override
	public Person getParent(PersonName instance) {
		return instance.getPerson();
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.impl.DelegatingSubResource#setParent(java.lang.Object,
	 *      java.lang.Object)
	 */
	@Override
	public void setParent(PersonName instance, Person person) {
		instance.setPerson(person);
	}
	
	/**
	 * @see org.openmrs.module.webservices.rest.web.resource.api.SubResource#doGetAll(java.lang.Object,
	 *      org.openmrs.module.webservices.rest.web.RequestContext)
	 */
	@Override
	public List<PersonName> doGetAll(Person parent, RequestContext context) throws ResponseException {
		List<PersonName> names = new ArrayList<PersonName>();
		
		if (parent != null) {
			names.addAll(parent.getNames());
		}
		
		return names;
	}
	
	@Override
	public PersonName getByUniqueId(String uuid) {
		return Context.getPersonService().getPersonNameByUuid(uuid);
	}
	
	@Override
	public void delete(PersonName pn, String reason, RequestContext context) throws ResponseException {
		// TODO Auto-generated function stub
	}
	
	@Override
	public void purge(PersonName pn, RequestContext context) throws ResponseException {
		// TODO Auto-generated function stub	
	}
	
	@Override
	protected PersonName save(PersonName newName) {
		// make sure that the name has actually been added to the person
		boolean needToAdd = true;
		for (PersonName pn : newName.getPerson().getNames()) {
			if (pn.equals(newName)) {
				needToAdd = false;
				break;
			}
		}
		if (needToAdd)
			newName.getPerson().addName(newName);
		Context.getPersonService().savePerson(newName.getPerson());
		return newName;
	}
	
	@Override
	protected PersonName newDelegate() {
		return new PersonName();
	}
	
	/**
	 * Gets the display string for a person name.
	 * 
	 * @param personName the person name object.
	 * @return the display string.
	 */
	public String getDisplayString(PersonName personName) {
		return personName.getFullName();
	}
}
