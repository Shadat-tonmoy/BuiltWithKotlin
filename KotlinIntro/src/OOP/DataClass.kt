package OOP

data class Person(val firstName:String, val lastName:String, val age:Int, val nationality:String)

fun main()
{
    var person = Person("Shadat","Tonmoy",23,"Bangladesh")
    println("====Person Details===")
    println("First Name : ${person.firstName}")
    println("Last Name : ${person.lastName}")
    println("Age : ${person.age}")
    println("Nationality : ${person.nationality}")

}