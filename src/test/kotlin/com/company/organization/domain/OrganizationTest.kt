package com.company.organization.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

internal class OrganizationTest {

    @Test
    fun `getRootEmployee returns null when organization is empty`() {
        assertThat(Organization.empty().getRootEmployee()).isNull()
    }

    @Test
    fun `getRootEmployee returns root employee`() {
        val root = Employee(1, "root", null)
        val org = Organization.reconstitute(listOf(root))
        assertThat(org.getRootEmployee()).isEqualTo(root)
    }

    @Test
    fun `getEmployee returns employee by name`() {
        val alice = Employee(1, "alice", null)
        val org = Organization.reconstitute(listOf(alice))
        assertThat(org.getEmployee("alice")).isEqualTo(alice)
    }

    @Test
    fun `getEmployee throws when employee not found`() {
        assertThrows<IllegalOrganizationException> { Organization.empty().getEmployee("ghost") }
    }

    @Test
    fun `addEmployees creates employees and sets manager relationship`() {
        val org = Organization.empty()
        org.addEmployees(mapOf("bob" to "alice"))

        val bob = org.getEmployee("bob")
        val alice = org.getEmployee("alice")
        assertThat(bob.manager).isEqualTo(alice)
        assertThat(alice.managed).contains(bob)
    }

    @Test
    fun `addEmployees results in a single root`() {
        val org = Organization.empty()
        org.addEmployees(mapOf("bob" to "alice"))
        assertThat(org.getRootEmployee()?.name).isEqualTo("alice")
    }

    @Test
    fun `addEmployees should fail when more than one root would exist`() {
        // Start with two existing roots (alice and charlie), then add a subordinate under alice
        // — charlie remains a second root, so the invariant must be violated
        val alice = Employee(null, "alice", null)
        val charlie = Employee(null, "charlie", null)
        val org = Organization.reconstitute(listOf(alice, charlie))

        assertThrows<IllegalOrganizationException> { org.addEmployees(mapOf("dave" to "alice")) }
    }

    @Test
    fun `addEmployees should fail on cyclic dependency`() {
        val alice = Employee(1, "alice", null)
        val bob = Employee(2, "bob", alice)
        alice.addManaged(bob)
        val org = Organization.reconstitute(listOf(alice, bob))

        // setting alice's manager to bob would create alice → bob → alice cycle
        assertThrows<IllegalOrganizationException> { org.addEmployees(mapOf("alice" to "bob")) }
    }
}
