package com.github.zinc.util.extension

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration

internal fun text(
    text: String,
    style: Style = Style.empty(),
    decoration: TextDecoration = TextDecoration.ITALIC
): TextComponent {
    return Component.text(text, style).decoration(decoration, false)
}
internal fun texts(vararg components: Component): MutableList<Component> = components.toMutableList()
internal fun texts(vararg texts: String, style: Style = Style.empty()): List<TextComponent> = texts.map { text(it, style) }
