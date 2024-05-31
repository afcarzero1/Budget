package com.example.budgetapplication.data.categories

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

enum class CategoryType {
    Income, Expense
}

@Entity(
    tableName = "categories",
    foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = arrayOf("id"),
        childColumns = arrayOf("parentCategoryId"),
        onDelete = ForeignKey.RESTRICT
    )]
)
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val defaultType: CategoryType,
    val parentCategoryId: Int?,
    val iconResId: String? = null,
)


