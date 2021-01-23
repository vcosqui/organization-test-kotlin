package com.company.organization.infrastructure

import com.company.organization.domain.Employee
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class EmployeeRepository(@Autowired val employeeCrudRepository: EmployeeCrudRepository) {


    fun findByNameOrCreate(name: String): Employee {
        return employeeCrudRepository.findByNameIs(name) ?: Employee(null, name, null)
    }

    fun findRoot(): Employee? {
        return employeeCrudRepository.findDistinctByManagerIsNull().first()
    }

    fun countRoots(): Long {
        return employeeCrudRepository.countDistinctByManagerIsNull()
    }

    fun save(employee: Employee) {
        employeeCrudRepository.save(employee)
    }
}