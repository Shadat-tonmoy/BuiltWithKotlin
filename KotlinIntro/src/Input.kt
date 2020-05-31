fun main()
{
    print("Enter First Name : ")
    var firstName:String = readLine()!!.toString()
    while(!isValidString(firstName))
    {
        print("Please enter a valid First Name : ")
        firstName = readLine()!!.toString()
    }
    print("Enter Last Name : ")
    var lastName:String = readLine()!!.toString()
    while (!isValidString(lastName))
    {
        print("Please Enter a valid Last Name : ")
        lastName = readLine()!!.toString()
    }
    println("Enter Age : ")
    var age:Int = readLine()!!.toInt()

    println("======User Info======")

    print("First Name : $firstName\nLast Name : $lastName\nAge : $age")
}

fun isValidString(string: String) : Boolean
{
    return string.isNotEmpty()
}