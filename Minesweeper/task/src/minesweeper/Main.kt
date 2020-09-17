package minesweeper

import java.util.*
import kotlin.random.Random

fun main() {
    var isRun = true
    val scanner = Scanner(System.`in`)
    val bField = BField()
    println("How many mines do you want on the field? ")
    val numMines = scanner.nextInt()
    bField.numMines = numMines
    bField.printBF()
    while(isRun) {
        if(bField.playerMove() && !bField.gameOver) bField.printBF()
        if (bField.gameOver) isRun = false
        if(bField.isWin()) isRun = false
        if(bField.noMove()) isRun = false
    }
    if(bField.isWin()) println("Congratulations! You found all the mines!")
    else if(bField.noMove()) println("Congratulations! You found all the mines!")
}

class BField() {
    var gameOver = false
    var numMines = 0
    val scanner = Scanner(System.`in`)
    var firstMove = true
    val bf = charArrayOf(
                '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.',
                '.', '.', '.', '.', '.', '.', '.', '.', '.')

    val bfPlayer = bf.copyOf()

    fun playerMove(): Boolean {
        print("Set/unset mines marks or claim a cell as free: ")
        val x = scanner.nextInt() - 1
        val y = scanner.nextInt() - 1
        val action = scanner.next()
        when (action) {
            "free" -> return setFree(x, y)
            "mine" -> return setMine(x, y)
            else -> return false
        }
    }

    fun setFree(x: Int, y: Int): Boolean {
        return when (bfPlayer[x + y * 9]) {
            '*' -> {
                println("Marked cell")
                false
            }
            '.' -> {
                openCell(x, y)
                true
            }
            else -> {
                println("Wrong cell")
                false
            }
        }
    }

    fun setMine(x: Int, y: Int): Boolean {
        return when(bfPlayer[x + y * 9]) {
            '.' -> {
                bfPlayer[x + y * 9] = '*'
                true
            }
            '*' -> {
                bfPlayer[x + y * 9] = '.'
                true
            }
            else -> {
                println("Wrong cell")
                false
            }
        }

    }

    fun openCell(x: Int, y: Int) {
        if (firstMove) {
            placeMines(numMines, x, y)
            markCell()
            firstMove = false
        }
        when (bf[x + y * 9]) {
            'X' -> gameOver()
            in '1'..'9' -> bfPlayer[x + y * 9] = bf[x + y * 9]
            //'.' -> bfPlayer[x + y * 9] = '/'
            '.' -> clearCells(x, y)
        }
    }

    fun isValid(x: Int, y: Int): Boolean {
        return !(x < 0 || y < 0 || x > 8 || y > 8)
    }

    fun clearCells(x: Int, y: Int) {
        if (bf[x + y * 9] in '1'..'9') {
            bfPlayer[x + y * 9] = bf[x + y * 9]
            return
        }
        if (bfPlayer[x + y * 9] == '/') {
            return
        }

        bfPlayer[x + y * 9] = bf[x + y * 9]
        if (bf[x + y * 9] == '.') {
            bfPlayer[x + y * 9] = '/'
        }

        if (isValid(x + 1, y)) {
            clearCells(x + 1, y)
        }
        if (isValid(x + 1, y + 1)) {
            clearCells(x + 1, y + 1)
        }
        if (isValid(x + 1, y - 1)) {
            clearCells(x + 1, y - 1)
        }
        if (isValid(x - 1, y)) {
            clearCells(x - 1, y)
        }
        if (isValid(x - 1, y - 1)) {
            clearCells(x - 1, y - 1)
        }
        if (isValid(x - 1, y + 1)) {
            clearCells(x - 1, y + 1)
        }
        if (isValid(x, y + 1)) {
            clearCells(x, y + 1)
        }
        if (isValid(x, y - 1)) {
            clearCells(x, y - 1)
        }
    }

    fun gameOver() {
        showMines()
        printBF()
        println("You stepped on a mine and failed!")
        gameOver = true
    }

    fun showMines() {
        for (i in bfPlayer.indices) {
            if (bf[i] == 'X') bfPlayer[i] = 'X'
        }
    }

    fun isWin(): Boolean {
        var isWin = true
        for (i in bf.indices) {
            if (bfPlayer[i] == '*' && bf[i] != 'X') isWin = false
            if (bfPlayer[i] != '*' && bf[i] == 'X') isWin = false
        }
        return isWin
    }

    fun noMove(): Boolean {
        var isWin = true
        for (i in bf.indices) {
            if (bfPlayer[i] == '.' && bf[i] != 'X') isWin = false
            if (bfPlayer[i] == '*' && bf[i] != 'X') isWin = false
        }
        return isWin
    }

    fun printBF() {
        print(" │123456789│\n" +
                "—│—————————│\n")
        var count = 1
        var numline = 1
        for (i in bfPlayer.indices) {
            if (count == 1) {
                print("$numline|")
                numline++
            }
            print(bfPlayer[i])

            if (count == 9) {
                print("|\n")
                count = 0
            }
            count++
        }

        print("—│—————————│\n")
    }

    fun placeMines(numMines: Int, x: Int, y: Int) {
        var count = numMines
        while(count > 0) {
            val mine = Random.nextInt(0, 80)
            if (bf[mine] == '.' && mine != x + y * 9) {
                bf[mine] = 'X'
                count--
            }
        }
    }

    fun markCell() {
        for (i in bf.indices) {
            if (bf[i] != 'X') {
                when (i) {
                    0 -> bf[i] = Character.forDigit(checkE(i) + checkS(i) + checkSE(i), 10)
                    8 -> bf[i] = Character.forDigit(checkW(i) + checkS(i) + checkSW(i), 10)
                    72 -> bf[i] = Character.forDigit(checkE(i) + checkN(i) + checkNE(i), 10)
                    80 -> bf[i] = Character.forDigit(checkW(i) + checkN(i) + checkNW(i), 10)
                    in 1..7 -> bf[i] = Character.forDigit(checkE(i) + checkS(i) + checkSE(i)
                            + checkW(i) + checkSW(i), 10)
                    in 73..79 -> bf[i] = Character.forDigit(checkE(i) + checkN(i) + checkNE(i)
                            + checkW(i) + checkNW(i), 10)
                    in 9..63 step 9 -> bf[i] = Character.forDigit(checkE(i) + checkN(i) + checkNE(i)
                            + checkS(i) + checkSE(i), 10)
                    in 17..71 step 9 -> bf[i] = Character.forDigit(checkW(i) + checkN(i) + checkNW(i)
                            + checkS(i) + checkSW(i), 10)
                    else -> bf[i] = Character.forDigit(checkNW(i) + checkN(i) + checkNE(i)
                            + checkW(i) + checkE(i)
                            + checkSW(i) + checkS(i) + checkSE(i), 10)

                }
                if(bf[i] == '0') bf[i] = '.'
            }
        }
    }

    fun checkNW(cell: Int): Int  = if (bf[cell - 9 - 1] == 'X') 1 else 0
    fun checkN(cell: Int): Int  = if (bf[cell - 9] == 'X') 1 else 0
    fun checkNE(cell: Int): Int  = if (bf[cell - 9 + 1] == 'X') 1 else 0
    fun checkW(cell: Int): Int  = if (bf[cell - 1] == 'X') 1 else 0
    fun checkE(cell: Int): Int  = if (bf[cell + 1] == 'X') 1 else 0
    fun checkSW(cell: Int): Int  = if (bf[cell + 9 - 1] == 'X') 1 else 0
    fun checkS(cell: Int): Int  = if (bf[cell + 9] == 'X') 1 else 0
    fun checkSE(cell: Int): Int  = if (bf[cell + 9 + 1] == 'X') 1 else 0
}