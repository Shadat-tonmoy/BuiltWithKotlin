package LambdasAndHighLevelFunctions

fun main()
{
    val mathHelper = MathHelper()
    val result = 0
    mathHelper.addTwoNum(5,6, {a,b -> a+b})

}

/**
 * Closure is the option of modifying outside variable from lambdas returned value.
 * Closure is not available in Java 8. But Kotlin allows closure.
 * For closure lambda expression / function must return a value and this returned value can be assigned to any outside
 * variable. Here outside variable is the variable that resides inside the container of the callee of lambda. For
 * example the main function.
 * */
class MathHelper
{
    fun addTwoNum(a:Int, b:Int, adder:(Int,Int) -> Int)
    {
        val result = adder(a,b)
        println("Result of addition : $result")
    }

}