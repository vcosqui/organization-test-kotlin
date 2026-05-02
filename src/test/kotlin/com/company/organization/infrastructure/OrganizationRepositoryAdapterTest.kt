package com.company.organization.infrastructure

import com.company.organization.domain.Employee
import com.company.organization.domain.Organization
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

internal class OrganizationRepositoryAdapterTest {

    @Test
    fun `load returns empty organization when no employees exist`() {
        val crudRepo = mock<EmployeeCrudRepository> { on { findAll() } doReturn emptyList() }
        val org = OrganizationRepositoryAdapter(crudRepo).load()

        assertThat(org.allEmployees).isEmpty()
        assertThat(org.getRootEmployee()).isNull()
    }

    @Test
    fun `load wires manager and managed relationships`() {
        val aliceEntity = EmployeeJpaEntity(id = 1, name = "alice")
        val bobEntity = EmployeeJpaEntity(id = 2, name = "bob", manager = aliceEntity)
        val crudRepo = mock<EmployeeCrudRepository> {
            on { findAll() } doReturn listOf(aliceEntity, bobEntity)
        }
        val org = OrganizationRepositoryAdapter(crudRepo).load()

        val alice = org.getEmployee("alice")
        val bob = org.getEmployee("bob")
        assertThat(bob.manager).isEqualTo(alice)
        assertThat(alice.managed).contains(bob)
        assertThat(org.getRootEmployee()).isEqualTo(alice)
    }

    @Test
    fun `save persists all employees with correct manager FK`() {
        val aliceEntity = EmployeeJpaEntity(id = 1, name = "alice")
        val bobEntity = EmployeeJpaEntity(id = 2, name = "bob")
        val crudRepo = mock<EmployeeCrudRepository> {
            on { findByNameIs("alice") } doReturn aliceEntity
            on { findByNameIs("bob") } doReturn bobEntity
        }

        val alice = Employee(1, "alice", null)
        val bob = Employee(2, "bob", alice)
        alice.addManaged(bob)
        val org = Organization.reconstitute(listOf(alice, bob))

        OrganizationRepositoryAdapter(crudRepo).save(org)

        assertThat(bobEntity.manager).isEqualTo(aliceEntity)
        assertThat(aliceEntity.manager).isNull()
        verify(crudRepo).save(aliceEntity)
        verify(crudRepo).save(bobEntity)
    }

    @Test
    fun `save creates new JPA entities for employees not yet persisted`() {
        val crudRepo = mock<EmployeeCrudRepository> { on { findByNameIs(any()) } doReturn null }
        val alice = Employee(null, "alice", null)
        val org = Organization.reconstitute(listOf(alice))

        OrganizationRepositoryAdapter(crudRepo).save(org)

        verify(crudRepo).save(any<EmployeeJpaEntity>())
    }
}
