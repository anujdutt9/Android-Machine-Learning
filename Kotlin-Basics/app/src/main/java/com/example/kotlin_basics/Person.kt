package com.example.kotlin_basics

open class Person (var firstName: String, var lastName: String){

    init {
        println("\nPerson Init Block...")

        if (firstName.isEmpty()){
            println("Error: Field is Empty")
        }

        if (lastName.isEmpty()){
            println("Error: Field is Empty")
        }
    }

    // Function to return Full Name
   fun fullName() = "$firstName $lastName"
}