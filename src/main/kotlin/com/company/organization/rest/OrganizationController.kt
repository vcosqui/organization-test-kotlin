package com.company.organization.rest

import com.company.organization.domain.Organization
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE as APPLICATION_JSON
import org.springframework.web.bind.annotation.GetMapping as GET

@RestController
class OrganizationController(@Autowired val organization: Organization) {

    @GET("/organization", consumes = [APPLICATION_JSON])
    fun setOrganization(employeesMap: Map<String, String>) = organization.addEmployees(employeesMap)

    @GET("/organization", produces = [APPLICATION_JSON])
    fun getOrganization(): Map<String, Any> {
        return topDown(listOfNotNull(organization.getRootEmployee()))
    }
}
