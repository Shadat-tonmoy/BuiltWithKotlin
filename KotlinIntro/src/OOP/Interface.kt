package OOP

interface CreditCard
{
    fun getCardDetails()

    fun getLoanEligibilityByAge(age:Int)
}

class MasterCard : CreditCard
{
    private var cardNumber:String

    constructor(cardNumber:String)
    {
        this.cardNumber = cardNumber
    }

    override fun getCardDetails()
    {
        println("===MasterCard Details====")
        println("Card Number : $cardNumber")
    }

    override fun getLoanEligibilityByAge(age: Int)
    {
        if(age < 20 || age > 50)
        {
            println("You are not eligible for Loan")
        }
        else println("Okay! You can take loan")
    }
}

class VisaCard:CreditCard
{

    private var cardNumber:String

    constructor(cardNumber: String)
    {
        this.cardNumber = cardNumber
    }

    override fun getCardDetails()
    {
        println("===VisaCard Details====")
        println("Card Number : $cardNumber")
    }

    override fun getLoanEligibilityByAge(age: Int)
    {
        if(age < 30 || age > 60)
        {
            println("You are not eligible for Loan")
        }
        else println("Okay! You can take loan")
    }
}

fun main()
{
    var masterCard = MasterCard("123456789")
    masterCard.getCardDetails()
    masterCard.getLoanEligibilityByAge(10)

    var visaCard = VisaCard("1020304050")
    visaCard.getCardDetails()
    visaCard.getLoanEligibilityByAge(50)

}
