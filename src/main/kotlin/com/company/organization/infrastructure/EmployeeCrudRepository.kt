package com.company.organization.infrastructure

import com.company.organization.domain.Employee
import org.springframework.data.repository.CrudRepository

open interface EmployeeCrudRepository : CrudRepository<Employee, Long> {

    fun findByNameIs(name: String): Employee?

    fun findDistinctByManagerIsNull(): List<Employee>

    fun countDistinctByManagerIsNull(): Long

}