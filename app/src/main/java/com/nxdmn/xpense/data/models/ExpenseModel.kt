package com.nxdmn.xpense.data.models

import android.os.Parcelable
import androidx.room.*
import com.nxdmn.xpense.helpers.LocalDateConverters
import com.nxdmn.xpense.helpers.LocalDateSerializer
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.time.LocalDate

@Serializable
@Parcelize
data class ExpenseModel(var id: Long = 0,
                        var amount: Int = 0,
                        @Serializable(with = LocalDateSerializer::class)
                        var date: LocalDate = LocalDate.now(),
                        var category: String = "",
                        var remarks: String = "",
                        var image: String = "") : Parcelable

@TypeConverters(LocalDateConverters::class)
@Entity
data class ExpenseEntity(@PrimaryKey(autoGenerate = true) var id: Long = 0,
                         var amount: Int = 0,
                         var date: LocalDate = LocalDate.now(),
                         var category: String = "",
                         var remarks: String = "",
                         var image: String = "")


fun ExpenseModel.asEntity() = ExpenseEntity(
    id = id,
    amount = amount,
    date = date,
    category = category,
    remarks = remarks,
    image = image,
)

fun ExpenseEntity.asModel() = ExpenseModel(
    id = id,
    amount = amount,
    date = date,
    category = category,
    remarks = remarks,
    image = image,
)