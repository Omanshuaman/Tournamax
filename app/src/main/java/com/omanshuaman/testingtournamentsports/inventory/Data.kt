package com.omanshuaman.testingtournamentsports.inventory

import com.omanshuaman.testingtournamentsports.R
import java.util.ArrayList

object Data {
    val fruitList: List<Fruit>
        get() {
            val fruitList: MutableList<Fruit> = ArrayList()
            val Cricket = Fruit()
            Cricket.name = "Cricket"
            Cricket.image = R.drawable.cricket
            fruitList.add(Cricket)
            val Football = Fruit()
            Football.name = "Football"
            Football.image = R.drawable.football
            fruitList.add(Football)
            val Sprint = Fruit()
            Sprint.name = "Sprint"
            Sprint.image = R.drawable.sprint
            fruitList.add(Sprint)
            val Hockey = Fruit()
            Hockey.name = "Hockey"
            Hockey.image = R.drawable.field
            fruitList.add(Hockey)
            return fruitList
        }
}