package com.nndwn.whitenoise.data.local

import com.nndwn.whitenoise.R
import com.nndwn.whitenoise.data.local.entity.DataAudio
import com.nndwn.whitenoise.data.local.entity.LabelAudio
import com.nndwn.whitenoise.data.local.entity.TypeAudio

object InitialAudioData {

    private const val LINK = "https://nndwn-my-id.vercel.app/"
    val audioList = listOf(
        //DataAudio("a1","Blue wind", TypeAudio.NATURE, "audio/a1_blue_wind.ogg", R.drawable.a1_blue_wind, Label.OFFLINE),
        DataAudio(
            "a2",
            "Waves on the beach",
            TypeAudio.NATURE,
            "audio/a2_waves_on_the_beach.ogg",
            R.drawable.a2_waves_on_the_beach,
            LabelAudio.OFFLINE
        ),
        DataAudio(
            "a3",
            "Crickets in silence",
            TypeAudio.ANIMAL,
            "audio/a3_crickets_in_silence.ogg",
            R.drawable.a3_crickets_in_silence,
            LabelAudio.OFFLINE
        ),
        DataAudio(
            "a4",
            "Roar of the wind",
            TypeAudio.NATURE,
            "audio/a4_roar_of_the_wind.ogg",
            R.drawable.a4_roar_of_the_wind,
            LabelAudio.OFFLINE
        ),
        DataAudio(
            "a5",
            "Peaceful Rain",
            TypeAudio.NATURE,
            "audio/a5_peaceful_rain.ogg",
            R.drawable.a5_peaceful_rain,
            LabelAudio.OFFLINE
        ),
        DataAudio(
            "a6",
            "Clock every second",
            TypeAudio.OBJECT,
            "audio/a6_clock_every_second.ogg",
            R.drawable.a6_clock_every_second,
            LabelAudio.OFFLINE
        ),
        DataAudio(
            "a7",
            "Underwater",
            TypeAudio.PlACE,
            "audio/a7_underwater.ogg",
            R.drawable.a7_underwater,
            LabelAudio.OFFLINE
        ),
        DataAudio(
            "a8",
            "Deep Forest",
            TypeAudio.PlACE,
            "audio/a8_deep_forest.ogg",
            R.drawable.a8_deep_forest,
            LabelAudio.OFFLINE
        ),
        DataAudio(
            "a9",
            "Soft rain on window",
            TypeAudio.NATURE,
            "audio/a9_soft_rain_on_window.ogg",
            R.drawable.a9_soft_rain_on_window,
            LabelAudio.OFFLINE
        ),
        DataAudio(
            "a10",
            "River Flow",
            TypeAudio.NATURE,
            "audio/a10_river_flow.ogg",
            R.drawable.a10_river_flow,
            LabelAudio.OFFLINE
        ),
        DataAudio(
            "a11",
            "Pensive Campfire",
            TypeAudio.PlACE,
            "${LINK}a11_pensive_campfire.mp3",
            R.drawable.a11_pensive_campfire,
            LabelAudio.ONLINE
        ),
        DataAudio(
            "a12",
            "Heavy Rain Outside",
            TypeAudio.NATURE,
            "${LINK}a12_heavy_rain_outside.mp3",
            R.drawable.a12_heavy_rain_outside,
            LabelAudio.ONLINE
        ),
        DataAudio(
            "a13",
            "Bird chirping",
            TypeAudio.ANIMAL,
            "${LINK}a13_birds_chirping.mp3",
            R.drawable.a13_birds_chirping,
            LabelAudio.ONLINE
        ),
        DataAudio(
            "a14",
            "Rain on umbrella",
            TypeAudio.NATURE,
            "${LINK}a14_rain_on_umbrella.mp3",
            R.drawable.a14_rain_on_umbrella,
            LabelAudio.ONLINE
        ),
        DataAudio(
            "a15",
            "Beautiful Waterfall",
            TypeAudio.NATURE,
            "${LINK}a15_beautiful_waterfall.mp3",
            R.drawable.a15_beautiful_waterfall,
            LabelAudio.ONLINE
        ),
        DataAudio(
            "a16",
            "Evening in the Village",
            TypeAudio.PlACE,
            "${LINK}a16_evening_in_the_village.mp3",
            R.drawable.a16_evening_in_the_village,
            LabelAudio.ONLINE
        ),
        DataAudio(
            "a17",
            "Waiting for laundry",
            TypeAudio.OBJECT,
            "${LINK}a17_waiting_for_laundry.mp3",
            R.drawable.a17_waiting_for_laundry,
            LabelAudio.ONLINE
        ),
        DataAudio(
            "a18",
            "The storm passed",
            TypeAudio.NATURE,
            "${LINK}a18_the_storm_passed.mp3",
            R.drawable.a18_the_storm_passed,
            LabelAudio.ONLINE
        ),
        DataAudio(
            "a19",
            "Inside a moving train",
            TypeAudio.PlACE,
            "${LINK}a19_inside_a_moving_train.mp3",
            R.drawable.a19_inside_a_moving_train,
            LabelAudio.ONLINE
        ),
        DataAudio(
            "a20",
            "Seagulls on the beach",
            TypeAudio.ANIMAL,
            "${LINK}a20_seagulls_on_the_beach.mp3",
            R.drawable.a20_seagulls_on_the_beach,
            LabelAudio.ONLINE
        )
    )
}