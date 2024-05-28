package com.nxdmn.xpense.data.models

import android.os.Parcelable
import androidx.room.*
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable

@Serializable
@Parcelize
data class ExpenseModel(var id: Long = 0,
                        var amount: Int = 0,
                        var remarks: String = "",
                        var image: String = "") : Parcelable

@Entity
data class ExpenseEntity(@PrimaryKey(autoGenerate = true) var id: Long = 0,
                         var amount: Int = 0,
                         var remarks: String = "",
                         var image: String = "")


fun ExpenseModel.asEntity() = ExpenseEntity(
    id = id,
    amount = amount,
    remarks = remarks,
    image = image,
)

fun ExpenseEntity.asModel() = ExpenseModel(
    id = id,
    amount = amount,
    remarks = remarks,
    image = image,
)