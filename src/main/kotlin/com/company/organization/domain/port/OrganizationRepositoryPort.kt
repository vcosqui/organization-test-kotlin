package com.company.organization.domain.port

import com.company.organization.domain.Organization

interface OrganizationRepositoryPort {
    fun load(): Organization
    fun save(organization: Organization)
    fun deleteByName(name: String)
}
