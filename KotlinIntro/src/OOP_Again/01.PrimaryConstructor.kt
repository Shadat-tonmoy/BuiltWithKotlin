package OOP_Again

fun main()
{
    var primaryConstructor = PrimaryConstructor("Shadat Tonmoy")
    primaryConstructor.printName()



}

class PrimaryConstructor (private var name:String)
{
    init {
        print("Initializing with $name\n")
    }

    fun printName()
    {
        print("Name is : $name")
    }
}