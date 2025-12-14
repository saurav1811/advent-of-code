package org.ee.aoc25.day3

import java.io.File
import java.nio.file.Paths
import kotlin.math.max


fun main() {
    val resultList = mutableListOf<Long>()

    val absoluteCurrentPath = Paths.get("").toAbsolutePath().toString()
    val file = File("$absoluteCurrentPath/advent-of-code-2025/src/main/kotlin/day3/test_input.txt")
    if (file.exists()) {
        file.forEachLine { batteriesBank ->
//            computeMaxJoltageForTwoDigits(batteriesBank, resultList)
//            computeMaxJoltageOptimizedForTwoDigits(batteriesBank, resultList)
            computeMaxJoltageForGivenDigits(batteriesBank, 12, resultList)
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

// Naive Approach (2 digits) :- O(n^2) Time Complexity
fun computeMaxJoltageForTwoDigits(batteriesBank: String, resultList: MutableList<Long>) {
    val batteriesBankSize = batteriesBank.length
    var maxJoltage = 0L

    for (firstIndex in 0..(batteriesBankSize - 2)) {
        val tensPlaceDigit = batteriesBank[firstIndex] - '0'

        var maxRight = 0
        for (secondIndex in (firstIndex + 1)..(batteriesBankSize - 1)) {
            maxRight = max(maxRight, batteriesBank[secondIndex] - '0')
        }

        maxJoltage = max(maxJoltage, tensPlaceDigit * 10L + maxRight)
    }

    println("For batteriesBank: $batteriesBank, the max Joltage is $maxJoltage")
    resultList.add(maxJoltage)
}

// Greedy Approach (2 digits) :- O(n) Time Complexity
fun computeMaxJoltageOptimizedForTwoDigits(batteriesBank: String, resultList: MutableList<Long>) {
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
    var maxJoltage = 0L
    for (index in 0..(batteriesBankSize - 2)) {
        maxJoltage = max(maxJoltage, (digits[index] * 10L + maxRight[index + 1]))
    }

    println("For batteriesBank: $batteriesBank, the max Joltage is $maxJoltage")
    resultList.add(maxJoltage)
}


// Optimized Approach (for any given preserve digits) :- O(n^2) Time Complexity
fun computeMaxJoltageForGivenDigits(batteriesBank: String, preserveDigits: Int, resultList: MutableList<Long>) {
    val batteriesBankSize = batteriesBank.length
    var toRemove = batteriesBankSize - preserveDigits

    val stack = ArrayDeque<Char>()

    for (digit: Char in batteriesBank.toCharArray()) {
            while (!stack.isEmpty()
                    && toRemove > 0
                    && stack.last() < digit) {
                stack.removeLast()
                toRemove--
            }
            stack.addLast(digit)
    }

    // If removals still left, remove from the end
    while (toRemove-- > 0) {
        stack.removeLast()
    }

    // Build result of exactly 12 digits
    val maxJoltageString = StringBuilder(12)
    for (i in 0..11) {
        maxJoltageString.append(stack.removeFirst())
    }
    val maxJoltage = maxJoltageString.toString().toLong()

    println("For batteriesBank: $batteriesBank, the max Joltage is $maxJoltage")
    resultList.add(maxJoltage)
}