package com.company.organization.infrastructure

import com.company.organization.domain.Employee
import com.company.organization.domain.Organization
import com.company.organization.domain.port.OrganizationRepositoryPort
import org.springframework.stereotype.Component

@Component
class OrganizationRepositoryAdapter(
    private val employeeCrudRepository: EmployeeCrudRepository
) : OrganizationRepositoryPort {

    override fun load(): Organization {
        val allEntities = employeeCrudRepository.findAll().toList()
        if (allEntities.isEmpty()) return Organization.empty()

        val domainMap: Map<String, Employee> = allEntities.associate { entity ->
            entity.name to Employee(entity.id, entity.name, null)
        }

        allEntities.forEach { entity ->
            entity.manager?.let { managerEntity ->
                val employee = domainMap[entity.name]!!
                val manager = domainMap[managerEntity.name]!!
                employee.manager = manager
                if (!manager.managed.contains(employee)) manager.addManaged(employee)
            }
        }

        return Organization.reconstitute(domainMap.values.toList())
    }

    override fun save(organization: Organization) {
        organization.allEmployees.forEach { employee ->
            val jpaEntity = employeeCrudRepository.findByNameIs(employee.name)
                ?: EmployeeJpaEntity(name = employee.name)
            val jpaManager = employee.manager?.let { mgr ->
                employeeCrudRepository.findByNameIs(mgr.name) ?: EmployeeJpaEntity(name = mgr.name)
            }
            jpaEntity.manager = jpaManager
            employeeCrudRepository.save(jpaEntity)
        }
    }
}
