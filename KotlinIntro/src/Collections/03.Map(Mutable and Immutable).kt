package Collections

/**
 * Map is a key value based data structure. By default map is in immutable in nature. That is once assigned
 * we can not modify the element inside the map. Map is defined by mapOf<...>() keyword in kotlin. It is read
 * only. It has no put or set method only get or accessor method is available.
 * to modify elements inside a map we need to use mutable map which is available by HashMap<...>
 * For immutable map while initializing we must set the initial values as we can not assign the value later
 * For mutable map we can not set initial value while initializing. We can set value letter by using put(...)
 * method.
 * */
fun main()
{
    val map = mapOf<String,String>("Name" to "Shadat Tonmoy", "Dept" to "CSE", "Nationality" to "Bangladeshi")
    println(map)
    for(key in map.keys)
    {
        println("Key -> $key, value -> ${map[key]}")
    }

    val hashMap = HashMap<String,String>()
    hashMap.put("Name","Shadat Tonmoy")
    hashMap["Department"] = "CSE" // recommended approach in kotlin
    hashMap["Nationality"] = "CSE"
    println(hashMap)



}