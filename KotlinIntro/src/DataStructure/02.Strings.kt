package DataStructure

fun main()
{
    var helloString = "Hello"
    var userName = "Shadat Tonmoy"
    var message = "Welcome To Kotlin Programming"

    var fullMessage = "$helloString, $userName $message"

    println("Full message is $fullMessage with length ${fullMessage.length}")

    println("Is Kotlin Programming : ${isKotlinProgramming(fullMessage)}")

    var tokenList : List<String>  = fullMessage.split(" ")
    for(i in tokenList.indices)
    {
        println("Token at position $i is ${tokenList[i]}")
    }
}

fun isKotlinProgramming(string: String) : Boolean
{
    return string.contains("Java")

}