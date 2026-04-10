package com.example.hmassignment.presentation.compose

import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.hmassignment.presentation.ProductUiModel
import com.example.hmassignment.presentation.SwatchDisplayInfo

@Composable
internal fun ProductListItem(
    uiModel: ProductUiModel,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        AsyncImage(
            model = uiModel.imageUrl,
            contentDescription = uiModel.imageAltText,
            modifier = Modifier
                .aspectRatio(2f / 3f)
                .fillMaxWidth()
                .testTag(ProductListItemTestTags.PRODUCT_IMAGE),
        )
        ProductInfo(
            uiModel = uiModel,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .padding(bottom = 16.dp),
        )
    }
}

@Composable
private fun ProductInfo(
    uiModel: ProductUiModel,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = uiModel.name,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.testTag(ProductListItemTestTags.PRODUCT_NAME),
        )

        uiModel.formattedPrice?.let { price ->
            Text(
                text = price,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.testTag(ProductListItemTestTags.PRODUCT_PRICE),
            )
        }

        SwatchRow(swatchDisplayInfo = uiModel.swatchDisplayInfo)
    }
}

@Composable
private fun SwatchRow(swatchDisplayInfo: SwatchDisplayInfo) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        swatchDisplayInfo.colors.forEach { color ->
            SwatchDot(color = color)
        }

        swatchDisplayInfo.overflowText?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.testTag(ProductListItemTestTags.SWATCH_OVERFLOW_TEXT),
            )
        }
    }
}

@Composable
private fun SwatchDot(color: Color) {
    Box(
        modifier = Modifier
            .size(12.dp)
            .padding(1.dp)
            .border(1.dp, Color.LightGray)
            .background(color)
            .testTag(ProductListItemTestTags.SWATCH_DOT)
            .semantics { contentDescription = "" },
    )
}

@VisibleForTesting
internal object ProductListItemTestTags {
    const val PRODUCT_IMAGE = "product_image"
    const val PRODUCT_NAME = "product_name"
    const val PRODUCT_PRICE = "product_price"
    const val SWATCH_DOT = "swatch_dot"
    const val SWATCH_OVERFLOW_TEXT = "swatch_overflow_text"
}
