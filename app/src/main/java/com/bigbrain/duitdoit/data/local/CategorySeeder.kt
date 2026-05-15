package com.bigbrain.duitdoit.data.local

import com.bigbrain.duitdoit.data.local.entity.CategoryEntity

object CategorySeeder {
    val defaultCategories = listOf(
        CategoryEntity(id = 1, name = "Food", icon = "ic_food", color = "#EF4444", type = "expense"),
        CategoryEntity(id = 2, name = "Transport", icon = "ic_transport", color = "#F97316", type = "expense"),
        CategoryEntity(id = 3, name = "Shopping", icon = "ic_shopping", color = "#A855F7", type = "expense"),
        CategoryEntity(id = 4, name = "Fun", icon = "ic_leisure", color = "#EC4899", type = "expense"),
        CategoryEntity(id = 5, name = "Health", icon = "ic_health", color = "#14B8A6", type = "expense"),
        CategoryEntity(id = 6, name = "Education", icon = "ic_education", color = "#3B82F6", type = "expense"),
        CategoryEntity(id = 7, name = "Bills", icon = "ic_bills", color = "#EAB308", type = "expense"),
        CategoryEntity(id = 8, name = "Other", icon = "ic_other", color = "#6B7280", type = "expense"),
        CategoryEntity(id = 9, name = "Salary", icon = "ic_salary", color = "#22C55E", type = "income"),
        CategoryEntity(id = 10, name = "Freelance", icon = "ic_freelance", color = "#10B981", type = "income"),
        CategoryEntity(id = 11, name = "Business", icon = "ic_business", color = "#3771FF", type = "income"),
        CategoryEntity(id = 12, name = "Gift", icon = "ic_gift", color = "#EC4899", type = "income"),
        CategoryEntity(id = 13, name = "Other", icon = "ic_other", color = "#6B7280", type = "income"),
    )
}