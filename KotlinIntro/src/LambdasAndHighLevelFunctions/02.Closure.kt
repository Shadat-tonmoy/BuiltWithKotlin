package LambdasAndHighLevelFunctions

fun main()
{
    val mathHelper = MathHelper()
    var result = 0
    mathHelper.addTwoNum(5,6, {a,b -> result = a+b})
    println("Result of addition from main function : $result")

}

/**
 * Closure is the option of modifying outside variable from lambdas returned value.
 * Closure is not available in Java 8. But Kotlin allows closure.
 * For closure lambda expression / function must not return a value as if we want to assign some value to any outside
 * variable within the body of lambda then technically lambda is not returning anything.
 * Here outside variable is the variable that resides inside the container of the callee of lambda. For
 * example the main function.
 * */
class MathHelper
{
    fun addTwoNum(a:Int, b:Int, adder:(Int,Int) -> Unit)
    {
        val result = adder(a,b)
        println("Result of addition : $result")
    }

}