package OOP

interface DigitalPayment
{
    fun showCardNumber()
    fun isEligibleForLoan()
}

class MobileBanking:DigitalPayment
{
    private var cardNumber:String?=null
    private var ageOfCardHolder:Int?=null

    constructor(cardNumber:String, ageOfCardHolder:Int)
    {
        this.cardNumber = cardNumber
        this.ageOfCardHolder = ageOfCardHolder
    }

    override fun showCardNumber()
    {
        println("Card Number of Mobile Banking $cardNumber")
    }

    override fun isEligibleForLoan()
    {
        if(ageOfCardHolder in 25..60) println("Yes You Can Take Loan From Mobile Banking!") else println("No! You can't take loan  From Mobile Banking")
    }
}

class DebitCard:DigitalPayment
{
    private var cardNumber:String?=null
    private var ageOfCardHolder:Int?=null

    constructor(cardNumber:String, ageOfCardHolder:Int)
    {
        this.cardNumber = cardNumber
        this.ageOfCardHolder = ageOfCardHolder
    }

    override fun showCardNumber()
    {
        println("Card Number of Debit Card $cardNumber")
    }

    override fun isEligibleForLoan()
    {
        if(ageOfCardHolder in 20..60) println("Yes You Can Take Loan From Debit Card!") else println("No! You can't take loan From Debit Card")
    }
}

fun main()
{
    var digitalPayment = DebitCard(cardNumber = "123456767",ageOfCardHolder = 15)
    var digitalPayment2 = MobileBanking(cardNumber = "01715303801",ageOfCardHolder = 55)
    digitalPayment.showCardNumber()
    digitalPayment.isEligibleForLoan()
    digitalPayment2.showCardNumber()
    digitalPayment2.isEligibleForLoan()
//    println("Digital Payment 1 -> ${digitalPayment.showCardNumber()} Loan Eligibility -> ${digitalPayment.isEligibleForLoan()}")
//    println("Digital Payment 2 -> ${digitalPayment2.showCardNumber()} Loan Eligibility -> ${digitalPayment2.isEligibleForLoan()}")
}