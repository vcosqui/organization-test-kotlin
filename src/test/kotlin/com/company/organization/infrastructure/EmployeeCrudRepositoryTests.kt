package com.company.organization.infrastructure

import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional

@SpringBootTest
@Transactional
class EmployeeCrudRepositoryTests @Autowired constructor(
    val entityManager: EntityManager,
    val employeeCrudRepository: EmployeeCrudRepository
) {

    @Test
    fun `findByNameIs returns the employee when it exists`() {
        val name = "alan"
        createEmployee(EmployeeJpaEntity(name = name))
        val employee = employeeCrudRepository.findByNameIs(name)
        assertThat(employee).isEqualTo(EmployeeJpaEntity(name = name))
    }

    @Test
    fun `findAll returns all persisted employees`() {
        createEmployee(EmployeeJpaEntity(name = "alan"))
        createEmployee(EmployeeJpaEntity(name = "ana"))
        val all = employeeCrudRepository.findAll().toList()
        assertThat(all).hasSize(2)
    }

    private fun createEmployee(entity: EmployeeJpaEntity) {
        entityManager.persist(entity)
        entityManager.flush()
    }
}
