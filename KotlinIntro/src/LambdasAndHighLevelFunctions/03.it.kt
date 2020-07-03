package LambdasAndHighLevelFunctions

fun main()
{
    val stringManager = StringManager()
    stringManager.reverseAndDisplay("Hello", {it.reversed()})
    var reversed = ""
    stringManager.reverseAndSet("Tonmoy",{reversed = it.reversed()})
    println("Reversed from main function is $reversed")

}

class StringManager
{
    fun reverseAndDisplay(string: String, method: (String) -> String)
    {
        val result = method(string)
        println("Reversed of $string is $result")
    }

    fun reverseAndSet(string: String, method: (String) -> Unit)
    {
        method(string)
        /*val result = method(string)
        println("Reversed of $string is $result")*/
    }

}