package kit.diswy.player

interface MyPlayerStatusObserver {
    fun onPlayerStart()
    fun onPlayerPause()
    fun onPlayerNetBad()
    fun onPlayerCompletion()
}