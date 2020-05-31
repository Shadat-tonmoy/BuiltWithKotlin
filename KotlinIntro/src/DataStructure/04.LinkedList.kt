package DataStructure

import java.util.*

fun main()
{
    var personList = LinkedList<String>()

    for(i in 1..5)
    {
        personList.add(readLine()!!.toString())
    }

    for(i in personList.indices)
    {
        println("Person At index $i is ${personList[i]}")
    }
}