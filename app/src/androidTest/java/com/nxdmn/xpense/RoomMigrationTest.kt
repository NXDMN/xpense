package com.nxdmn.xpense

import androidx.room.Room
import androidx.room.testing.MigrationTestHelper
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.nxdmn.xpense.data.dataSources.room.AppDatabase
import com.nxdmn.xpense.data.dataSources.room.MIGRATION_1_2
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class RoomMigrationTest {
    private val testDb = "migration-test"

    private val allMigrations = arrayOf(
        MIGRATION_1_2
    )

    @get:Rule
    val helper: MigrationTestHelper = MigrationTestHelper(
        InstrumentationRegistry.getInstrumentation(),
        AppDatabase::class.java
    )

    @Test
    @Throws(IOException::class)
    fun migrateAll() {
        // Create the earliest version of the database.
        helper.createDatabase(testDb, 1).apply {
            close()
        }

        // Open latest version of the database. Room validates the schema
        // once all migrations execute.
        Room.databaseBuilder(
            InstrumentationRegistry.getInstrumentation().targetContext,
            AppDatabase::class.java,
            testDb
        ).addMigrations(*allMigrations).build().apply {
            openHelper.writableDatabase.close()
        }
    }

    @Test
    @Throws(IOException::class)
    fun migrate1To2() {
        helper.createDatabase(testDb, 1).apply {
            // Database has schema version 1. Insert some data using SQL queries.
            // You can't use DAO classes because they expect the latest schema.
            execSQL(
                """
                    INSERT INTO ExpenseEntity (id, amount, date, categoryId, remarks, image) 
                    VALUES (1, 10, '2025-08-31', 1, 'This is remarks', 'image1')
                    """.trimIndent()
            )

            // Prepare for the next version.
            close()
        }

        // Re-open the database with version 2 and provide
        // MIGRATION_1_2 as the migration process.
        val db = helper.runMigrationsAndValidate(testDb, 2, true, MIGRATION_1_2)

        // MigrationTestHelper automatically verifies the schema changes,
        // but you need to validate that the data was migrated properly.
        val cursor = db.query("SELECT * FROM ExpenseEntity WHERE images = '[\"image1\"]'")
        assert(cursor.count == 1)
        cursor.moveToFirst()
        assert(cursor.getString(4) == "This is remarks")
        assert(cursor.getString(5) == "[\"image1\"]")
        cursor.close()

        db.close()
    }

}