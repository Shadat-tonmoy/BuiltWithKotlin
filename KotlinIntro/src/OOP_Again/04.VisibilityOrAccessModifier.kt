package OOP_Again

/**
 * Kotlin has 4 types of visibility modifier -
 * - public : Accessible anywhere inside the project
 *
 * - protected : Is not applicable for high level members. High level members are member (function/variable) those are
 * not inside any classes. That is if we declare them into a kotlin file (not any class) then they are the high level
 * members. Protected modifier is not applicable to these types of members. Protected modifier can be applied to class
 * member only and accessible to only sub classes. For example in the following classes c is visible only in the
 * Bangladeshi subclass
 *
 * - internal : Accessible inside the module only. In a project we can have multiple module. For example, in Android
 * application app is a module, if we use retrofit (by adding dependency in gradle) then retrofit is another module
 *
 * - private : Accessible inside only the file (if no class is declared), only the classes (if class is declared)
 * In kotlin by default everything is public in nature (everything means variables, functions, classes)
 * */
fun main()
{
    val bangladeshi = Bangladeshi()
    bangladeshi.testVisibility()

    val anyOtherClass = AnyOtherClass()
    anyOtherClass.testVisibility()


}

open class Person{
    val a = 1
    private val b = 2
    protected val c = 3
    internal val d = 4
}

class Bangladeshi : Person()
{
    fun testVisibility()
    {
        println("a is $a b is not visible c is $c d is $d")
    }
}

class AnyOtherClass
{
    val person = Person()
    fun testVisibility()
    {
        println("a is ${person.a} b is not visible, c is not visible d is ${person.d}")
    }
}

fun visibilityFromAnotherFile()
{
    println("Printing from a default public method from outside file")
}

private fun notVisibleToOtherFiles()
{
    println("Printing from a private method")
}

/**
 * As this class is by default public in nature it is accessible in other files
 * */
class PublicClass
{


}

/**
 * As this class is modified to private in nature it is not accessible in other files
 * */
private class PrivateClass
{

}

/**
 * As this class is modified to internal in nature it is accessible inside the module only
 * */
internal class InternalClass
{

}