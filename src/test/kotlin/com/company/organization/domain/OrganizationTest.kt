package com.company.organization.domain

import com.company.organization.infrastructure.EmployeeRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

internal class OrganizationTest {

    @Test
    fun `getRootEmployee returns root employee`() {
        val root = Employee(1, "root", null)
        val repo = mock<EmployeeRepository> { on { findRoot() } doReturn root }
        val org = Organization(repo)

        assertThat(org.getRootEmployee()).isEqualTo(root)
    }

    @Test
    fun `getEmployee returns employee by name`() {
        val employee = Employee(1, "alice", null)
        val repo = mock<EmployeeRepository> { on { findByName("alice") } doReturn employee }
        val org = Organization(repo)

        assertThat(org.getEmployee("alice")).isEqualTo(employee)
    }

    @Test
    fun `getEmployee throws when employee not found`() {
        val repo = mock<EmployeeRepository> { on { findByName("ghost") } doReturn null }
        val org = Organization(repo)

        assertThrows<IllegalOrganizationException> { org.getEmployee("ghost") }
    }

    @Test
    fun `addEmployees calls repository save and sets manager`() {
        val employee = Employee(null, "bob", null)
        val manager = Employee(null, "alice", null)
        val repo = mock<EmployeeRepository> {
            on { findByNameOrCreate("bob") } doReturn employee
            on { findByNameOrCreate("alice") } doReturn manager
            on { countRoots() } doReturn 1L
        }
        val org = Organization(repo)

        org.addEmployees(mapOf("bob" to "alice"))

        verify(repo).save(employee)
        assertThat(employee.manager).isEqualTo(manager)
    }

    @Test
    fun `addEmployees should fail when more than one root`() {
        val employee = Employee(null, "bob", null)
        val manager = Employee(null, "alice", null)
        val repo = mock<EmployeeRepository> {
            on { findByNameOrCreate("bob") } doReturn employee
            on { findByNameOrCreate("alice") } doReturn manager
            on { countRoots() } doReturn 2L
        }
        val org = Organization(repo)

        assertThrows<IllegalOrganizationException> { org.addEmployees(mapOf("bob" to "alice")) }
    }

    @Test
    fun `addEmployees should fail on cyclic dependency`() {
        val alice = Employee(1, "alice", null)
        val bob = Employee(2, "bob", alice)
        alice.addManaged(bob)
        val repo = mock<EmployeeRepository> {
            on { findByNameOrCreate("alice") } doReturn alice
            on { findByNameOrCreate("bob") } doReturn bob
            on { countRoots() } doReturn 1L
        }
        val org = Organization(repo)

        // setting alice's manager to bob would create alice → bob → alice cycle
        assertThrows<IllegalOrganizationException> { org.addEmployees(mapOf("alice" to "bob")) }
    }
}
