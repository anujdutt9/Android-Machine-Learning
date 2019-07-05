package com.example.kotlin_basics

// Class Employee extends Person
// Inherits the First Name and Last Name Strings from Person class
class Employee(firstName: String, lastName: String, var age:Int = 18, var company: String, var emailID: String): Person(firstName, lastName){
    init {
        println("Employee Init Block...")
    }

    // Function to get Employee BioData
    fun getBioData() = "Name: $firstName $lastName \nAge: $age \nCompany: $company \nEmail ID: $emailID"
}