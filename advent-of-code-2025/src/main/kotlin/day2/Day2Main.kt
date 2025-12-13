package org.ee.aoc25.day2

import java.io.File
import java.nio.file.Paths
import java.util.regex.Pattern

fun main() {
    val resultList = mutableListOf<Long>()

    val absoluteCurrentPath = Paths.get("").toAbsolutePath().toString()
    val file = File("$absoluteCurrentPath/advent-of-code-2025/src/main/kotlin/day2/test_input.txt")
    if (file.exists()) {
        val inputRanges = file.readText().split(",")
        for (inputRange in inputRanges) {
            val inputRangeValues = inputRange.split("-").map { it.toLong() }
            if (inputRangeValues.size == 2) {
                traverseThroughRange(LongRange(inputRangeValues[0], inputRangeValues[1]), resultList)
            }
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

private fun traverseThroughRange(range: LongRange, resultList: MutableList<Long>) {
    for (number in range) {
        if (patternMatchRepeatedDigitsInNumber(number.toString())) {
            resultList.add(number)
        }
    }
}

private fun patternMatchRepeatedDigitsInNumber(numberStr: String): Boolean {
    val regex = "^(.+)\\1+$"
    val p = Pattern.compile(regex)
    val matcher = p.matcher(numberStr)
    var matchFound = false

    while (matcher.find()) {
        println(numberStr + " got repeated: " + matcher.group(1))
        matchFound = true
    }
    if (!matchFound) {
        println("$numberStr has no repetition")
    }

    return matchFound
}
