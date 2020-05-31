package OOP

class Car(val name:String, val price:Double, val milesRun:Double)
{
    init {
        println("Initializing Empty Car")
    }

    fun getPriceOfCar():Double{
        return this.price
    }
}

fun main() {
    val car = Car(name = "BMW",price = 1000.0,milesRun = 17.4)
    println("=====Car Info=====")
    println("Name Of Car ${car.name}")
    println("Price of Car ${car.getPriceOfCar()}")
    println("Miles run of Car ${car.milesRun}")

}