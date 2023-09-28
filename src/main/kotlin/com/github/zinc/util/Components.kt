package com.github.zinc.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import net.kyori.adventure.text.event.HoverEvent
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.NamedTextColor.GRAY
import net.kyori.adventure.text.format.TextDecoration

internal fun list(vararg components: Component): MutableList<Component> = components.toMutableList()
internal fun texts(vararg texts: String): List<Component> = texts.map(Component::text)

internal fun TextComponent.setHoverText(text: Component)
        = this.hoverEvent(HoverEvent.hoverEvent(HoverEvent.Action.SHOW_TEXT, text))

internal fun Component.content(): String = (this as? TextComponent)?.content() ?: "not a TextComponent"

internal fun warn(text: String): Component = Component.text(text, NamedTextColor.RED, TextDecoration.BOLD)
internal fun italic(text: String): Component = Component.text(text, GRAY, TextDecoration.ITALIC)