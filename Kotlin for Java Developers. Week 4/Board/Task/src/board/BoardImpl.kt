package board

import board.Direction.*
import java.lang.IllegalArgumentException
import kotlin.math.max

fun createSquareBoard(width: Int): SquareBoard = object  : SquareBoard{

    val allCell = mutableListOf<Cell>()

    init {
        for(i in 1..width)
        {
            for(j in 1..width)
            {
                allCell.add(Cell(i,j))
            }
        }
    }

    override val width: Int
        get() = width



    override fun getCellOrNull(i: Int, j: Int): Cell?
    {
        return if( (i in 1..width) && (j in 1..width))
        {
            allCell.find { it.i == i && it.j == j }
        }
        else null
    }

    override fun getCell(i: Int, j: Int): Cell {
        if( (i in 1..width) && (j in 1..width))
        {
            val cell = allCell.find { it.i == i && it.j == j }
            if(cell != null) return cell
            else throw IllegalArgumentException()

        }
        else throw IllegalArgumentException()
    }

    override fun getAllCells(): Collection<Cell> {
        return allCell
    }

    override fun getRow(i: Int, jRange: IntProgression): List<Cell> {
        val cellList = mutableListOf<Cell>()
        if(jRange.last > jRange.first)
        {
            if((jRange.first in 1..width))
            {
                val last = jRange.last.coerceAtMost(width)
                for(j in jRange.first..last)
                {

//                    cellList.add(Cell(i, j))
                    val cell = allCell.find { it.i == i && it.j == j }
                    cellList.add(cell!!)
                }
            }
        }
        else
        {
            var last = jRange.last
            if(last > width) last = width
            var first = jRange.first
            if(first < 1) first = 1
            for(j in first downTo last)
            {
                val cell = allCell.find { it.i == i && it.j == j }
                cellList.add(cell!!)
//                cellList.add(Cell(i, j))
            }
        }
        return cellList
    }

    override fun getColumn(iRange: IntProgression, j: Int): List<Cell> {
        val cellList = mutableListOf<Cell>()
        if(iRange.last > iRange.first)
        {
            if((iRange.first in 1..width))
            {
                val last = iRange.last.coerceAtMost(width)
                for(i in iRange.first..last)
                {
                    val cell = allCell.find { it.i == i && it.j == j }
                    cellList.add(cell!!)
//                    cellList.add(Cell(i, j))
                }

            }

        }
        else
        {
            var last = iRange.last
            if(last > width) last = width
            var first = iRange.first
            if(first < 1) first = 1

            for(i in first downTo last)
            {
                val cell = allCell.find { it.i == i && it.j == j }
                cellList.add(cell!!)
//                cellList.add(Cell(i, j))
            }
        }
        return cellList
    }

    override fun Cell.getNeighbour(direction: Direction): Cell? {
        return if(direction == UP)
        {
            allCell.find { it.i == this.i-1 && it.j == this.j }
        }
        else if(direction == DOWN)
        {
            allCell.find { it.i == this.i+1 && it.j == this.j }
        }
        else if(direction == LEFT)
        {
            allCell.find { it.i == this.i && it.j == this.j-1 }
        }
        else if(direction == RIGHT)
        {
            allCell.find { it.i == this.i && it.j == this.j+1 }
        }
        else null
    }
}
fun <T> createGameBoard(width: Int): GameBoard<T> = object : GameBoard<T>{

    val allCell = mutableListOf<Cell>()

    init {
        for(i in 1..width)
        {
            for(j in 1..width)
            {
                allCell.add(Cell(i,j))
            }
        }
    }

    val map = HashMap<Cell, T?>()
    override val width: Int
        get() = width

    override fun getCellOrNull(i: Int, j: Int): Cell? {
        if( (i in 1..width) && (j in 1..width))
        {
            val cell = Cell(i,j)
            return cell
        }
        else return null
    }

    override fun getCell(i: Int, j: Int): Cell {
        if( (i in 1..width) && (j in 1..width))
        {
            val cell = allCell.find { it.i == i && it.j == j }
            if(cell != null) return cell
            else throw IllegalArgumentException()

        }
        else throw IllegalArgumentException()
    }

    override fun getAllCells(): Collection<Cell> {
        return allCell
    }

    override fun getRow(i: Int, jRange: IntProgression): List<Cell> {
        TODO("Not yet implemented")
    }

    override fun getColumn(iRange: IntProgression, j: Int): List<Cell> {
        TODO("Not yet implemented")
    }

    override fun Cell.getNeighbour(direction: Direction): Cell? {
        TODO("Not yet implemented")
    }

    override fun get(cell: Cell): T? {
        return map[cell]
    }

    override fun set(cell: Cell, value: T?) {
        val cellFromList = allCell.find { it.i == cell.i && it.j == cell.j }
       map[cellFromList!!] = value
    }

    override fun filter(predicate: (T?) -> Boolean): Collection<Cell> {
        val result = mutableListOf<Cell>()
        for((key, value) in map)
        {
            if(predicate(value))
            {
                result.add(allCell.find { it.i == key.i && it.j == key.j }!!)
            }
        }
        return result
    }

    override fun find(predicate: (T?) -> Boolean): Cell? {
        for((key, value) in map)
        {
            if(predicate(value))
            {
                return allCell.find { it.i == key.i && it.j == key.j }!!
            }
        }
        return null
    }

    override fun any(predicate: (T?) -> Boolean): Boolean {
        /*for((key, value) in map)
        {
            if(predicate(value))
            {
                return true
            }
        }
        return false*/

        for(cell in allCell)
        {
            if(predicate(map[cell]))
            {
                return true
            }
        }
        return false
    }

    override fun all(predicate: (T?) -> Boolean): Boolean {
        var count = 0
        /*for((key, value) in map)
        {
            if(predicate(value))
            {
                count++
            }
        }*/
        for(cell in allCell)
        {
            if(map[cell] != null && predicate(map[cell]))
            {
                count++
            }
        }
        return count == allCell.size
    }

}



