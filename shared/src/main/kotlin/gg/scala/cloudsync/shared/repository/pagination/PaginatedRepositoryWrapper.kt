package gg.scala.cloudsync.shared.repository.pagination

/**
 * @author GrowlyX
 * @since 1/21/2022
 */
interface PaginatedRepositoryWrapper<T>
{
    val items: MutableList<T>
}
