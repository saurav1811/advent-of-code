package org.ee.aoc25.day2

import java.io.File
import java.nio.file.Paths
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

fun main() {
    val resultList = mutableListOf<Long>()
//    traverseThroughRange(IntRange(11, 22), resultList)
//    traverseThroughRange(IntRange(95, 115), resultList)
//    traverseThroughRange(IntRange(998, 1012), resultList)
//    traverseThroughRange(IntRange(1188511880, 1188511890), resultList)
//    traverseThroughRange(IntRange(222220, 222224), resultList)
//    traverseThroughRange(IntRange(1698522, 1698528), resultList)
//    traverseThroughRange(IntRange(446443, 446449), resultList)
//    traverseThroughRange(IntRange(38593856, 38593862), resultList)
//    traverseThroughRange(IntRange(565653, 565659), resultList)
//    traverseThroughRange(IntRange(824824821, 824824827), resultList)
//    traverseThroughRange(IntRange(2121212118, 2121212124), resultList)

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
        // Get number of digits in the number
        val numOfDigits = digitsInNumber(number)

        // Split the number into two equal halves
        val (temp1, temp2) = splitNumberToHalves(number, numOfDigits)

        if (temp1 == temp2) {
            resultList.add(number)
        }
    }
}

fun digitsInNumber(number: Long): Int {
    return if (number == 0L) 1 else {
        floor(log10(abs(number).toDouble())).toInt() + 1
    }
}

private fun splitNumberToHalves(number: Long, numOfDigits: Int): Pair<Long, Long> {
    var temp1 = number
    var temp2 = 0L
    for (power in 0..numOfDigits / 2 - 1) {
        val unitDigit = temp1 % 10
        temp2 += unitDigit * 10.0.pow(power.toDouble()).toInt()
        temp1 /= 10
    }
    return Pair(temp1, temp2)
}
