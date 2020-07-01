package LambdasAndHighLevelFunctions

fun main()
{
    val button = Button()
    button.interact(object : Listener{
        override fun onClick() { println("Clicked On Button") }
    })

    val listener = {m:String -> println(m)}
    button.interact(listener)

    val adder : (Int,Int) -> Unit = {a,b -> println("Summation of $a and $b is ${a+b}")}
    button.addToNumber(5,10,adder)


    //lambda function without defining any type
    val testWithoutType = { n:String -> n.length > 10 }
    //we can write the above lambda as
    val testWithType : (String) -> Boolean = {n -> n.length > 10}
    //lambda with multiple parameter
    val isSubstring : (String, String) -> Boolean = {a,b -> a.contains(b)}
    //a little bit complex lambda
    val isPalindrome : (String, String) -> Boolean = {a,b -> a.reversed() == b }
    val getGenderFromChar : (Char) -> String = {genderChar -> if(genderChar=='M') "Male"
    else if(genderChar=='F') "Female"
    else "Unknown"}

    println("Gender is ${getGenderFromChar('M')}")

    //lambda function with signature as type of variable
    val test : (Int) -> Unit = { n -> println("PrintingFromLambdaFunction $n") }
    test(5)
}

class Button {
    fun interact(listener: Listener) {
        listener.onClick()
    }

    fun interact(lambda: (String) -> Unit)
    {
        val message = "ButtonWasClicked"
        lambda(message)
    }

    fun addToNumber(a:Int, b:Int, adder : (Int, Int) -> Unit)
    {
        adder(a,b)
    }

}

interface Listener
{
    fun onClick()
}