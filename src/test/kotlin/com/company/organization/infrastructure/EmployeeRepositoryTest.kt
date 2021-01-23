package com.company.organization.infrastructure

import com.company.organization.domain.Employee
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class EmployeeRepositoryTest {

    @Test
    fun `findByNameOrCreate should return the existing employee`() {
        val employee = Employee(1, "name", null)
        val crudRepositoryMock = mock<EmployeeCrudRepository> {
            on { findByNameIs("name") } doReturn employee
        }
        val employeeRepository = EmployeeRepository(crudRepositoryMock)

        val foundEmployee = employeeRepository.findByNameOrCreate("name")

        assertThat(foundEmployee == employee)
    }

    @Test
    fun `findByNameOrCreate should return a new employee`() {
        val employee = Employee(1, "name", null)
        val crudRepositoryMock = mock<EmployeeCrudRepository> {
            on { findByNameIs("name") } doReturn null
        }
        val employeeRepository = EmployeeRepository(crudRepositoryMock)

        val foundEmployee = employeeRepository.findByNameOrCreate("name")

        assertThat(foundEmployee == employee)
    }
}