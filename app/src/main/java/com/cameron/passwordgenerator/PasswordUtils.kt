package com.cameron.passwordgenerator

class PasswordUtils {

    companion object {
        private val numbers = mutableListOf<Char>()
        private val lowerLetters = mutableListOf<Char>()
        private val upperLetters = mutableListOf<Char>()
        private val defaultList = mutableListOf<Char>()

        init {
            for (i in '0'.toInt()..'9'.toInt())
                numbers.add(i.toChar())

            for (i in 'a'.toInt()..'z'.toInt())
                lowerLetters.add(i.toChar())

            for (i in 'A'.toInt()..'Z'.toInt())
                upperLetters.add(i.toChar())

            defaultList.addAll(numbers)
            defaultList.addAll(lowerLetters)
            defaultList.addAll(upperLetters)
        }

        fun generatePassword(useNumbers: Boolean, useUpper: Boolean, useLower: Boolean, length: Int = 15): String {
            var retString = ""
            val charList = mutableListOf<Char>()

            if (useNumbers) charList.addAll(numbers)
            if(useUpper) charList.addAll(upperLetters)
            if (useLower) charList.addAll(lowerLetters)

            if (charList.isEmpty())
                return "Add at least one constraint"

            for (i in 0 until length)
                retString += charList.random()

            return retString
        }
    }
}