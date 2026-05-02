package com.company.organization.rest

import com.company.organization.domain.port.OrganizationUseCase
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class OrganizationController(val organization: OrganizationUseCase) {

    @PostMapping("/organization", consumes = [APPLICATION_JSON_VALUE])
    fun setOrganization(@RequestBody employeesMap: Map<String, String>) = organization.addEmployees(employeesMap)

    @GetMapping("/organization", produces = [APPLICATION_JSON_VALUE])
    fun getOrganization(): Map<String, Any> {
        return topDown(listOfNotNull(organization.getRootEmployee()))
    }

    @GetMapping("/organization/employee/{employeeName}/management", produces = [APPLICATION_JSON_VALUE])
    fun getEmployeeManagement(@PathVariable employeeName: String): Map<String, Any> {
        return upstreamHierarchy(organization.getEmployee(employeeName))
    }
}
