package com.example.kotlin_basics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Val: Values are non Changeable
        val msg = "Hello World"
        val modulo = 5%2

        // Var: Variables value can be changed at any time
        var result = 9
        result += 5

        println("$msg" + " " + "$modulo" + " " +"$result")

        // Show Text on Screen by Grabbing Text View using ID
        //textView.text = "$msg" + " " + "$modulo" + " " +"$result"

        // Arrays and Lists
        // Array: arrays are fixed size
        // Index starts at "0"
        // Without any explicit definition, array can take any type of value
        val evenNums = arrayOf(2,4,"Anuj",6,8,"Jenny",10,12,"Chris",14,16,"John",18)
        val firstVal = evenNums.first()
        val lastVal = evenNums.last()
        val indexVal = evenNums.get(4)
        // Change value at Index
        evenNums.set(0,20)
        // Count
        val valsCount = evenNums.count()
        // Use "arr.toString()" to print all contents of array
        println("Array: ${Arrays.toString(evenNums)}" + "\nCount: $valsCount" + "\nFirst Value: $firstVal" + "\nLast Value: $lastVal" + "\nValue at Index 4: $indexVal")

        // Explicitly Defined Array
        // Only Strings allowed in this example.
        val names = arrayOf<String>("India", "Australia", "Canada", "USA")
        println("Countries: ${Arrays.toString(names)}")

        // Lists: lists size can be changed
        val oddNums = mutableListOf(1,3,5,7,9,11,13,15,17,19,21)
        // Add a new element at Index
        oddNums.add(2,23)
        val firstNum = oddNums.first()
        val lastNum = oddNums.last()
        val indexNum = oddNums.get(9)
        val numsCount = oddNums.count()
        println("List: $oddNums" + "\nCount: $numsCount" + "\nFirst Number: $firstNum" + "\nLast Number: $lastNum" + "\nNumber at Index 9: $indexNum")

        // Accessing Array using For Loop
        println("--------------- Even Numbers -------------------")
        for (element in evenNums) print("$element ")
        println()

        // Accessing List using For Loop
        println("--------------- Odd Numbers --------------------")
        for (vals in oddNums) print("$vals ")
        println()

        // If-Else Statement
        var on = true
        if (on){
            println("\nLights ON !!")
            on = false
        }
        else {
            println("\nLights OFF !!")
        }

        // When Statement <=> Switch Statement
        val check = when(on){
            true -> println("\nLights are ON !!")
            false -> println("\nLights are OFF !!")
        }
        println("$check")

        for (element in oddNums){
            when(element){
                1 -> println("Greater than zero !")
                3,5,7,9 -> println("Less than 10 !")
                11,13,15,17,19 -> println("Less than 20 !")
                21 -> println("Greater than 20 !")
                else -> println("Unknown Number...")
            }
        }

        // While Loop
        var i = 10
        while (i >= 5){
            println("\ni: $i")
            i -= 1
        }

        // do-while Loop
        var j = 5
        do{
            println("j: $j")
            j += 1
        } while (j < 20)

        // Calling a Function
        println("Double of 5 is ${double(5)}")
        println("My name is ${getName("Anuj","Dutt")}")

        // Instantiate the "Person" object
        val person = Person("Anuj","Dutt").fullName()
        println("Person's Name: $person")

        // Subclass- superclass Inheritance
        val employeeData = Employee(firstName = "John", lastName = "Wayne",company = "ABC Company", emailID = "abc@company.com").getBioData()
        println(" \n*********************** ")
        println("Employee Information:\n$employeeData")
        println(" ***********************\n ")
    }

    // Function Declaration
    // Define Input Type and Return Type
    // x: Input, type: Int
    // Return Type: Int
    fun double(x: Int): Int{
        return 2 * x
    }

    // Function to Print a Name
    // Since the function returns a single expression, you can omit the curly braces and specify body after " = "
    fun getName(firstName: String, lastName: String) = "$firstName $lastName"
}
