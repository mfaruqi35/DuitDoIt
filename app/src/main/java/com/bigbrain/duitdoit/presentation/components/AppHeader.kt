package com.bigbrain.duitdoit.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.bigbrain.duitdoit.R
import com.bigbrain.duitdoit.ui.theme.Poppins
import com.bigbrain.duitdoit.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppHeader(
    title: String = "",
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (ColumnScope.() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Primary)

    ) {
        Canvas(modifier = Modifier
            .matchParentSize()
        ) {
            drawCircle(
                color = Color.White.copy(alpha = 0.08f),
                radius = size.width * 0.4f,
                center = Offset(x = size.width * 0.085f, y = size.height * 0.3f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.06f),
                radius = size.width * 0.35f,
                center = Offset(x = size.width * 0.85f, y = size.height * 1.2f)
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = size.width * 0.2f,
                center = Offset(x = size.width * 0.1f, y = size.height * 0.3f)
            )
        }

        TopAppBar(
            modifier = Modifier.statusBarsPadding(),
            title = {
                Text(
                    text = title,
                    fontFamily = Poppins,
                    color = Color.White
                )
            },
            navigationIcon = {
                if (showBackButton) {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            },
            actions = actions,
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent,
                titleContentColor = Color.White,
                navigationIconContentColor = Color.White,
                actionIconContentColor = Color.White
            )
        )
        if (content != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                content = content
            )
        }
    }
}