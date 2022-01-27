package gg.scala.cloudsync.shared.repository.repositories

import gg.scala.cloudsync.shared.repository.pagination.PaginatedRepositoryWrapper
import gg.scala.cloudsync.shared.repository.component.ComponentWrapper

/**
 * @author GrowlyX
 * @since 1/21/2022
 */
class ComponentPaginatedRepositoryWrapper(
    override val items: MutableList<ComponentWrapper> = mutableListOf(),
    val continuationToken: String? = null
) : PaginatedRepositoryWrapper<ComponentWrapper>
