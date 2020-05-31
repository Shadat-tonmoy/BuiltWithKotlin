fun main()
{
    val nationalitiy = "Bangladesh"
    var firstName:String
    var lastName:String
    var age:Int
    var department:String? = null

    firstName = "Shadat"
    lastName = "Tonmoy"
    age = 23

    println("My Information")

    println("First Name : $firstName")
    println("Last Name : $lastName")
    println("Age : $age")
    println("Adult : ${age>18}")

    /*will throw null pointer exception. This is called null safety. We are forcing that this variable
    must have a value not null
    !! indicates the value should not be null. If null then exception will be thrown
    */
    //println("Department : ${department!!}")

    //not null safe. That is the variable value can be null.
    println("Department : $department")



}