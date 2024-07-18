package com.nxdmn.xpense.data.dataSources.json

import android.content.Context
import com.nxdmn.xpense.data.dataSources.CategoryDataSource
import com.nxdmn.xpense.data.models.CategoryModel
import com.nxdmn.xpense.helpers.exists
import com.nxdmn.xpense.helpers.read
import com.nxdmn.xpense.helpers.write
import com.nxdmn.xpense.ui.CategoryIcon
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.Random

const val CATEGORIES_JSON_FILE = "categories.json"

class CategoryJSONDataSource(private val context: Context) : CategoryDataSource {
    private var _categories = mutableListOf<CategoryModel>()

    init {
        if (exists(context, CATEGORIES_JSON_FILE)) {
            deserialize()
        }
    }

    override suspend fun findAll(): List<CategoryModel> {
        if (_categories.isEmpty()) generateDefaultList()
        return _categories
    }

    override suspend fun create(category: CategoryModel) {
        category.id = Random().nextLong()
        _categories.add(category)
        serialize()
    }

    override suspend fun update(category: CategoryModel) {
        _categories.find { it.id == category.id }?.apply {
            name = category.name
            icon = category.icon
            count = category.count
        }
        serialize()
    }

    override suspend fun delete(category: CategoryModel) {
        _categories.remove(category)
        serialize()
    }

    private fun serialize() {
        val jsonString = Json.encodeToString(_categories)
        write(context, CATEGORIES_JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, CATEGORIES_JSON_FILE)
        _categories = Json.decodeFromString(jsonString)
    }

    private suspend fun generateDefaultList() {
        listOf(
            CategoryModel(name = "Food", icon = CategoryIcon.EATING),
            CategoryModel(name = "Clothes", icon = CategoryIcon.CLOTHING),
            CategoryModel(name = "Others", icon = CategoryIcon.OTHERS),
            CategoryModel(name = "Entertainment", icon = CategoryIcon.ENTERTAINMENT),
            CategoryModel(name = "Family", icon = CategoryIcon.FAMILY),
            CategoryModel(name = "Fuel", icon = CategoryIcon.FUEL),
            CategoryModel(name = "Gift", icon = CategoryIcon.GIFT),
            CategoryModel(name = "Groceries", icon = CategoryIcon.GROCERIES),
            CategoryModel(name = "Rental", icon = CategoryIcon.HOME),
            CategoryModel(name = "Medical", icon = CategoryIcon.MEDICAL),
            CategoryModel(name = "Phone bill", icon = CategoryIcon.PHONE_BILL),
            CategoryModel(name = "Shopping", icon = CategoryIcon.SHOPPING),
            CategoryModel(name = "Sports", icon = CategoryIcon.SPORTS),
            CategoryModel(name = "Trip", icon = CategoryIcon.TRAVEL),
            CategoryModel(name = "Utilities", icon = CategoryIcon.UTILITIES),
            CategoryModel(name = "Insurance", icon = CategoryIcon.LIFE),
        ).forEach { create(it) }
    }
}