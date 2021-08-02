package exceptions

data class GameException(override val message: String):Exception(message) {
}