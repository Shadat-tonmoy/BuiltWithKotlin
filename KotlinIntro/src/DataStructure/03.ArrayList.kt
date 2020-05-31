package DataStructure

fun main()
{
    var personList:ArrayList<String> = ArrayList<String>()

    for(i in 1..5)
    {
        personList.add(readLine()!!.toString())
    }
    for(i in personList.indices)
    {
        println("Person at index $i is ${personList[i]}")
    }
}

