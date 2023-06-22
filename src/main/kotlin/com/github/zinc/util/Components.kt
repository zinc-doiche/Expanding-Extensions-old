package com.github.zinc.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.Style
import net.kyori.adventure.text.format.TextDecoration

internal fun text(
    text: String,
    style: Style = Style.empty()
): TextComponent {
    return Component.text(text, style).decoration(TextDecoration.ITALIC, false)
}
internal fun texts(vararg components: Component): MutableList<Component> = components.toMutableList()
internal fun texts(vararg texts: String, style: Style = Style.empty()): List<TextComponent> = texts.map { text(it, style) }

internal fun TextComponent.setHoverText(text: Component)
        = this.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, text))

internal fun Component.content(): String = Component.empty().append(this).content()