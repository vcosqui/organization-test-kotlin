package com.company.organization.rest

import com.company.organization.domain.port.OrganizationUseCase
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE as APPLICATION_JSON
import org.springframework.web.bind.annotation.GetMapping as GET
import org.springframework.web.bind.annotation.PostMapping as POST

@RestController
class OrganizationController(@Autowired val organization: OrganizationUseCase) {

    @POST("/organization", consumes = [APPLICATION_JSON])
    fun setOrganization(@RequestBody employeesMap: Map<String, String>) = organization.addEmployees(employeesMap)

    @GET("/organization", produces = [APPLICATION_JSON])
    fun getOrganization(): Map<String, Any> {
        return topDown(listOfNotNull(organization.getRootEmployee()))
    }

    @GET("/organization/employee/{employeeName}/management", produces = [APPLICATION_JSON])
    fun getEmployeeManagement(@PathVariable employeeName: String): Map<String, Any> {
        return upstreamHierarchy(organization.getEmployee(employeeName))
    }
}
