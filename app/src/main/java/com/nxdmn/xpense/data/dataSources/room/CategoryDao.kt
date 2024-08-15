package com.nxdmn.xpense.data.dataSources.room

import androidx.room.*
import com.nxdmn.xpense.data.models.CategoryEntity
import com.nxdmn.xpense.data.relations.CategoryWithExpenses

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun create(category: CategoryEntity)

    @Query("SELECT * FROM CategoryEntity")
    suspend fun findAll(): List<CategoryEntity>

    @Query("SELECT * FROM CategoryEntity WHERE id = :id")
    suspend fun findById(id: Long): CategoryEntity

    @Update
    suspend fun update(category: CategoryEntity)

    @Delete
    suspend fun delete(category: CategoryEntity)

    @Transaction
    @Query("SELECT * FROM CategoryEntity")
    suspend fun getCategoryWithExpenses(): List<CategoryWithExpenses>
}