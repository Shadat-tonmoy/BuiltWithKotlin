package Functions

val sum = { num1:Int,num2:Int ->
    num1+num2
}

fun main()
{
    var ans = sum(5,9)
    print("Ans is $ans")

    var numberList = ArrayList<Int>()

    numberList.add(1)
    numberList.add(5)
    numberList.add(2)
    numberList.add(6)
    numberList.add(3)
    numberList.add(9)
    numberList.add(12)

    numberList.forEach{number->
        println("Printing Number using Lambda Function $number")

    }

}