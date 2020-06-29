package OOP_Again

fun main()
{
    var primaryConstructor = PrimaryConstructor("Shadat Tonmoy")
    primaryConstructor.printName()

    val primaryConstructorWithParameterOnly = PrimaryConstructorWithParameterOnly("Shadat Tonmoy")
    primaryConstructorWithParameterOnly.printName()



}

/**
 * Primary constructor can have only parameter, member variable with access modifier like private, val or var type
 * member variable declaration. It has no body. But we can use the init{...} to act as a body of the primary constructor
 * While passing only parameter inside the primary constructor (not declaring the member) we need to explicitly declare
 * the member variable inside class to assign the passed parameter value to the member variable.
 * */
class PrimaryConstructor (private var name:String)
{
    init {
        print("Initializing PrimaryConstructor with $name\n")
    }

    fun printName()
    {
        print("Name (From PrimaryConstructor) is : $name")
    }
}

/**
 * Here we only pass the parameter with primary constructor. So we need to explicitly declare a member variable.
 * */
class PrimaryConstructorWithParameterOnly (name:String)
{
    var name:String?=null
    init {
        print("Initializing PrimaryConstructorWithParameterOnly name : $name\n")
        this.name = name
    }

    fun printName()
    {
        print("Name (From PrimaryConstructorWithParameterOnly) is : $name")
    }
}