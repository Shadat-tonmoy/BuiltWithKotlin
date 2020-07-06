package Collections

/**
 * Arrays are mutable in nature. That is once assigned we can change the value of element in certain index.
 * Arrays have fixed size in nature. That is while declaring an array we must define the size of the array
 * and using a lambda must assign the initial value of all the elements in the array.
 * Array can not be resized. That is if we want to enlarge the size of an array we need to re-declare another
 * array of larger size
 * */
fun main()
{
    var nums:Array<Int> = Array<Int>(50){0}
    println(nums[0])
    for(num in nums)
    {
        println("Num is $num")
    }

    for(i in nums.indices)
    {
        println("Num at index $i is ${nums[i]}")
    }
}