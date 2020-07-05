package OOP_Again

/**
 * In OOP object is declared with instantiation of individual object. Singleton is special kind of object that has
 * only one instance of object in the whole application. That is if we declare a class as singleton we can have
 * only one instance of that class. This single instance creation is handled by kotlin internally.
 * We can not create any object of a singleton class in kotlin.
 * In java for singleton we need to use static keyword. But in kotlin we do not have anything called static.
 * In kotlin we can declare a singleton class by using object keyword.
 * All the members of the singleton object is by default static in nature
 * Singleton nature class can also inherit property from super class
 * */
fun main()
{
    //var student = Student() //not possible to create an instance of student as it is singleton in nature
    Student.firstName = "Shadat"
    Student.lastName = "Tonmoy"
    println("Singleton Student ${Student.firstName} ${Student.lastName}")
    Student.showInfo()
    Student.lastName = "Tommy"
    Student.showInfo()

}

object Student{
    var firstName:String ?=null // by default static in nature
    var lastName:String ?=null  // by default static in nature

    fun showInfo()
    {
        println("First name is $firstName last name is $lastName")
    }
}

