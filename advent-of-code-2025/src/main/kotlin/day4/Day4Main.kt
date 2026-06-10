package org.ee.aoc25.day4

import java.io.File
import java.nio.file.Paths

const val EMPTY_POSITION = "."
const val FORKLIFT_POSITION = "x"
const val PAPERROLL_POSITION = "@"

fun main() {
    // The marked example from the problem statement (x = a roll the forklift can reach).
    // Treating x as a roll, this must yield 13 accessible rolls.
    val exampleGrid = """
        ..xx.xx@x.
        x@@.@.@.@@
        @@@@@.x.@@
        @.@@@@..@.
        x@.@@@@.@x
        .@@@@@@@.@
        .@.@.@.@@@
        x.@@@.@@@@
        .@@@@@@@@.
        x.x.@@@.x.
    """.trimIndent()

    val accessibleInExample = countAccessibleRolls(toPaperRollsGridArray(exampleGrid))
    println("Example -> Part 1 accessible rolls = $accessibleInExample (expected 13)")

    val removableInExample = countRemovableRolls(toPaperRollsGridArray(exampleGrid))
    println("Example -> Part 2 removable rolls = $removableInExample (expected 43)")

    val absoluteCurrentPath = Paths.get("").toAbsolutePath().toString()
    val file = File("$absoluteCurrentPath/advent-of-code-2025/src/main/kotlin/day4/test_input.txt")
    if (file.exists()) {
        val puzzleGrid = file.readText()
        val grid = toPaperRollsGridArray(puzzleGrid)
        println("Puzzle -> Part 1 accessible rolls = ${countAccessibleRolls(grid)}")
        println("Puzzle -> Part 2 removable rolls  = ${countRemovableRolls(grid)}")
    } else {
        println("File not found.")
    }
}

/**
 * Counts the paper rolls a forklift can access.
 *
 * A roll is accessible when fewer than four of its eight adjacent positions hold a roll.
 *
 * Dynamic Programming approach — a 2D prefix sum (summed-area table):
 *   prefix[i][j] = number of rolls inside the sub-grid rows 0..i-1, cols 0..j-1
 *
 * The table is filled with the recurrence
 *   prefix[i+1][j+1] = roll(i,j) + prefix[i][j+1] + prefix[i+1][j] - prefix[i][j]
 * in a single O(rows * cols) sweep. Afterward the number of rolls inside any
 * rectangle (and hence each cell's 3x3 neighborhood) is an O(1) lookup, so the
 * whole scan stays linear in the number of cells — independent of the window size.
 */
fun countAccessibleRolls(grid: Array<Array<String>>): Int {
    val rows = grid.size
    if (rows == 0) return 0
    val cols = grid[0].size

    // Build the summed-area table (DP). Both '@' and the marked 'x' count as rolls.
    val prefix = Array(rows + 1) { IntArray(cols + 1) }
    for (i in 0 until rows) {
        for (j in 0 until cols) {
            val isRoll = if (grid[i][j] == PAPERROLL_POSITION || grid[i][j] == FORKLIFT_POSITION) 1 else 0
            prefix[i + 1][j + 1] = isRoll + prefix[i][j + 1] + prefix[i + 1][j] - prefix[i][j]
        }
    }

    // O(1) count of rolls in an inclusive rectangle, clamped to the grid bounds.
    fun rollsInRect(r1: Int, c1: Int, r2: Int, c2: Int): Int {
        val top = maxOf(0, r1)
        val left = maxOf(0, c1)
        val bottom = minOf(rows - 1, r2)
        val right = minOf(cols - 1, c2)
        return prefix[bottom + 1][right + 1] - prefix[top][right + 1] - prefix[bottom + 1][left] + prefix[top][left]
    }

    var accessible = 0
    for (i in 0 until rows) {
        for (j in 0 until cols) {
            if (grid[i][j] != PAPERROLL_POSITION && grid[i][j] != FORKLIFT_POSITION) continue
            // Rolls in the 3x3 window minus the cell itself = rolls in the 8 neighbors.
            val neighbouringRolls = rollsInRect(i - 1, j - 1, i + 1, j + 1) - 1
            if (neighbouringRolls < 4) accessible++
        }
    }
    return accessible
}

/**
 * Part 2 — counts how many rolls can be removed in total.
 *
 * A roll becomes accessible (removable) once fewer than four of its eight neighbours
 * are rolls. Removing it lowers its neighbours' counts, which may make them removable,
 * and so on, cascading until nothing is left accessible.
 *
 * Key insight: removing rolls only ever *decreases* neighbour counts (monotone), so a
 * roll that becomes accessible never becomes inaccessible again. The set of removable
 * rolls is therefore well-defined and order-independent. We compute it with a single
 * BFS-style "peel":
 *
 *   1. Compute each roll's live neighbour count once.
 *   2. Seed a queue with every roll already having < 4 roll-neighbours.
 *   3. Pop a roll, remove it, and decrement each present neighbour's count; whenever a
 *      neighbour's count crosses 4 -> 3 it has just become removable, so enqueue it.
 *
 * Each cell is removed at most once and each of its 8 edges is relaxed a constant number
 * of times, so the whole process is O(rows * cols) — linear in the number of cells.
 */
fun countRemovableRolls(grid: Array<Array<String>>): Int {
    val rows = grid.size
    if (rows == 0) return 0
    val cols = grid[0].size

    fun isRoll(r: Int, c: Int) =
        grid[r][c] == PAPERROLL_POSITION || grid[r][c] == FORKLIFT_POSITION

    val neighbourOffsets = arrayOf(
        -1 to -1, -1 to 0, -1 to 1,
        0 to -1, /* self */  0 to 1,
        1 to -1, 1 to 0, 1 to 1,
    )

    // present[r][c] = the roll is still on the grid (not yet removed).
    val present = Array(rows) { r -> BooleanArray(cols) { c -> isRoll(r, c) } }
    // neighbourCount[r][c] = number of present roll-neighbours around a roll.
    val neighbourCount = Array(rows) { IntArray(cols) }

    val queue = ArrayDeque<Int>() // encode (r, c) as r * cols + c

    for (r in 0 until rows) {
        for (c in 0 until cols) {
            if (!present[r][c]) continue
            var count = 0
            for ((dr, dc) in neighbourOffsets) {
                val nr = r + dr
                val nc = c + dc
                if (nr in 0 until rows && nc in 0 until cols && present[nr][nc]) count++
            }
            neighbourCount[r][c] = count
            if (count < 4) queue.addLast(r * cols + c)
        }
    }

    var removed = 0
    while (queue.isNotEmpty()) {
        val code = queue.removeFirst()
        val r = code / cols
        val c = code % cols
        if (!present[r][c]) continue // already removed via another path

        present[r][c] = false
        removed++

        for ((dr, dc) in neighbourOffsets) {
            val nr = r + dr
            val nc = c + dc
            if (nr !in 0 until rows || nc !in 0 until cols || !present[nr][nc]) continue
            // This neighbour just lost a roll-neighbour. Enqueue it the moment it
            // crosses the threshold (4 -> 3), so each cell is queued at most once here.
            if (--neighbourCount[nr][nc] == 3) queue.addLast(nr * cols + nc)
        }
    }
    return removed
}

fun toPaperRollsGridArray(paperRollsGrid: String): Array<Array<String>> = paperRollsGrid
    .trimIndent()
    .lines()
    .filter { it.isNotEmpty() }
    .also { rows ->
        val width = rows.firstOrNull()?.length ?: 0
        require(rows.all { it.length == width }) {
            "All rows in paperRollsGrid must have the same width"
        }
    }
    .map { row ->
        row.map { char ->
            when (char) {
                '.' -> EMPTY_POSITION
                'x' -> FORKLIFT_POSITION
                '@' -> PAPERROLL_POSITION
                else -> error("Unknown grid character: $char")
            }
        }.toTypedArray()
    }
    .toTypedArray()