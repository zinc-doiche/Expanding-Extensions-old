package com.github.zinc.lib.`object`

import com.github.zinc.lib.brief.BriefItem
import com.github.zinc.util.isFullStack
import com.github.zinc.util.isNotNull
import com.github.zinc.util.toJson
import org.bukkit.inventory.ItemStack

/**
 * 크기가 자유로운 가상 인벤토리
 */
class Container(val size: Int) {
    private var items: Array<ItemStack?> = arrayOfNulls(size)

    fun getItems(): List<ItemStack> {
        return items.filterNotNull()
    }

    operator fun get(idx: Int): ItemStack? {
        return items[idx]
    }

    fun subtract(idx: Int) {
        if (items[idx]!!.subtract().amount <= 0) {
            remove(idx)
        }
    }

    fun remove(idx: Int) {
        items[idx] = null
    }

    fun setItem(idx: Int, item: ItemStack?) {
        items[idx] = item
    }

    fun clear() {
        for (i in 0 until size) remove(i)
    }

    fun forEach(block: (ItemStack?) -> Unit) {
        items.forEach(block)
    }

    fun toList(): List<ItemStack?> {
        return items.toList()
    }

    /**
     * null 제거
     */
    fun sort() {
        val iterator = items.filter(::isNotNull).iterator()
        for (i in 0 until size) {
            if (iterator.hasNext()) {
                items[i] = iterator.next()
            } else {
                items[i] = null
            }
        }
    }

    fun serialize(): List<String> {
        return items.filter(::isNotNull)
            .map { item -> item?.toJson()!! }
            .toList()
    }

    fun brief(): List<BriefItem> {
        return items.filter(::isNotNull)
            .map { item -> BriefItem.of(item!!) }
            .toList()
    }

    /**
     * @param adds 추가할 아이템
     * @return 넘친 아이템 리스트
     */
    fun addItem(vararg adds: ItemStack?): List<ItemStack?> {
        for (i in items.indices) {
            for (k in adds.indices) {
                if (adds[k] == null) {
                    continue
                }

                //그 칸이 비어있으면 저장하고 continue

                if (items[i] == null) {
                    items[i] = adds[k]!!.clone()
                    continue
                }
                //도구거나 꽉 차있으면 break
                if (items[i]!!.isFullStack) {
                    break
                }

                //채울 게 도구거나 다른거면 continue
                if (adds[k]!!.getMaxStackSize() == 1 || !items[i]!!.isSimilar(adds[k])) {
                    continue
                }
                val remain = items[i]!!.getMaxStackSize() - items[i]!!.amount
                val exceed = adds[k]!!.amount - remain

                //공간 남으면 다 넣고 continue
                if (exceed < 0) {
                    items[i]!!.amount = items[i]!!.getMaxStackSize() + exceed
                } else {
                    items[i]!!.amount = items[i]!!.getMaxStackSize()
                    adds[k]!!.amount = exceed
                    break
                }
            }
        }
        return adds.filter(::isNotNull).toList()
    }
}