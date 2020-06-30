package OOP_Again

fun main()
{
    val bangladeshiPeople = BangladeshiPeople()
    bangladeshiPeople.singNationalSong()
    bangladeshiPeople.speak()
    val indianPeople = IndianPeople()
    indianPeople.singNationalSong()
    indianPeople.speak()

}

/**
 * any class can be abstract in nature. Abstract class is declared with the abstract modifier
 * Abstract class is by default open in nature. So we do not need to explicitly declare it as open.
 * Only abstract class can contain abstract method. That is if a method is abstract then the method must be inside an
 * abstract class. Abstract class can contain normal method too.
 * Any class that extends an abstract class (in this case basically implements) should and must override all of it's
 * abstract method. It is not mandatory to override it's non abstract method. But for abstract method it must be
 * overridden or the class need to be abstract itself.
 * In kotlin, member can be abstract that is we can declare abstract variable and abstract method. In these cases,
 * abstract variables should have no value and abstract method should have no body.
 * */
abstract class People
{
    abstract fun singNationalSong()
    abstract var nationality:String

    fun speak()
    {
        println("$nationality People is speaking....")
    }

}

class BangladeshiPeople : People()
{

    override var nationality: String = "Bangladesh"

    override fun singNationalSong() {
        println("Amar Sonar Bangla.....")
    }
}

class IndianPeople : People()
{

    override var nationality: String = "Indian"

    override fun singNationalSong() {
        println("Jana Gana Mana.....")
    }
}