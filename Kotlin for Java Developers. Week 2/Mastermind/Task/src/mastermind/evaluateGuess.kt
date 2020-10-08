package mastermind

data class Evaluation(val rightPosition: Int, val wrongPosition: Int)

fun evaluateGuess(secret: String, guess: String): Evaluation {
    var right = 0
    var wrong = 0
    val charsSecret = secret.toCharArray()
    val charsGuess = guess.toCharArray()
    for(i in charsSecret.indices)
    {
        if(charsSecret[i] == charsGuess[i])
        {
            right++
            charsSecret[i] = '*'
            charsGuess[i] = '*'
        }
    }

    for(i in charsSecret.indices)
    {
        if(charsSecret[i] == '*') continue
        val index = charsGuess.indexOf(charsSecret[i])
//        print("For ${charsSecret[i]} index : $index")
        if(index == i)
        {
            right++
            charsGuess[index] = '*'
        }
        else if(index != -1)
        {
            wrong++
            charsGuess[index] = '*'
        }
    }
    return Evaluation(right, wrong)
}

fun main() {
    val result = evaluateGuess("AABC","DEFC")
    print(result)
}
