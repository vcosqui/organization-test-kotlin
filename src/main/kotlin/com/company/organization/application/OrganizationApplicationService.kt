package com.company.organization.application

import com.company.organization.domain.Employee
import com.company.organization.domain.port.OrganizationRepositoryPort
import com.company.organization.domain.port.OrganizationUseCase

class OrganizationApplicationService(
    private val organizationRepository: OrganizationRepositoryPort
) : OrganizationUseCase {

    override fun getRootEmployee(): Employee? =
        organizationRepository.load().getRootEmployee()

    override fun getEmployee(name: String): Employee =
        organizationRepository.load().getEmployee(name)

    override fun addEmployees(employeesMap: Map<String, String>) {
        val organization = organizationRepository.load()
        organization.addEmployees(employeesMap)
        organizationRepository.save(organization)
    }
}
