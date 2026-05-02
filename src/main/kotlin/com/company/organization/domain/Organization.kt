package com.company.organization.domain

import com.company.organization.infrastructure.EmployeeRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class Organization(@Autowired val employeeRepository: EmployeeRepository) {

    fun getRootEmployee(): Employee? {
        return employeeRepository.findRoot()
    }

    fun getEmployee(name: String): Employee {
        return employeeRepository.findByName(name)
            ?: throw IllegalOrganizationException("Employee '$name' not found")
    }

    fun addEmployees(employeesMap: Map<String, String>) {
        employeesMap.forEach { (employeeName, managerName) ->
            val employee = employeeRepository.findByNameOrCreate(employeeName)
            val manager = employeeRepository.findByNameOrCreate(managerName)
            checkCyclicDep(manager, employee)
            employee.manager = manager
            manager.addManaged(employee)
            employeeRepository.save(employee)
        }
        verifySingleRoot()
    }

    private fun verifySingleRoot() {
        if (employeeRepository.countRoots() > 1) {
            throw IllegalOrganizationException("Organization must have a single root")
        }
    }

    private fun checkCyclicDep(employee: Employee, target: Employee) {
        if (employee == target) throw IllegalOrganizationException("Cyclic dependency detected for '${target.name}'")
        employee.manager?.let { checkCyclicDep(it, target) }
    }
}
