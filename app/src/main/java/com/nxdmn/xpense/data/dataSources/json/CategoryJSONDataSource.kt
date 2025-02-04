package com.nxdmn.xpense.data.dataSources.json

import android.content.Context
import com.nxdmn.xpense.data.dataSources.CategoryDataSource
import com.nxdmn.xpense.data.models.CategoryModel
import com.nxdmn.xpense.helpers.exists
import com.nxdmn.xpense.helpers.read
import com.nxdmn.xpense.helpers.write
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

    override suspend fun findAll(): List<CategoryModel> = _categories

    override suspend fun find(id: Long): CategoryModel? = _categories.find { it.id == id }

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
        _categories.removeIf { it.id == category.id }
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
}