package OOP

open class Vehicle
{
    protected var name:String?=null
    protected var price:Double?=null
    protected var milesRun:Int?=null

    constructor(name: String, price:Double, milesRun:Int)
    {
        this.name = name
        this.price = price
        this.milesRun = milesRun
    }

    fun showCarDetails()
    {
        println("====Car Details====")
        println("Name : $name\nPrice : $price\nMiles Run : $milesRun")
    }
}

class Truck : Vehicle
{
    constructor(name: String, price:Double, milesRun:Int) : super(name,price,milesRun)
    {
        println("Calling Child Class Constructor")
    }

    fun showTruckDetails()
    {
        println("====Truck Details====")
        println("Name : $name\nPrice : $price\nMiles Run : $milesRun")
    }
}

fun main()
{
    var truck = Truck("Toyota",12345.0,120)
    truck.showTruckDetails()
}