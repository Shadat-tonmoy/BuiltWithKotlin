package DataStructure

fun main()
{
    var num:Array<Int> = Array<Int>(10){0}

    //iterating array
    for (i in num)
    {
        println("Num is $i")
    }

    //iterating array with indices
    for(i in 0 until num.size)
    {
        println("Num at index $i is ${num[i]}")
    }

    //iterating array with indices (recommended approach)
    for(i in num.indices)
    {
        println("Num at index $i is ${num[i]}")
    }


}