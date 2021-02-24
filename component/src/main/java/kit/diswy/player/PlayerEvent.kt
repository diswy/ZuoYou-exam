package kit.diswy.player

/**
 * 点击事件传递
 */
interface PlayerEvent {
    fun isFull(isFull: Boolean)

    fun playerBack()
}