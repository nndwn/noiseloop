package com.nndwn.whitenoise.ui.features.main.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nndwn.whitenoise.R
import com.nndwn.whitenoise.data.local.entity.TypeAudio
import com.nndwn.whitenoise.ui.features.main.AudioFilter
import com.nndwn.whitenoise.ui.theme.Dimens
import com.nndwn.whitenoise.ui.theme.Palette
import com.nndwn.whitenoise.ui.theme.PlusJakartaFontFamily
import com.nndwn.whitenoise.utils.toTitleCase
import com.valentinilk.shimmer.shimmer

@Composable
fun ListFilterBar(
    selectedFilter : AudioFilter,
    colorSelected : Color,
    modifier: Modifier = Modifier,
    onFilterSelected :(AudioFilter) -> Unit
){
    Container(modifier = modifier) {
        item {
            val isAllSelected = selectedFilter is AudioFilter.All
            ItemFilterAudio(
                text = stringResource(R.string.filter_tag_all),
                isSelected = isAllSelected,
                colorSelected = colorSelected,
                onClick = { onFilterSelected(AudioFilter.All) }
            )
        }
        item {
            val isFavoriteSelected = selectedFilter is AudioFilter.Favorite
            ItemFilterAudio(
                text = stringResource(R.string.filter_tag_favorite),
                isSelected = isFavoriteSelected,
                colorSelected = colorSelected,
                onClick = { onFilterSelected(AudioFilter.Favorite) }
            )
        }
        items(TypeAudio.entries.toTypedArray()){ type ->
            val isTypeSelected = selectedFilter is AudioFilter.SelectedType && selectedFilter.type == type

            ItemFilterAudio(
                text = type.name.toTitleCase(),
                isSelected = isTypeSelected,
                colorSelected = colorSelected,
                onClick = { onFilterSelected(AudioFilter.SelectedType(type)) }
            )
        }

    }
}


@Composable
fun AudioFilterBarShimmer() {
    Container {
        items(TypeAudio.entries.toTypedArray()){
            ItemFilterAudioShimmer(text = it.name.toTitleCase())
        }
    }
}



@Composable
private fun ItemFilterAudio(
    text: String,
    isSelected : Boolean,
    modifier: Modifier = Modifier,
    colorSelected : Color = Palette.Black3,
    onClick : () -> Unit = {}
) {

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) colorSelected else Palette.Black2,
        label = "tagByColor"
    )

    val borderColor = if (isSelected) Color.Transparent else Palette.White.copy(alpha = 0.15f)

    Container(
        modifier = modifier
            .clickable(
                indication = ripple(),
                interactionSource = remember { MutableInteractionSource() },
                onClickLabel = "",
                onClick = onClick
            ),
        backgroundColor = backgroundColor,
        borderColor = borderColor
    ) {
        ContentText(
            text = text,
            color = Palette.White
        )
    }
}


@Composable
private fun ItemFilterAudioShimmer(
    text: String,
){
    Container(
        modifier = Modifier
            .shimmer(),
        backgroundColor = Palette.Grey,
        borderColor = Color.Transparent
    ) {
        ContentText(
            text = text,
            color = Color.Transparent,
            modifier = Modifier
                .background(
                    color = Palette.Grey,
                    shape = RoundedCornerShape(4.dp)
                )
        )
    }
}


@Composable
private fun ContentText(
    text : String,
    modifier: Modifier = Modifier,
    color : Color = Palette.White
){
    Text(
        fontFamily = PlusJakartaFontFamily,
        text = text,
        color = color,
        fontWeight = FontWeight.Medium,
        fontSize = 13.sp,
        maxLines = 1,
        modifier = modifier
    )
}
@Composable
private fun Container(
    modifier: Modifier,
    backgroundColor : Color,
    borderColor : Color,
    content :  @Composable (BoxScope.() -> Unit)
) {
    Box(
        modifier = modifier
            .border(
                width = 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(20.dp)
            )
            .background(color = backgroundColor, shape = RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ){
        content()
    }
}

@Composable
private fun  Container(
    modifier : Modifier = Modifier,
    content :  LazyListScope.() -> Unit
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = Dimens.PaddingHorizontal, vertical = 10.dp)
    ) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun ListFilterBarPreview() {
    Column {
        AudioFilterBarShimmer()
        ListFilterBar(
            selectedFilter = AudioFilter.All,
            colorSelected = Color.Green,
            onFilterSelected = {},
        )
    }

}