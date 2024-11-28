package com.holden.basicworkouttracker

import com.holden.basicworkouttracker.util.OrderedMap
import com.holden.basicworkouttracker.util.swap
import com.holden.basicworkouttracker.util.toOrderedMap
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class OrderedMapTests {
    lateinit var testOrderedMap: OrderedMap<String, Int>
    @Before
    fun setup() {
        testOrderedMap = listOf(
            "0" to 0,
            "1" to 1,
            "2" to 2,
            "3" to 3,
            "4" to 4,
            "5" to 5,
        ).toOrderedMap()
    }

    @Test
    fun `removeAtIndex should return correct result`() {
        val goal = listOf(
            "0" to 0,
            "2" to 2,
            "3" to 3,
            "4" to 4,
            "5" to 5,
        ).toOrderedMap()
        val result = testOrderedMap.removeAtIndex(1)
        assertEquals(goal, result)
    }

    @Test
    fun `insert should return correct result`(){
        val goal1 = listOf(
            "0" to 0,
            "1" to 1,
            "2" to 2,
            "3" to 3,
            "6" to 6,
            "4" to 4,
            "5" to 5,
        ).toOrderedMap()
        val result1 = testOrderedMap.insert("6" to 6, 4)
        assertEquals(goal1, result1)
        val goal2 = listOf(
            "0" to 0,
            "1" to 1,
            "2" to 2,
            "3" to 3,
            "5" to 5,
            "4" to 4
        ).toOrderedMap()
        val result2 = testOrderedMap.insert("5" to 5, 4)
        assertEquals(goal2, result2)
    }

    @Test
    fun `replaceAtIndex should return correct result`() {
        val goal = listOf(
            "0" to 0,
            "1" to 1,
            "2" to 2,
            "6" to 6,
            "4" to 4,
            "5" to 5,
        ).toOrderedMap()
        val result = testOrderedMap.replaceAtIndex("6" to 6, 3)
        assertEquals(goal, result)
    }

    @Test
    fun `swap works as expected`() {
        val goal = listOf(
            "0" to 0,
            "1" to 1,
            "2" to 2,
            "5" to 5,
            "4" to 4,
            "3" to 3,
        ).toOrderedMap()
        val result1 = testOrderedMap.swap(3, 5)
        assertEquals(goal, result1)
        val result2 = testOrderedMap.swap(5, 3)
        assertEquals(goal, result2)
    }

    @Test
    fun `another swap test`() {
        val startlist = listOf(0 to "0",1 to "1",2 to "2",3 to "3",4 to "4",5 to "5").toOrderedMap()
        val result = startlist.swap(0, 1)
        assertEquals(listOf(1 to "1",0 to "0",2 to "2",3 to "3",4 to "4",5 to "5").toOrderedMap(), result)
    }
}