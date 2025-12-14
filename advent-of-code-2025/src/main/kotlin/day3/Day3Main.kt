package org.ee.aoc25.day3

import java.io.File
import java.nio.file.Paths
import kotlin.math.max

fun main() {
    val resultList = mutableListOf<Int>()

    val absoluteCurrentPath = Paths.get("").toAbsolutePath().toString()
    val file = File("$absoluteCurrentPath/advent-of-code-2025/src/main/kotlin/day3/test_input.txt")
    if (file.exists()) {
        file.forEachLine { batteriesBank ->
//            computeMaxJoltage(batteriesBank, resultList)
            computeMaxJoltageOptimized(batteriesBank, resultList)
        }
    } else {
        println("File not found.")
    }

    var sum = 0L
    for (resultElement in resultList) {
        sum += resultElement
    }
    println("Sum = $sum")
}

// Naive Approach :- O(n^2) Time Complexity
fun computeMaxJoltage(batteriesBank: String, resultList: MutableList<Int>) {
    val batteriesBankSize = batteriesBank.length
    var maxJoltage = 0

    for (firstIndex in 0..(batteriesBankSize - 2)) {
        val tensPlaceDigit = batteriesBank[firstIndex] - '0'

        var maxRight = 0
        for (secondIndex in (firstIndex + 1)..(batteriesBankSize - 1)) {
            maxRight = max(maxRight, batteriesBank[secondIndex] - '0')
        }

        maxJoltage = max(maxJoltage, tensPlaceDigit * 10 + maxRight)
    }

    println("For batteriesBank: $batteriesBank, the max Joltage is $maxJoltage")
    resultList.add(maxJoltage)
}

// Greedy Approach :- O(n) Time Complexity
fun computeMaxJoltageOptimized(batteriesBank: String, resultList: MutableList<Int>) {
    val batteriesBankSize = batteriesBank.length

    // Create digits array
    val digits = Array(batteriesBankSize) { 0 }
    for (index in 0..(batteriesBankSize - 1)) {
        digits[index] = batteriesBank[index] - '0'
    }

    // Create maxRight values array
    val maxRight = Array(batteriesBankSize) { 0 }
    maxRight[batteriesBankSize - 1] = digits[batteriesBankSize - 1]
    for (index in (batteriesBankSize - 2) downTo 0) {
        maxRight[index] = max(digits[index], maxRight[index + 1])
    }

    // Compute max Joltage
    var maxJoltage = 0
    for (index in 0..(batteriesBankSize - 2)) {
        maxJoltage = max(maxJoltage, (digits[index] * 10 + maxRight[index + 1]))
    }

    println("For batteriesBank: $batteriesBank, the max Joltage is $maxJoltage")
    resultList.add(maxJoltage)
}