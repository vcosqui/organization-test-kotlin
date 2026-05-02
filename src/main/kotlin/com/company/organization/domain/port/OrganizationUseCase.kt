package com.company.organization.domain.port

import com.company.organization.domain.Employee

interface OrganizationUseCase {
    fun getRootEmployee(): Employee?
    fun getEmployee(name: String): Employee
    fun addEmployees(employeesMap: Map<String, String>)
}
