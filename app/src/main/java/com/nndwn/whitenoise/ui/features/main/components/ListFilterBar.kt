package com.nndwn.whitenoise.ui.features.main.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.res.stringResource
import com.nndwn.whitenoise.R
import com.nndwn.whitenoise.data.local.entity.TypeAudio
import com.nndwn.whitenoise.ui.LocalActiveAudio
import com.nndwn.whitenoise.ui.extentions.shimmer
import com.nndwn.whitenoise.ui.features.main.AudioFilter
import com.nndwn.whitenoise.ui.features.main.MainUiState
import com.nndwn.whitenoise.ui.theme.dimens
import com.nndwn.whitenoise.ui.theme.toComposeColor
import com.nndwn.whitenoise.utils.toTitleCase

@Composable
fun ListFilterBar(
    shimmerProgress: Float?,
    stateUI : MainUiState,
    selectedFilter : AudioFilter,
    modifier: Modifier = Modifier,
    colorAudioActive : Color,
    onFilterSelected :(AudioFilter) -> Unit
){
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(MaterialTheme.dimens.small),
        contentPadding = PaddingValues(
            horizontal = MaterialTheme.dimens.medium,
            vertical = MaterialTheme.dimens.small)
    ) {

        when(stateUI){
            is MainUiState.Loading -> {
                items(TypeAudio.entries.toTypedArray()){ typeAudio ->
                    Text(
                        text = typeAudio.name.toTitleCase(),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Transparent,
                        modifier = Modifier
                            .shimmer(
                                progress = shimmerProgress,
                                shape = CircleShape
                            )
                            .padding(
                                horizontal = MaterialTheme.dimens.medium,
                                vertical = MaterialTheme.dimens.small
                            )
                    )
                }
            }
            is MainUiState.Success -> {
                item {
                    val isAllSelected = selectedFilter is AudioFilter.All
                    ItemFilterAudio(
                        text = stringResource(R.string.filter_tag_all),
                        isSelected = isAllSelected,
                        colorSelected = colorAudioActive,
                        onClick = { onFilterSelected(AudioFilter.All) }
                    )
                }
                item {
                    val isFavoriteSelected = selectedFilter is AudioFilter.Favorite
                    ItemFilterAudio(
                        text = stringResource(R.string.filter_tag_favorite),
                        isSelected = isFavoriteSelected,
                        colorSelected = colorAudioActive,
                        onClick = { onFilterSelected(AudioFilter.Favorite) }
                    )
                }
                items(TypeAudio.entries.toTypedArray()){ type ->
                    val isTypeSelected = selectedFilter is AudioFilter.SelectedType && selectedFilter.type == type

                    ItemFilterAudio(
                        text = type.name.toTitleCase(),
                        isSelected = isTypeSelected,
                        colorSelected = colorAudioActive,
                        onClick = { onFilterSelected(AudioFilter.SelectedType(type)) }
                    )
                }
            }
        }

    }
}

@Composable
private fun ItemFilterAudio(
    text: String,
    isSelected : Boolean,
    modifier: Modifier = Modifier,
    colorSelected : Color = Color.Unspecified,
    onClick : () -> Unit = {}
) {
    val color = colorSelected.takeOrElse { MaterialTheme.colorScheme.background}

    val backgroundColor by animateColorAsState(
        targetValue = if (isSelected) colorSelected else color,
        label = "tagByColor"
    )

    val borderColor = if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outline

    ContainerBox(
        modifier = modifier
            .clickable(
                indication = ripple(),
                interactionSource = remember { MutableInteractionSource() },
                onClick = onClick
            ),
        backgroundColor = backgroundColor,
        borderColor = borderColor
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium
        )
    }
}


@Composable
private fun ContainerBox(
    modifier: Modifier,
    backgroundColor : Color = Color.Unspecified,
    borderColor : Color = Color.Unspecified,
    content :  @Composable () -> Unit
) {
    val fill = backgroundColor.takeOrElse { MaterialTheme.colorScheme.surface }
    val border = borderColor.takeOrElse { MaterialTheme.colorScheme.surface }
    Surface(
        color = fill,
        shape = CircleShape,
        border = BorderStroke(
            width = MaterialTheme.dimens.borderMedium,
            color = border,
        ),
        modifier = modifier
            .padding(
                horizontal = MaterialTheme.dimens.medium,
                vertical = MaterialTheme.dimens.small),
    ){
        content()
    }
}
