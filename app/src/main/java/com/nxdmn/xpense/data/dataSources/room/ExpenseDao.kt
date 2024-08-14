package com.nxdmn.xpense.data.dataSources.room

import androidx.room.*
import com.nxdmn.xpense.data.models.ExpenseEntity
import com.nxdmn.xpense.data.relations.ExpenseWithCategory

@Dao
interface ExpenseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun create(expense: ExpenseEntity)

    @Query("SELECT * FROM ExpenseEntity")
    suspend fun findAll(): List<ExpenseEntity>

    @Query("SELECT * FROM ExpenseEntity WHERE id = :id")
    suspend fun findById(id: Long): ExpenseEntity

    @Update
    suspend fun update(expense: ExpenseEntity)

    @Delete
    suspend fun delete(expense: ExpenseEntity)

    @Transaction
    @Query("SELECT * FROM ExpenseEntity")
    fun getExpenseWithCategory(): List<ExpenseWithCategory>
}