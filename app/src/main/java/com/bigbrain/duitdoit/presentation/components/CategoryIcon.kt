package com.bigbrain.duitdoit.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.bigbrain.duitdoit.R
import com.bigbrain.duitdoit.ui.theme.*

fun getCategoryIcon(categoryName: String): Int {
    return when (categoryName) {
        "Food" -> R.drawable.ic_food
        "Transport" -> R.drawable.ic_transport
        "Shopping" -> R.drawable.ic_shopping
        "Fun" -> R.drawable.ic_leisure
        "Health" -> R.drawable.ic_health
        "Education" -> R.drawable.ic_education
        "Bills" -> R.drawable.ic_bills
        "Salary" -> R.drawable.ic_salary
        "Freelance" -> R.drawable.ic_freelance
        "Business" -> R.drawable.ic_business
        "Gift" -> R.drawable.ic_gift
        else -> R.drawable.ic_other
    }
}

fun getCategoryColor(categoryName: String): Color {
    return when (categoryName) {
        "Food" -> CategoryFoodDrinks
        "Transport" -> CategoryTransport
        "Shopping" -> CategoryShopping
        "Fun" -> CategoryFun
        "Health" -> CategoryHealth
        "Education" -> CategoryEducation
        "Bills" -> CategoryBills
        "Salary" -> CategorySalary
        "Freelance" -> CategoryFreelance
        "Business" -> CategoryBusiness
        "Gift" -> CategoryGift
        else -> CategoryOther
    }
}

@Composable
fun CategoryIconBox(
    categoryName: String,
    size: Dp = 40.dp,
    iconSize: Dp = 20.dp
) {
    val color = getCategoryColor(categoryName)
    val icon = getCategoryIcon(categoryName)

    Box(
        modifier = Modifier
            .size(size)
            .background(color, RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = categoryName,
            tint = Color.White,
            modifier = Modifier.size(iconSize)
        )
    }
}