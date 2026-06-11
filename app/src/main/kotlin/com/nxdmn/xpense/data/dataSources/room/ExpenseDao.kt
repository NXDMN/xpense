package com.nxdmn.xpense.data.dataSources.room

import androidx.room.*
import com.nxdmn.xpense.data.models.ExpenseEntity
import com.nxdmn.xpense.data.relations.ExpenseWithCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Query("SELECT * FROM ExpenseEntity")
    suspend fun getAll(): List<ExpenseEntity>

    @Transaction
    @Query("SELECT * FROM ExpenseEntity")
    suspend fun getAllExpenseWithCategory(): List<ExpenseWithCategory>

    @Transaction
    @Query("SELECT * FROM ExpenseEntity")
    fun getAllExpenseWithCategoryAsFlow(): Flow<List<ExpenseWithCategory>>

    @Query("SELECT * FROM ExpenseEntity WHERE id = :id")
    suspend fun getById(id: Long): ExpenseEntity?

    @Transaction
    @Query("SELECT * FROM ExpenseEntity WHERE id = :id")
    suspend fun getExpenseWithCategoryById(id: Long): ExpenseWithCategory?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun create(expense: ExpenseEntity)

    @Update
    suspend fun update(expense: ExpenseEntity)

    @Query("DELETE FROM ExpenseEntity WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Delete
    suspend fun delete(expense: ExpenseEntity)
}