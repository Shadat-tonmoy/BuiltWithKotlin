package LambdasAndHighLevelFunctions


/**
 * Lambda and High Level functions are extensively used in android.
 * High Level functions are functions or methods that can take other functions as parameter and/or can return
 * other functions.
 * If any function perform one of these two operation then it can be called as a high level function.
 * Lambdas are functions in the form of expression. It is often similar to a variable which has a data type.
 * Lambdas can be declared as of the followings syntax :
 * - {method_param_with_data_type -> method_body} for example :
 * - {a,b -> println(a+b)}
 * If lambda returns something then it should be the last line of the method body.
 * Lambda can be assigned to a variable also. Then the syntax will be :
 * val x : (Int,Int) -> Unit = {a,b -> println(a+b)}
 * here the (Int,Int) -> Unit refers to the type of x meaning that it takes two integer as parameter and return Unit
 * */
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
    button.addToNumber(1,2, {a,b -> println("Summation of $a and $b is ${a+b}")})
    button.addToNumber(3,4) { a, b -> println("Summation of $a and $b is ${a+b}")}


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