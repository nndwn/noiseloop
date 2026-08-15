package com.nndwn.whitenoise.ui.features.main.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nndwn.whitenoise.R
import com.nndwn.whitenoise.data.local.InitialAudioData
import com.nndwn.whitenoise.data.local.entity.DataAudio
import com.nndwn.whitenoise.data.local.entity.LabelAudio
import com.nndwn.whitenoise.ui.components.WaveVisAnim
import com.nndwn.whitenoise.ui.features.main.AudioFilter
import com.nndwn.whitenoise.ui.theme.Dimens
import com.nndwn.whitenoise.ui.theme.Palette
import com.nndwn.whitenoise.ui.theme.PlusJakartaFontFamily
import com.nndwn.whitenoise.utils.toTitleCase
import com.valentinilk.shimmer.shimmer

@Composable
fun ListAudio(
    isTablet : Boolean,
    selectedFilter: AudioFilter,
    colorSelected : Color,
    onFilterSelected: (AudioFilter) -> Unit,
    list : List<DataAudio>,
    activeAudioId: String?,
    isPlaying : Boolean,
    isLoading : Boolean,
    modifier: Modifier = Modifier,
    swipeColor : Color,
    spaceBottom : Dp,
    onAudioClick: (DataAudio) -> Unit,
    onSetFavorite : (DataAudio, Boolean) -> Unit
){
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isLoading){
            if (!isTablet){
                item {
                    AudioFilterBarShimmer()
                }
            }

            items(items = InitialAudioData.audioList, key = { "shimmer_${it.id}" }){ item ->
                ItemSoundShimmer(
                    item = item
                )
            }
        }else {
            if (!isTablet){
                item {
                    ListFilterBar(
                        selectedFilter = selectedFilter,
                        colorSelected = colorSelected,
                        onFilterSelected = onFilterSelected,
                    )
                }
            }

            items(items = list, key = {it.id}){ audio ->
                SwipeActionFavorite(
                    isFavorite = audio.isFavorite,
                    colorSelected = swipeColor,
                    onToggleFavorite = {
                        onSetFavorite(audio, !audio.isFavorite)
                    }
                ) { _ ->
                    ItemSound(
                        active = audio.id == activeAudioId && isPlaying,
                        item = audio
                    ) {
                        onAudioClick(audio)
                    }
                }
            }
        }
        item {
            Spacer(Modifier.height(spaceBottom))
        }
    }
}

@Composable
private fun ItemSound(
    active : Boolean,
    item : DataAudio,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
){
    Container(
        modifier = modifier
            .background(Palette.Black2)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(color = Palette.Black2),
                role = Role.Button,
                onClickLabel = "play sound ${item.name}",
                onClick = onClick
            )
    ) {

        BoxCover {
            Image(
                painter = painterResource(item.cover),
                contentDescription = "cover ${item.name}",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop

            )
        }
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            TitleText(item.name)
            SpaceArrangementHeight()
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TypeText(item.type.name)
                if (item.label == LabelAudio.ONLINE){
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        painter = painterResource(R.drawable.ic_down),
                        contentDescription = "download icon",
                        tint = Palette.Grey,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }
        }
        WaveVisAnim(active, size = 40.dp)
    }
}


@Composable
private fun ItemSoundShimmer(
    item : DataAudio,
    modifier : Modifier = Modifier
){
    Container(
        modifier = modifier
            .shimmer()
    ) {
        BoxCover(
            backgroundColor = Palette.Grey
        )

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            TitleText(
                text = item.name,
                color = Color.Transparent,
                modifier = Modifier
                    .background(
                        color = Palette.Grey,
                        shape = RoundedCornerShape(4.dp)
                    )
            )
            SpaceArrangementHeight()
            TypeText(
                text = item.type.name,
                color = Color.Transparent,
                modifier = Modifier
                    .background(
                        color = Palette.Grey,
                        shape = RoundedCornerShape(4.dp)
                    )
            )
        }
    }
}

@Composable
private fun Container(
    modifier: Modifier = Modifier,
    content :  @Composable (RowScope.() -> Unit)
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = Dimens.PaddingHorizontal),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ){
        content()
    }
}

@Composable
private fun SpaceArrangementHeight(){
    Spacer(Modifier.height(4.dp))
}


@Composable
private fun BoxCover(
    modifier: Modifier = Modifier,
    backgroundColor : Color = Palette.White,
    content:  @Composable (BoxScope.() -> Unit) ={}
){
    Box(
        modifier = modifier
            .size(50.dp)
            .clip(RoundedCornerShape(5.dp))
            .background( backgroundColor)

    ) {
        content()
    }
}


@Composable
private fun TypeText(
    text : String,
    modifier: Modifier = Modifier,
    color : Color = Palette.White.copy(alpha = 0.6f)
) {
    Text(
        fontFamily = PlusJakartaFontFamily,
        text = text.toTitleCase(),
        fontSize = 12.sp,
        fontWeight = FontWeight.Light,
        color = color,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        modifier = modifier
    )
}

@Composable
private fun TitleText(
    text: String,
    modifier: Modifier = Modifier,
    color : Color = Palette.White
){
    Text(
        fontFamily = PlusJakartaFontFamily,
        text = text.toTitleCase(),
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
        color = color,
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
        modifier = modifier
    )
}