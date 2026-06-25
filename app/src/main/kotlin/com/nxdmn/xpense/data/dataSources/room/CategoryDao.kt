package com.nxdmn.xpense.data.dataSources.room

import androidx.room.*
import com.nxdmn.xpense.data.models.CategoryEntity
import com.nxdmn.xpense.data.relations.CategoryWithExpenses
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM CategoryEntity")
    suspend fun getAll(): List<CategoryEntity>

    @Transaction
    @Query("SELECT * FROM CategoryEntity")
    fun getAllAsFlow(): Flow<List<CategoryEntity>>

    @Transaction
    @Query("SELECT * FROM CategoryEntity")
    suspend fun getAllCategoryWithExpenses(): List<CategoryWithExpenses>

    @Query("SELECT * FROM CategoryEntity WHERE id = :id")
    suspend fun getById(id: Long): CategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun create(category: CategoryEntity): Long

    @Update
    suspend fun update(category: CategoryEntity)

    @Query("DELETE FROM CategoryEntity WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Delete
    suspend fun delete(category: CategoryEntity)
}