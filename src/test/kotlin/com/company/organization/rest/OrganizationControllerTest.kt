package com.company.organization.rest

import com.company.organization.domain.Employee
import com.company.organization.domain.Organization
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

internal class OrganizationControllerTest {

    @Test
    fun `getOrganization returns downstream hierarchy`() {
        val root = Employee(1, "root", null)
        val org = mock<Organization> { on { getRootEmployee() } doReturn root }
        val controller = OrganizationController(org)

        val result = controller.getOrganization()

        assertThat(result).isEqualTo(mapOf("root" to mapOf<String, Any>()))
    }

    @Test
    fun `getOrganization returns empty map when no root`() {
        val org = mock<Organization> { on { getRootEmployee() } doReturn null }
        val controller = OrganizationController(org)

        val result = controller.getOrganization()

        assertThat(result).isEmpty()
    }

    @Test
    fun `setOrganization calls addEmployees`() {
        val org = mock<Organization>()
        val controller = OrganizationController(org)
        val payload = mapOf("bob" to "alice")

        controller.setOrganization(payload)

        verify(org).addEmployees(payload)
    }

    @Test
    fun `getEmployeeManagement returns upstream chain`() {
        val manager = Employee(1, "alice", null)
        val employee = Employee(2, "bob", manager)
        val org = mock<Organization> { on { getEmployee("bob") } doReturn employee }
        val controller = OrganizationController(org)

        val result = controller.getEmployeeManagement("bob")

        assertThat(result).isEqualTo(mapOf("bob" to mapOf("alice" to mapOf<String, Any>())))
    }
}
