package com.company.organization.infrastructure

import org.springframework.data.repository.CrudRepository

interface EmployeeCrudRepository : CrudRepository<EmployeeJpaEntity, Long> {
    fun findByNameIs(name: String): EmployeeJpaEntity?
}
