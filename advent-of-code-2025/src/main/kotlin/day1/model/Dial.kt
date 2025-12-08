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
            currentPosition = when (direction) {
                LEFT -> {
                    print("$rotationsString :- ")
                    val leftwardPosition = rotateLeftwards(rotations)
                    println("Move from $currentPosition --> $leftwardPosition")
                    leftwardPosition
                }
                RIGHT -> {
                    print("$rotationsString :- ")
                    val rightwardPosition = rotateRightwards(rotations)
                    println("Move from $currentPosition --> $rightwardPosition")
                    rightwardPosition
                }
                else -> {
                    currentPosition
                }
            }
        } catch (exception: Exception) {
            println(exception.message)
        }
    }

    // Rotate positions leftwards by number mentioned after 'L'
    private fun rotateLeftwards(rotations: Int): Int {
        var leftwardPosition = (currentPosition - rotations)

        while (leftwardPosition < 0) {
            leftwardPosition += size
        }

        if (leftwardPosition == 0) {
            countOfZeroPosition++
        }

        return leftwardPosition
    }

    // Rotate positions rightwards by number mentioned after 'R'
    private fun rotateRightwards(rotations: Int): Int {
        var rightwardPosition = (currentPosition + rotations)

        if (rightwardPosition >= size) {
            rightwardPosition %= size
        }

        if (rightwardPosition == 0) {
            countOfZeroPosition++
        }

        return rightwardPosition
    }
}