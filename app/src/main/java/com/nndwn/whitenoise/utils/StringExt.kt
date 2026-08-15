package com.nndwn.whitenoise.utils

fun String.toTitleCase(): String {
    return this.split(" ").joinToString(" ") { word ->
        word.lowercase().replaceFirstChar { it.uppercase() }
    }
}

fun String.toCamelCase(): String {
    return this.split(" ").mapIndexed { index, word ->
        if (index == 0) word.lowercase()
        else word.lowercase().replaceFirstChar { it.uppercase() }
    }.joinToString("")
}