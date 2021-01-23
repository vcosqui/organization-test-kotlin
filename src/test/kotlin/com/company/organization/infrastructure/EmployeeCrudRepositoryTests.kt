package com.company.organization.infrastructure

import com.company.organization.domain.Employee
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class EmployeeCrudRepositoryTests @Autowired constructor(
        val entityManager: TestEntityManager,
        val employeeCrudRepository: EmployeeCrudRepository) {

    @Test
    fun `When findByNameIs then return Employee`() {
        val name = "alan"
        createEmployee(Employee(null, name, null))
        val employee = employeeCrudRepository.findByNameIs(name)
        assertThat(employee).isEqualTo(Employee(null, name, null))
    }

    @Test
    fun `When findDistinctByManagerIsNull then return all managers`() {
        val alan = Employee(null, "alan", null)
        createEmployee(alan)
        val ana = Employee(null, "alan", null)
        createEmployee(ana)
        createEmployee(Employee(null, "mika", alan))
        createEmployee(Employee(null, "lula", ana))
        val managers = employeeCrudRepository.findDistinctByManagerIsNull()
        assertThat(managers.size).isEqualTo(2)
        assertThat(managers.contains(alan))
        assertThat(managers.contains(ana))
    }

    @Test
    fun `When findDistinctByManagerIsNull then count all managers`() {
        val alan = Employee(null, "alan", null)
        createEmployee(alan)
        val ana = Employee(null, "alan", null)
        createEmployee(ana)
        createEmployee(Employee(null, "mika", alan))
        createEmployee(Employee(null, "lula", ana))
        val managersCount = employeeCrudRepository.countDistinctByManagerIsNull()
        assertThat(managersCount).isEqualTo(2)
    }

    private fun createEmployee(employee: Employee) {
        entityManager.persist(employee)
        entityManager.flush()
    }
}