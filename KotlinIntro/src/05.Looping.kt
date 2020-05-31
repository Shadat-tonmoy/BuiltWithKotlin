fun main()
{
    println("Will Print Using Loop")
    for(i in 1..10)
        println("Now Printing $i")

    println("Will Print Using 2 Step")

    for(i in 1..10 step 2)
        println("Now Printing $i")

    println("Will print in reverse order")

    for(i in 10 downTo 1)
        println("Now printing $i")

    println("Will print in reverse order and 2 Step")

    for(i in 10 downTo 1 step 2)
        println("Now printing $i")
}