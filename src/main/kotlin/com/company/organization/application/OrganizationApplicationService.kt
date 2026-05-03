package com.company.organization.application

import com.company.organization.domain.Employee
import com.company.organization.domain.port.OrganizationRepositoryPort
import com.company.organization.domain.port.OrganizationUseCase
import org.springframework.transaction.annotation.Transactional

@Transactional
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

    override fun removeEmployee(name: String) {
        val organization = organizationRepository.load()
        organization.removeEmployee(name)
        organizationRepository.deleteByName(name)
    }
}
