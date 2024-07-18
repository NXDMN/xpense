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

const val EXPENSES_JSON_FILE = "expenses.json"

class ExpenseJSONDataSource(private val context: Context) : ExpenseDataSource {
    private var _expenses = mutableListOf<ExpenseModel>()

    init {
        if (exists(context, EXPENSES_JSON_FILE)) {
            deserialize()
        }
    }

    override suspend fun findAll(): List<ExpenseModel> = _expenses

    override suspend fun create(expense: ExpenseModel) {
        expense.id = Random().nextLong()
        _expenses.add(expense)
        serialize()
    }

    override suspend fun update(expense: ExpenseModel) {
        _expenses.find { it.id == expense.id }?.apply {
            amount = expense.amount
            date = expense.date
            category = expense.category
            remarks = expense.remarks
            image = expense.image
        }
        serialize()
    }

    override suspend fun delete(expense: ExpenseModel) {
        _expenses.remove(expense)
        serialize()
    }

    private fun serialize(){
        val jsonString = Json.encodeToString(_expenses)
        write(context, EXPENSES_JSON_FILE, jsonString)
    }

    private fun deserialize(){
        val jsonString = read(context, EXPENSES_JSON_FILE)
        _expenses = Json.decodeFromString(jsonString)
    }
}