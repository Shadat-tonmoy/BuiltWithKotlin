package OOP_Again

fun main()
{
    val secondaryConstructor = SecondaryConstructor("Shadat Tonmoy")
    secondaryConstructor.printName()

}

/**
 * Secondary constructor has body. We can use this body to initialize or assign passed parameter as a member variable
 * Secondary constructor can only have parameter. We can not declare member variable with the help of secondary
 * constructor parameter. That is secondary constructor can not have val, var member with or without access modifier
 * We can use primary and secondary constructor at the same time.
 * If we initialize all the member variable inside secondary constructor body then no need to use lateinit, null
 * assignment to member variable. They are redundant
 * For simple variable assignment kotlin always recommends the primary constructor to make code cleaner
 * */
class SecondaryConstructor
{
    private var name: String

    constructor(name:String)
    {
        print("Name from SecondaryConstructor : $name")
        this.name = name
    }

    fun printName()
    {
        print("Name from printName method : $name")
    }

}