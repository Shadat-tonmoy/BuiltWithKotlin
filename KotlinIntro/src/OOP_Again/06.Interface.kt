package OOP_Again

fun main()
{
    val button = Button()
    button.onClick()
    button.onTouch()
    button.onLongPress()
    button.onNothing()

}

/**
 * Interface is declared with interface keyword in kotlin
 * Interface is by default open, public in nature. So we don't need to use these keywords.
 * Interface can not be instantiated
 * Interface can contain normal method as well as abstract method. If a method does not have any body it is abstract
 * by default, we do not need to use the abstract keyword. If a method inside an interface has body it can not be abstract
 * Interface is not a class, it is just a blueprint or container of some empty method.
 * Any class should implement the interface by overriding it's abstract method.
 * To implement an interface we don't need any primary constructor call like inheritance as interface can not have any
 * primary constructor.
 * If a class implements an interface it must implement all of it's abstract members.
 * If we have a non abstract normal method in more than one interface and same class implements both of the interface,
 * then we must override the non abstract method (member) in the overridden class. This is the rule otherwise the
 * compiler won't work
 * In this case, we can call the super class method by following super<className>.methodName() to identify the
 * exact super class to call.
 * */
interface Listener
{
    fun onClick()

    fun onTouch()

    fun onLongPress()

    fun onNothing()
    {
        println("No Interaction....")
    }
}

interface Listener2
{
    fun onClick()

    fun onTouch()

    fun onLongPress()

    fun onNothing()
    {
        println("No Interaction....")
    }
}

class Button : Listener, Listener2
{
    override fun onClick() {
        println("Button was clicked")
    }

    override fun onTouch() {
        println("Button was touched")
    }

    override fun onLongPress() {
        println("Button was long pressed")
    }

    override fun onNothing() {
        super<Listener>.onNothing()
    }
}