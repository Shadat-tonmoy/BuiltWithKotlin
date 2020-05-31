fun main()
{
    var age = readLine()!!.toInt()

    var isEven = if(age%2==0 && age>=20) 55 else 10

    println("Is Even : $isEven")

    println("Should Go Further? (Y/N)")

    var message = readLine().toString()

    if(!message.equals("Y"))
    {
        println("Good Bye!")
        return
    }

    while(age!=-1)
    {
        when
        {
            age in 1..10 ->
                println("You are a child")
            age in 11..18 ->
                println("You are a teenage")
            age in 19..50 ->
                println("You are young")
            age in 51..80 ->
                println("You are old")
            else ->
            {
                println("You are dead! Good Bye!")
                age = -1
            }
        }
        age = readLine()!!.toInt()
    }



    while (age!=-1)
    {
        if(age in 1..10)
            println("You are a child")
        else if(age in 11..18)
            println("You are a teenage")
        else if(age in 19..50)
            println("You are young")
        else if(age in 51..80)
            println("You are old")
        else if(age > 80)
        {
            println("You are dead! Good Bye")
            break
        }
        age = readLine()!!.toInt()
    }

    println("=====End of Program=====")


}