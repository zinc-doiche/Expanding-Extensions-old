package com.github.zinc.util.component

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration

internal fun text(text: String, style: Style = Style.style(TextDecoration.STRIKETHROUGH)): TextComponent = Component.text(text, style)
internal fun texts(vararg components: Component): List<Component> = components.toList()
internal fun texts(vararg texts: String, style: Style = Style.style(TextDecoration.STRIKETHROUGH)): List<TextComponent> = texts.map { text(it, style) }
