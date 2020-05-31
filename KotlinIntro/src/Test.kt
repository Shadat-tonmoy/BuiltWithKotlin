fun main()
{
    var n:Int
    n = readLine()!!.toInt()
    var sum = 0
    for(i in 1 until n)
    {
        for(j in 1..3)
        {
            var m = readLine()!!.toInt()
            if(j==1)
                m-=m
            sum+=m
        }
    }
    if(sum==0)
        println("YES")
    else println("NO")
}