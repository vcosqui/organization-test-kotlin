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
                val employee = domainMap[entity.name]
                    ?: error("Employee '${entity.name}' missing from domain map — data integrity issue")
                val manager = domainMap[managerEntity.name]
                    ?: error("Manager '${managerEntity.name}' missing from domain map — data integrity issue")
                employee.manager = manager
                if (!manager.managed.contains(employee)) manager.addManaged(employee)
            }
        }

        return Organization.reconstitute(domainMap.values.toList())
    }

    override fun save(organization: Organization) {
        val existingByName: Map<String, EmployeeJpaEntity> =
            employeeCrudRepository.findAll().associateBy { it.name }

        val jpaByName: Map<String, EmployeeJpaEntity> = organization.allEmployees.associate { employee ->
            employee.name to (existingByName[employee.name] ?: EmployeeJpaEntity(name = employee.name))
        }

        organization.allEmployees.forEach { employee ->
            val jpaEntity = jpaByName[employee.name]!!
            jpaEntity.manager = employee.manager?.let { jpaByName[it.name] }
        }

        employeeCrudRepository.saveAll(jpaByName.values.toList())
    }
}
