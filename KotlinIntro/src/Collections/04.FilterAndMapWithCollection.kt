package Collections

import kotlin.math.pow

/**
 * Filter expects an lambda and can be applied to a collection only. The lambda is often referred as predicate.
 * The predicate works like, it takes an parameter (of the same type as the collection element), apply some
 * operation that return a boolean result. Now it will perform the operation over each element in the collection
 * and for those whose result of the operation is true only those values are returned. As it takes only one param
 * we can use 'it' here. It is quite natural that it will take at most one parameter as it will take the
 * collection element one by one and perform the operation onto them.
 * Map works in the similar fashion but instead of returning boolean it return a modified version of each
 * element in the collection based on lambda's operation. Here also we can use the "it" as it only takes a
 * single parameter for the same reason as filter.
 * We can also combine them (filter, map) together
 * */
fun main()
{
    val numbers = listOf<Int>(1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30)
    val evenNumbers = numbers.filter { n -> n%2 == 0 }
    println("EvenNumbers : $evenNumbers")
    val oddNumbers = numbers.filter { it%2==1 }
    println("oddNumbers : $oddNumbers")
    val doubledNumber = numbers.map { n -> n*2 }
    println("doubledNumbers : $doubledNumber")
    val squaredNumbers = numbers.map { it*it }
    println("squaredNumbers : $squaredNumbers")
    val evenSquared = numbers.filter { it%2 == 0 }.map { it*it }
    println("evenSquared : $evenSquared")


}