package Collections


/**
 * By default list is immutable in nature that is we can not modify (add, remove, update)
 * the elements of the list. listOf is similar to List<...> in java
 * To modify elements inside a list we need to use mutable list which is available by mutableListOf<...>,
 * arrayListOf<...>, ArrayList<...>. All of these basically are the same thing. They internally use the
 * ArrayList<...> implementation. We can use any of them.
 * For immutable list while initializing we must set the initial values as we can not assign the value later
 * For mutable list initial value is not required but we can assign if we wish so.
 * */
fun main()
{
    val list = listOf<String>("A","B","C","D")
    println(list)
    val mutableList = mutableListOf<String>()
    mutableList.add("A")
    mutableList.add("B")
    mutableList.add("C")
    println(mutableList)
//    mutableList.remove("C")
    mutableList.add(1,"X")
    println(mutableList)
    val arrayList = ArrayList<Int>()
    arrayList.add(1)
    arrayList.add(2)
    arrayList.add(3)
    arrayList.add(4)
    arrayList.add(5)
    arrayList.add(6)
    println(arrayList)

}