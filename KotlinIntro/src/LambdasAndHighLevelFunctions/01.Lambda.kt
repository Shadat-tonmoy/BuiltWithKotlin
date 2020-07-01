package LambdasAndHighLevelFunctions

fun main()
{
    val button = Button()
    button.interact(object : Listener{
        override fun onClick() { println("Clicked On Button") }
    })

}

class Button
{
    fun interact(listener: Listener)
    {
        listener.onClick()
    }

}

interface Listener
{
    fun onClick()
}