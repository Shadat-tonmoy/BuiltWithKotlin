package OOP

class CarWithConstructor
{
    var name:String?=null
    var price:Double?=null
    var milesRun:Int?=null

    constructor(name:String, price:Double, milesRun:Int)
    {
        println("Calling Constructor")
        this.name = name
        this.price = price
        this.milesRun = milesRun
    }

    constructor(name: String,price: Double)
    {
        println("Calling Second Constructor")
        this.name = name
        this.price = price
        this.milesRun = 0
    }

    fun showCarDetails()
    {
        println("====Car Details====")
        println("Name : $name\nPrice : $price\nMiles Run : $milesRun")
    }

}

fun main()
{
    var carWithConstructor = CarWithConstructor(name = "Toyota",price = 1256.0)
    carWithConstructor.showCarDetails()


}