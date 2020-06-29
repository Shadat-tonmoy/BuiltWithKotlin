package OOP_Again

fun main()
{
    val secondaryConstructor = SecondaryConstructor("Shadat Tonmoy")
    secondaryConstructor.printName()

    val secondaryConstructorWithPrimary = SecondaryConstructorWithPrimary("Shadat Tonmoy",23)
    secondaryConstructorWithPrimary.printInfo()

}

/**
 * Secondary constructor has body. We can use this body to initialize or assign passed parameter as a member variable
 * Secondary constructor can only have parameter. We can not declare member variable with the help of secondary
 * constructor parameter. That is secondary constructor can not have val, var member with or without access modifier
 * We can use primary and secondary constructor at the same time.
 * If we initialize all the member variable (Object type not primitive type) inside secondary constructor body then no
 *  need to use lateinit, null assignment to member variable. They are redundant. This is only true for object type
 *  like String not for primitive type like int, float etc.
 * For simple variable assignment kotlin always recommends the primary constructor to make code cleaner
 * */
class SecondaryConstructor
{
    private var name: String

    constructor(name:String)
    {
        println("Name from SecondaryConstructor : $name")
        this.name = name
    }

    fun printName()
    {
        println("Name from printName method : $name")
    }

}

/**
 * While using secondary constructor alongside primary constructor we need to call the primary constructor with
 * proper parameter passing right after the declaration of secondary constructor. The primary constructor will
 * declare it's member variable (if any), then call the init{...} block. We can use the init block to assign value
 * to the primary constructor declared variable (if any and if we wish)
 * */
class SecondaryConstructorWithPrimary(private val nameFromPrimary:String)
{
    private var age:Int?=null

    init {
        println("Iniside init block age is $age")
    }

    constructor(name: String,age:Int) : this(name)
    {
        this.age = age
        print("After secondary constructor age is ${this.age}")
    }

    fun printInfo()
    {
        println("From PrintInfo Method Name : $nameFromPrimary, age : $age")
    }
}