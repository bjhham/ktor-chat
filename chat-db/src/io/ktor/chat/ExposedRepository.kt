package io.ktor.chat

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

abstract class ExposedRepository<E: Identifiable<ID>, ID: Comparable<ID>, T: IdTable<ID>>(
    private val database: Database,
    private val table: T,
): Repository<E, ID> {
    override suspend fun get(id: ID): E? =
        withTransaction {
            table.select { table.id eq id }
                .map(::rowToEntity)
                .singleOrNull()
        }
    
    override suspend fun create(e: E): E =
        withTransaction {
            val newId = table.insert(assignColumns(e))[table.id]
            e.withId(newId.value)
        }
    
    override suspend fun update(e: E) {
        withTransaction {
            table.update({ table.id eq e.id }, limit = 1, assignColumns(e))
        }
    }
    
    override suspend fun delete(id: ID) {
        withTransaction {
            table.deleteWhere { table.id eq id }
        }
    }
    
    override suspend fun list(): List<E> =
        withTransaction {
            table.selectAll()
                .map(::rowToEntity)
                .toList()
        }
    
    private suspend fun <T> withTransaction(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO, database) { block() }
    
    protected abstract fun rowToEntity(row: ResultRow): E
    protected abstract fun assignColumns(e: E): T.(UpdateBuilder<*>) -> Unit
    protected abstract fun E.withId(id: ID): E
}