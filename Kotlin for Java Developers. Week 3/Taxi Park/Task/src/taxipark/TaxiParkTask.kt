package taxipark

import java.lang.Math.ceil
import java.lang.Math.floor

/*
 * Task #1. Find all the drivers who performed no trips.
 */
fun TaxiPark.findFakeDrivers(): Set<Driver> {
    val fakeDrivers = HashSet<Driver>()
    for(driver in allDrivers)
    {
        val tripByDriver = trips.find { it.driver.name == driver.name }
        if(tripByDriver == null)
        {
            fakeDrivers.add(driver)
        }
    }
    return fakeDrivers
}


/*
 * Task #2. Find all the clients who completed at least the given number of trips.
 */
fun TaxiPark.findFaithfulPassengers(minTrips: Int): Set<Passenger> {
    val faithfulPassengers = HashSet<Passenger>()
    for(passenger in allPassengers)
    {
        val tripByPassenger = trips.count { it.passengers.contains(passenger) }
        if(tripByPassenger >= minTrips) faithfulPassengers.add(passenger)
    }
    return faithfulPassengers

}


/*
 * Task #3. Find all the passengers, who were taken by a given driver more than once.
 */
fun TaxiPark.findFrequentPassengers(driver: Driver): Set<Passenger> {
    val frequentPassenger = HashSet<Passenger>()
    for(passenger in allPassengers)
    {
        val tripByPassenger = trips.count { it.passengers.contains(passenger) && it.driver.name == driver.name }
        if(tripByPassenger > 1) frequentPassenger.add(passenger)
    }
    return frequentPassenger
}

/*
 * Task #4. Find the passengers who had a discount for majority of their trips.
 */
fun TaxiPark.findSmartPassengers(): Set<Passenger> {
    val smartPassengers = HashSet<Passenger>()
    for(passenger in allPassengers)
    {
        val tripsWithDiscount = trips.count { it.passengers.contains(passenger) && it.discount != null && it.discount > 0.0 }

        val tripsWithoutDiscount = trips.count { it.passengers.contains(passenger) && (it.discount == null || it.discount <= 0.0) }

        if(tripsWithDiscount > tripsWithoutDiscount) smartPassengers.add(passenger)
    }
    return smartPassengers
}

/*
 * Task #5. Find the most frequent trip duration among minute periods 0..9, 10..19, 20..29, and so on.
 * Return any period if many are the most frequent, return `null` if there're no trips.
 */
fun TaxiPark.findTheMostFrequentTripDurationPeriod(): IntRange? {
    var startMap = HashMap<Int, Int>()
    for(trip in trips)
    {
        val lowerIndex = findLowerIndex(trip.duration)
//        print("Lower indes is : $lowerIndex for duration : ${trip.duration}\n\n")
        if(startMap.containsKey(lowerIndex))
        {
            val existing = startMap[lowerIndex]
            startMap[lowerIndex] = existing?.plus(1)!!
        }
        else startMap[lowerIndex] = 1
    }
//    print(startMap)
    val maxEntry = startMap.maxBy { it.value }
    if(maxEntry == null) return null
    val maxKey = maxEntry.key
    return IntRange(maxKey, maxKey+9)
}

fun findLowerIndex(num : Int) : Int
{
    var startIndex = 0

    if(num < 10) startIndex = 0
    else
    {
        startIndex = num
        while (startIndex % 10 != 0)
            startIndex--
    }
    return startIndex

}

/*
 * Task #6.
 * Check whether 20% of the drivers contribute 80% of the income.
 */
fun TaxiPark.checkParetoPrinciple(): Boolean {
    if(trips.isEmpty()) return false
    var map = HashMap<String,Double>()
    for(trip in trips)
    {
        val existing = map[trip.driver.name]
        if(existing == null) map[trip.driver.name] = trip.cost
        else map[trip.driver.name] = existing + trip.cost
    }
    val result = map.toList().sortedByDescending { (_, value) -> value}.toMap()

    val totalIncome = trips.sumByDouble { it.cost }
    var totalDrivers = allDrivers.size

    val _20 = (totalDrivers * 0.2).toInt()
//    print("20% driver : $_20 totalDriver : $totalDrivers")
    var total = 0
    var topTotal = 0.0
//    print("Result : $result totalDriver : $totalDrivers")
    for((key, value) in result)
    {
        if(total < _20) topTotal += value
        total++
        if(total >= _20) break

    }
//    print("topTotal : $topTotal, 80% : ${totalIncome * 0.8} 20% : ${_20}")
    return topTotal >= totalIncome * 0.8
}