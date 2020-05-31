package Functions

var globalVariable = 10
var globalString:String?=null

fun add(x:Double=0.0, y:Double)
{
    val ans = x+y
    globalString = "String initialized inside add function"
    println("Addition result is $ans and global variable is $globalVariable and global string is $globalString")
}

//function overloading
fun add(x:Int, y:Int) : Int
{
    return x+y
}

//another function overloading
fun add(x:Double, y:Int) : Int
{
    return x.toInt()+y
}

fun displayUserNames(vararg names:String)
{
    for(i in names.indices)
    {
        println("Name at index $i is ${names[i]}")
    }
}

fun main()
{
    var userNamesArray = Array<String>(10){""}
    add(y = 1.2,x = 5.5)
    userNamesArray[0] = "Shadat"
    userNamesArray[1] = "Tonmoy"
    userNamesArray[2] = "Software"
    userNamesArray[3] = "Engineer II"
    displayUserNames(*userNamesArray)

}