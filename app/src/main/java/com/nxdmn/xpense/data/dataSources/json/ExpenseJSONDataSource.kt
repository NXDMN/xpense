package com.nxdmn.xpense.data.dataSources.json

import android.content.Context
import com.nxdmn.xpense.helpers.exists
import com.nxdmn.xpense.helpers.read
import com.nxdmn.xpense.helpers.write
import com.nxdmn.xpense.data.models.ExpenseModel
import com.nxdmn.xpense.data.dataSources.ExpenseDataSource
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Random

const val JSON_FILE = "expenses.json"

fun generateRandomId(): Long {
    return Random().nextLong()
}

class ExpenseJSONDataSource(private val context: Context) : ExpenseDataSource {
    private var _expenses = mutableListOf<ExpenseModel>()

    init {
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    override suspend fun findAll(): MutableList<ExpenseModel> {
        return _expenses
    }

    override suspend fun create(expense: ExpenseModel) {
        expense.id = generateRandomId()
        _expenses.add(expense)
        serialize()
    }

    override suspend fun update(expense: ExpenseModel) {
        val index = _expenses.indexOfFirst { it.id == expense.id }
        _expenses[index] = _expenses[index].copy(amount = expense.amount, remarks = expense.remarks, image = expense.image)
        serialize()
    }

    override suspend fun delete(expense: ExpenseModel) {
        _expenses.remove(expense)
        serialize()
    }

    private fun serialize(){
        val jsonString = Json.encodeToString(_expenses)
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize(){
        val jsonString = read(context, JSON_FILE)
        _expenses = Json.decodeFromString(jsonString)
    }

}