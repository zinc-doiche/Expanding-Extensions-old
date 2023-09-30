package com.github.zinc.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.NamedTextColor.GRAY
import net.kyori.adventure.text.format.TextDecoration
import java.text.DecimalFormat

internal fun plain(content: String): Component = Component.text(content)
    .color(NamedTextColor.WHITE)
    .decoration(TextDecoration.ITALIC, false)

internal fun Component.noItalic(): Component = this.decoration(TextDecoration.ITALIC, false)
internal fun Component.bold(): Component = this.decoration(TextDecoration.BOLD, true)

internal fun list(vararg components: Component): MutableList<Component> = components.toMutableList()
internal fun texts(vararg texts: String): List<Component> = texts.map(Component::text)

internal fun TextComponent.setHoverText(text: Component)
        = this.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, text))

internal fun Component.content(): String = (this as? TextComponent)?.content() ?: "not a TextComponent"

internal fun warn(text: String): Component = Component.text(text, NamedTextColor.RED, TextDecoration.BOLD)
internal fun italic(text: String): Component = Component.text(text, GRAY, TextDecoration.ITALIC)

internal fun Double.format(format: String): String = DecimalFormat(format).format(this)