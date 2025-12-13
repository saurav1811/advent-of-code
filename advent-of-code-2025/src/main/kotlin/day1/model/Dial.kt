package org.ee.aoc25.day1.model

data class Dial(
    var currentPosition: Int = 0,
    val size: Int
) {

    companion object {
        const val LEFT: String = "L"
        const val RIGHT: String = "R"
    }

    var countOfZeroPosition: Int = 0

    fun rotate(rotationsString: String) {
        try {
            val direction = rotationsString.substring(0, 1)
            val rotations: Int = rotationsString.substring(1).toInt()

            val delta = when(direction) {
                LEFT -> -1
                RIGHT -> 1
                else -> 0
            }

            print("$rotationsString :- Move from $currentPosition --> ")
            for (rotation in 1..rotations) {
                currentPosition = (currentPosition + delta + size) % size
                if (currentPosition == 0) {
                    countOfZeroPosition++
                }
            }
            println(currentPosition)
        } catch (exception: Exception) {
            println(exception.message)
        }
    }
}