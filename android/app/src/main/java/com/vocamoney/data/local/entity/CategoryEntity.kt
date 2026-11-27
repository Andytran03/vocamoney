package com.vocamoney.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.vocamoney.domain.model.Category
import com.vocamoney.domain.model.CategoryType

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "parent_id") val parentId: String?,
    @ColumnInfo(name = "group_name_vi") val groupNameVi: String,
    @ColumnInfo(name = "name_vi") val nameVi: String,
    val type: CategoryType,
    @ColumnInfo(name = "is_system") val isSystem: Boolean,
    @ColumnInfo(name = "sort_order") val sortOrder: Int,
)

fun Category.toEntity() = CategoryEntity(
    id = id,
    parentId = null,
    groupNameVi = parentNameVi,
    nameVi = nameVi,
    type = type,
    isSystem = isSystem,
    sortOrder = sortOrder,
)
