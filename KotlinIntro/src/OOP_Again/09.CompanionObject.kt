package OOP_Again

fun main()
{
    println("Constant Tag is ${Constants.TAG}")
}

class Constants{

    companion object{
        const val TAG = "HelloWorld"
    }
}