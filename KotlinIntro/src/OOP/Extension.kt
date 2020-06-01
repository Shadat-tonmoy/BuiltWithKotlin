package OOP

fun ArrayList<Int>.findMax():Int{
    var max:Int = 0
    for(i in this)
    {
        if(i > max)
        {
            max = i
        }
    }
    return max
}

fun main()
{
    var numbers = ArrayList<Int>()
    numbers.add(1)
    numbers.add(10)
    numbers.add(5)
    numbers.add(2)
    numbers.add(7)
    numbers.add(5)
    numbers.add(99)
    var maxNumber = numbers.findMax()
    println("Maximum in the list : $maxNumber")

}