fun main()
{

    var number = 5
    println("Number Before Increment : $number")
    number++
    println("Number after increment : $number")

    print("How Many Operations : ")
    val n = readLine()!!.toInt()
    for(i in 1..n)
    {
        print("Enter number 1 : ")
        var number1 = readLine()!!.toDouble()
        print("Enter number 2 : ")
        var number2 = readLine()!!.toDouble()

        println("Addition : ${number1+number2}")
        println("Subtraction : ${number1-number2}")
        println("Multiplication : ${number1 * number2}")
        println("Division : ${number1 / number2}")
    }


}