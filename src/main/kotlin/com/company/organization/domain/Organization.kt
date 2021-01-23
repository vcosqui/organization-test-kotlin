package com.company.organization.domain

import com.company.organization.infrastructure.EmployeeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class Organization(@Autowired val employeeRepository: EmployeeRepository) {

    fun getRootEmployee(): Employee? {
        return employeeRepository.findRoot()
    }

    fun addEmployees(employeesMap: Map<String, String>) {
        employeesMap.forEach { (employeeName, managerName) ->
            val employee = employeeRepository.findByNameOrCreate(employeeName)
            val manager = employeeRepository.findByNameOrCreate(managerName)
            employee.manager = manager
            manager.addManaged(employee)
            employeeRepository.save(employee)
        };
    }

}
