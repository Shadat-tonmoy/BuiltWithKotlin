package OOP_Again

fun main()
{
    val user1 = User("Tonmoy","12345")
    val user2 = User("Tonmoy","12345")
    if(user1 == user2)
        println("Equal")
    else println("Not equal")
    println(user1)
    println(user2)

}


/**
 * Data class is kind of model class in Kotlin
 * The main difference between normal class and data class is while comparing to object data class is compared based
 * on their values. For example in the user data class while comparing the compiler compare two object based on their
 * value. If two class have similar value then they will be treated as equal. But normal class is compared based on the
 * address, so they will not be equal.
 * */
data class User(val name:String,val id:String)