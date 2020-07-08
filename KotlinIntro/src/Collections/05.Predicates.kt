package Collections


/**
 * Predicates are just simple condition that return true or false. Following are some uses of
 * predicates in kotlin
 * all{...} - it returns true if all the elements satisfies the condition/predicate inside the lambda,
 * otherwise return false
 * any{...} - it returns true if any element satisfies the condition/predicate inside the lambda, otherwise
 * return false
 * count{...} - it returns the total number of element that satisfy the condition/predicate inside the lambda
 * find{...} - it returns the first element that satisfy the condition/predicate inside the lambda
 * */
fun main()
{
    val numbers = listOf<Int>(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30)
    val evenNumbers = numbers.filter { it%2==0 }
    val isAllEven = evenNumbers.all { it%2==0 }
    println("AllEven : $isAllEven")
    val isAnyOdd = evenNumbers.any { it%2 == 1 }
    println("AnyOdd : $isAnyOdd")
    val totalEvenInMainList = numbers.count { it%2==0 }
    val totalOddInMainList = numbers.count { it%2==1 }
    println("Total Even : $totalEvenInMainList, total Odd : $totalOddInMainList")



}