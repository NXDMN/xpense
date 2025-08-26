package com.nxdmn.xpense.data.dataSources.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.nxdmn.xpense.data.models.CategoryEntity
import com.nxdmn.xpense.data.models.ExpenseEntity
import kotlin.concurrent.Volatile

@Database(entities = [ExpenseEntity::class, CategoryEntity::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var Instance: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase = Instance ?: synchronized(this) {
            Room.databaseBuilder(context, AppDatabase::class.java, "app_database")
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration(false)
                .build()
                .also { Instance = it }
        }
    }
}

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE ExpenseEntity RENAME COLUMN image TO images")
    }
}