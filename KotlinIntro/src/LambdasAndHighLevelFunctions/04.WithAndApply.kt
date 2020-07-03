package LambdasAndHighLevelFunctions

/**
 * with and apply is two lambda function can be applied to any class level variable
 * they help to assign member's value of class.
 * The with(...) takes an object as parameter and return nothing. It is just like a simple setter function that
 * allow to set the value of different field inside a class. We can also call method of the class from this lambda
 * function. But for this the members must be public in nature.
 * The apply is similar to with(...) but does not take any argument. Instead it is applied to a object and return
 * the modified object after assigning value to the members. Here also the members must be public in nature.
 * */
fun main()
{
    val person = Person()
    with(person){
        firstName = "Shadat"
        lastName = "Tonmoy"
        age = 24
        showInfo()
    }

    person.showInfo()

    person.apply {
        firstName = "Shadat Mufakhkhar"
        lastName = "Tonmoy"
        age = 26
    }.showInfo()
}

class Person{
    lateinit var firstName:String
    lateinit var lastName:String
    var age:Int? = null

    fun showInfo()
    {
        println("Person info First Name : $firstName, Last Name : $lastName, age : $age")
    }
}