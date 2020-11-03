package rationals

import java.math.BigInteger

fun main() {
    val half = 1 divBy 2
    val third = 1 divBy 3

    val sum: Rational = half + third
//    println("Sum : ${sum::class.simpleName} ${(5 divBy 6)::class.simpleName}")
    println(5 divBy 6 == sum)

    val difference: Rational = half - third
//    println("difference : $difference ${1 divBy 6}")
    println(1 divBy 6 == difference)


    val product: Rational = half * third
//    println("product : $product ${1 divBy 6}")
    println(1 divBy 6 == product)

    val quotient: Rational = half / third
//    println("quotient : $quotient ${3 divBy 2}")
    println(3 divBy 2 == quotient)

    val negation: Rational = -half
    println(-1 divBy 2 == negation)

    println((2 divBy 1).toString() == "2")
    println((-2 divBy 4).toString() == "-1/2")
    println("117/1098".toRational().toString() == "13/122")

    val twoThirds = 2 divBy 3
    println(half < twoThirds)

    println(half in third..twoThirds)

    println(2000000000L divBy 4000000000L == 1 divBy 2)

    println("912016490186296920119201192141970416029".toBigInteger() divBy
            "1824032980372593840238402384283940832058".toBigInteger() == 1 divBy 2)
}

operator fun ClosedRange<Rational>.contains(half: Rational): Boolean {
    return this.contains(half)

}

fun String.toRational(): Rational
{
    val parts = split("/")
    val nominator = BigInteger(parts[0])
    var denominator = BigInteger.ONE
    if(parts.size >1) denominator = BigInteger(parts[1])
    return Rational(nominator,denominator)
}

infix fun Int.divBy(i: Int): Rational {
    return Rational(this.toBigInteger(), i.toBigInteger()).normalize()
}

infix fun Long.divBy(i: Long): Rational {
    return Rational(this.toBigInteger(), i.toBigInteger()).normalize()
}

infix fun String.divBy(i: String): Rational {
    return Rational(this.toBigInteger(), i.toBigInteger()).normalize()
}

infix fun BigInteger.divBy(i: BigInteger): Rational {
    return Rational(this, i).normalize()
}

data class Rational(var nominator : BigInteger, var denominator : BigInteger) : Comparable<Rational>
{

    operator fun plus(third: Rational): Rational
    {
        val lcm = findLCM(this.denominator, third.denominator)
        val a = lcm.div(this.denominator).multiply(this.nominator)
        val b = lcm.div(third.denominator).multiply(third.nominator)
        return Rational(a.add(b), lcm)
    }

    operator fun minus(third: Rational): Rational {
        val lcm = findLCM(this.denominator, third.denominator)
        val a = lcm.div(this.denominator).multiply(this.nominator)
        val b = lcm.div(third.denominator).multiply(third.nominator)
        return Rational(a.subtract(b), lcm)

    }

    operator fun times(third: Rational): Rational {
        return Rational(third.nominator.multiply(this.nominator), third.denominator.multiply(this.denominator))

    }

    operator fun div(third: Rational): Rational {
        return Rational(this.nominator.multiply(third.denominator), this.denominator.multiply(third.nominator))
    }

    operator fun unaryMinus(): Rational {
        return Rational(this.nominator.multiply(BigInteger("-1")),this.denominator.abs())


    }

    fun normalize () : Rational
    {
        val gcd = nominator.gcd(denominator)
        return Rational(nominator.div(gcd), denominator.div(gcd))
    }

    override operator fun compareTo(other: Rational): Int {

        val second = this.normalize().toString().toRational()
        val third = other.normalize().toString().toRational()

        val lcm = findLCM(second.denominator, third.denominator)
        val a = lcm.div(second.denominator).multiply(second.nominator)
        val b = lcm.div(third.denominator).multiply(third.nominator)

        /*val a = this.nominator.toBigDecimal().div(this.denominator.toBigDecimal())
        val b = other.nominator.toBigDecimal().div(other.denominator.toBigDecimal())
        println("this $this other : $other A is $a b is $b")*/
        return a.compareTo(b)


    }

    override fun equals(other: Any?): Boolean {
        if(other is Rational)
        {
            val second = this.normalize().toString().toRational()
            val third = other.normalize().toString().toRational()
            return (second.nominator == third.nominator && second.denominator == third.denominator)
        }
        return super.equals(other)
    }

    override fun toString(): String
    {
        val normalizedRational = normalize()
        var nominator = normalizedRational.nominator
        var denominator = normalizedRational.denominator
        if(nominator < BigInteger.ZERO && denominator < BigInteger.ZERO)
        {
            nominator = nominator.multiply((-1).toBigInteger())
            denominator = denominator.multiply((-1).toBigInteger())
        }
        if(denominator == BigInteger.ONE || denominator == (-1).toBigInteger())
        {
            return if(denominator < BigInteger.ZERO)
            {
                "-$nominator"
            }
            else "$nominator"
        }
        else
        {
//            println("TOString : nom : $nominator denom : $denominator")
            if(denominator < BigInteger.ZERO)
            {
                denominator = denominator.multiply((-1).toBigInteger())
                if(nominator > BigInteger.ZERO){
                    nominator = nominator.multiply((-1).toBigInteger())
                }
            }
            return "$nominator/$denominator"
        }

    }

    operator fun rangeTo(other: Rational) : ClosedRange<Rational>
    {
        return object : ClosedRange<Rational>{
            override val endInclusive: Rational
                get() = other
            override val start: Rational
                get() = this@Rational
        }
    }

    fun findLCM(a : BigInteger, b : BigInteger) : BigInteger
    {
        val ab = a.multiply(b)
        val gcd = a.gcd(b)
        val lcm : BigInteger = ab.div(gcd)
        return lcm
    }


}