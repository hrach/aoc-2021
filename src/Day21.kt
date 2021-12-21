package day21

interface Dice {
    fun getNext(): Int
    val rolls: Int

    class Deterministic : Dice {
        var i = 1
        override var rolls = 0
        override fun getNext() = i.also {
            i += 1
            rolls += 1
        }
    }
}

fun play(player1: Int, player2: Int, dice: Dice): Int {
    var pos1 = player1
    var pos2 = player2
    var score1 = 0
    var score2 = 0
    while (score1 < 1000 && score2 < 1000) {
        pos1 = (pos1 + dice.getNext() + dice.getNext() + dice.getNext()) % 10
        if (pos1 == 0) pos1 = 10
        score1 += pos1

        if (score1 >= 1000) break

        pos2 = (pos2 + dice.getNext() + dice.getNext() + dice.getNext()) % 10
        if (pos2 == 0) pos2 = 10
        score2 += pos2
    }

    val rolls = dice.rolls
    return rolls * minOf(score1, score2)
}

fun main() {
    fun part1(player1: Int, player2: Int): Int {
        return play(player1, player2, Dice.Deterministic())
    }

    data class Wins(
        val wins1: Long,
        val wins2: Long,
    )

    data class State(
        val pos1: Int,
        val pos2: Int,
        val score1: Int,
        val score2: Int,
    ) {
        fun playP1(i: Int): State {
            var pos = (pos1 + i) % 10
            pos = if (pos == 0) 10 else pos
            return copy(
                pos1 = pos,
                score1 = score1 + pos,
            )
        }

        fun playP2(i: Int): State {
            var pos = (pos2 + i) % 10
            pos = if (pos == 0) 10 else pos
            return copy(
                pos2 = pos,
                score2 = score2 + pos,
            )
        }
    }

    val results: MutableMap<State, Wins> = mutableMapOf()

    fun genPlaysP1(state: State): List<State> = buildList {
        for (i in 1..3) {
            for (j in 1..3) {
                for (k in 1..3) {
                    add(state.playP1(i + j + k))
                }
            }
        }
    }

    fun genPlaysP2(state: State): List<State> = buildList {
        for (i in 1..3) {
            for (j in 1..3) {
                for (k in 1..3) {
                    add(state.playP2(i + j + k))
                }
            }
        }
    }

    fun playRecursive(state: State): Wins = results.getOrPut(state) {
        val playsP1 = genPlaysP1(state)
        playsP1.fold(Wins(wins1 = 0, wins2 = 0)) { acc, s ->
            if (s.score1 >= 21) {
                acc.copy(wins1 = acc.wins1 + 1)
            } else {
                val playsP2 = genPlaysP2(s)
                playsP2.fold(acc) { acc, s ->
                    if (s.score2 >= 21) {
                        acc.copy(wins2 = acc.wins2 + 1)
                    } else {
                        val res = playRecursive(s)
                        acc.copy(
                            wins1 = acc.wins1 + res.wins1,
                            wins2 = acc.wins2 + res.wins2,
                        )
                    }
                }
            }
        }
    }

    fun part2(player1: Int, player2: Int): Long {
        val result = playRecursive(State(score1 = 0, score2 = 0, pos1 = player1, pos2 = player2))
        val max = maxOf(result.wins1, result.wins2)
        return max
    }

    check(part1(player1 = 4, player2 = 8) == 739785)
    check(part2(player1 = 4, player2 = 8) == 444356092776315)

    println(part1(player1 = 6, player2 = 1))
    println(part2(player1 = 6, player2 = 1))
}
