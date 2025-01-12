package com.nxdmn.xpense.data.dataSources.room

import androidx.room.*
import com.nxdmn.xpense.data.models.ExpenseEntity
import com.nxdmn.xpense.data.relations.ExpenseWithCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun create(expense: ExpenseEntity)

    @Query("SELECT * FROM ExpenseEntity")
    suspend fun findAll(): List<ExpenseEntity>

    @Query("SELECT * FROM ExpenseEntity WHERE id = :id")
    suspend fun findById(id: Long): ExpenseEntity

    @Transaction
    @Query("SELECT * FROM ExpenseEntity WHERE id = :id")
    suspend fun getExpenseWithCategoryById(id: Long): ExpenseWithCategory?

    @Update
    suspend fun update(expense: ExpenseEntity)

    @Delete
    suspend fun delete(expense: ExpenseEntity)

    @Transaction
    @Query("SELECT * FROM ExpenseEntity")
    suspend fun getExpenseWithCategory(): List<ExpenseWithCategory>

    @Transaction
    @Query("SELECT * FROM ExpenseEntity")
    fun getExpenseWithCategoryAsFlow(): Flow<List<ExpenseWithCategory>>
}