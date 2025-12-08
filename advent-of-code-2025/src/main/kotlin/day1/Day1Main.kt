package org.ee.aoc25.day1

import java.io.File
import java.nio.file.Paths
import org.ee.aoc25.day1.model.Dial

fun main() {
    val dial = Dial(50, 100)

    val absoluteCurrentPath = Paths.get("").toAbsolutePath().toString()
    val file = File("$absoluteCurrentPath/advent-of-code-2025/src/main/kotlin/day1/test_input.txt")
    if (file.exists()) {
        file.forEachLine { rotationsString ->
            dial.rotate(rotationsString)
        }
    } else {
        println("File not found.")
    }

    println("====================")
    println("Answer is :- ${dial.countOfZeroPosition}")
}