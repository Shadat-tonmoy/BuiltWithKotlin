package OOP_Again

fun main()
{
    val cat = Cat()
    val dog = Dog()
    val animal = Animal()

    cat.color = "White"
    cat.age = 10

    dog.color = "Black"
    dog.age = 12


    cat.showInfo()
    dog.showInfo()

    animal.eat()
    cat.eat()
    dog.eat()





}

/**
 * By default classes are public and final in kotlin, and according to OOP rules final class can not be inherited.
 * For this we need to make a class open by using the open keyword.
 * This public final thing is also true for method and member variable too. That is method and member variable can
 * not be directly overridden. In order to override them they should be open as well
 * We can call the super class method from the sub class method using the super keyword while overriding
 * If we inherit multiple class or interfaces and a same named method is present in more then one super class
 * then while calling the super class method we need to specify the exact super class using the following syntax
 * super<className>.methodName(). For example : super<Animal>.eat(), super<Mammal>.eat()
 * */
open class Animal
{
    open var color = "Default animal color"
    open var age = -1

    open fun eat()
    {
        println("Animal is eating....")
    }


}

class Dog : Animal()
{

    override var color: String = "Black"
    override var age: Int = 13

    fun bark()
    {
        println("Dog is barking...")
    }

    fun showInfo()
    {
        println("Showing Dog Info color : $color, age : $age")
    }

    override fun eat() {
        println("Dog is eating....")
    }

}

class Cat : Animal()
{

    override var color: String
        get() = "Milky"
        set(value) {}

    override var age: Int
        get() = 5
        set(value) {}


    fun meow()
    {
        println("cat is mewing")
    }

    fun showInfo()
    {
        println("Showing Cat Info color : $color, age : $age")
    }

    override fun eat() {
        //super<Animal>.eat()
        println("Cat is eating....")
    }
}