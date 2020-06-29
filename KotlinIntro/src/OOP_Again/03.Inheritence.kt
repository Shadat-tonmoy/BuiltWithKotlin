package OOP_Again

fun main()
{
    val cat = Cat()
    val dog = Dog()

    cat.showInfo()
    dog.showInfo()



}

open class Animal
{
    val color = "Default animal color"
    val age = -1

    fun eat()
    {
        println("Animal is eating....")
    }


}

class Dog : Animal()
{
    fun bark()
    {
        println("Dog is barking...")
    }

    fun showInfo()
    {
        println("Showing Dog Info color : $color, age : $age")
    }

}

class Cat : Animal()
{
    fun meow()
    {
        println("cat is mewing")
    }

    fun showInfo()
    {
        println("Showing Cat Info color : $color, age : $age")
    }
}