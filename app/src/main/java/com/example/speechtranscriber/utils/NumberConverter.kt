package com.example.speechtranscriber.utils

object NumberConverter {
    
    // Mapeo de números básicos
    private val basicNumbers = mapOf(
        "cero" to 0, "uno" to 1, "dos" to 2, "tres" to 3, "cuatro" to 4, "cinco" to 5,
        "seis" to 6, "siete" to 7, "ocho" to 8, "nueve" to 9, "diez" to 10,
        "once" to 11, "doce" to 12, "trece" to 13, "catorce" to 14, "quince" to 15,
        "dieciséis" to 16, "diecisiete" to 17, "dieciocho" to 18, "diecinueve" to 19,
        "veinte" to 20, "treinta" to 30, "cuarenta" to 40, "cincuenta" to 50,
        "sesenta" to 60, "setenta" to 70, "ochenta" to 80, "noventa" to 90,
        "cien" to 100, "ciento" to 100, "mil" to 1000, "millón" to 1000000, "millones" to 1000000
    )
    
    // Patrones para detectar números
    private val numberPatterns = listOf(
        // Números simples: "uno", "dos", "tres"
        Regex("\\b(cero|uno|dos|tres|cuatro|cinco|seis|siete|ocho|nueve|diez|once|doce|trece|catorce|quince|dieciséis|diecisiete|dieciocho|diecinueve|veinte|treinta|cuarenta|cincuenta|sesenta|setenta|ochenta|noventa|cien|ciento|mil|millón|millones)\\b", RegexOption.IGNORE_CASE),
        
        // Números compuestos: "veintiuno", "treinta y dos"
        Regex("\\b(veintiuno|veintidós|veintitrés|veinticuatro|veinticinco|veintiséis|veintisiete|veintiocho|veintinueve)\\b", RegexOption.IGNORE_CASE),
        Regex("\\b(treinta|cuarenta|cincuenta|sesenta|setenta|ochenta|noventa)\\s+y\\s+(uno|dos|tres|cuatro|cinco|seis|siete|ocho|nueve)\\b", RegexOption.IGNORE_CASE),
        
        // Números de cientos: "ciento veinte", "doscientos"
        Regex("\\b(ciento|doscientos|trescientos|cuatrocientos|quinientos|seiscientos|sietecientos|ochocientos|novecientos)\\s+(\\w+)\\b", RegexOption.IGNORE_CASE),
        Regex("\\b(doscientos|trescientos|cuatrocientos|quinientos|seiscientos|sietecientos|ochocientos|novecientos)\\b", RegexOption.IGNORE_CASE),
        
        // Números de miles: "mil doscientos"
        Regex("\\bmil\\s+(\\w+)\\b", RegexOption.IGNORE_CASE),
        
        // Números complejos: "mil doscientos treinta y cuatro"
        Regex("\\b(mil)\\s+(doscientos|trescientos|cuatrocientos|quinientos|seiscientos|sietecientos|ochocientos|novecientos)?\\s*(\\w+)?\\s*(y\\s+\\w+)?\\b", RegexOption.IGNORE_CASE)
    )
    
    /**
     * Convierte texto que contiene números hablados a formato numérico
     */
    fun convertSpokenNumbersToDigits(text: String): String {
        var result = text
        
        // Buscar y reemplazar números complejos primero
        result = convertComplexNumbers(result)
        
        // Buscar y reemplazar números simples
        result = convertSimpleNumbers(result)
        
        return result
    }
    
    private fun convertComplexNumbers(text: String): String {
        var result = text
        
        // Patrón para números complejos como "mil doscientos treinta y cuatro"
        val complexPattern = Regex("\\b(mil)\\s+(doscientos|trescientos|cuatrocientos|quinientos|seiscientos|sietecientos|ochocientos|novecientos)?\\s*(\\w+)?\\s*(y\\s+\\w+)?\\b", RegexOption.IGNORE_CASE)
        
        complexPattern.findAll(text).forEach { matchResult ->
            val original = matchResult.value
            val number = parseComplexNumber(original)
            if (number != null) {
                result = result.replace(original, number.toString())
            }
        }
        
        return result
    }
    
    private fun convertSimpleNumbers(text: String): String {
        var result = text
        
        // Convertir números simples
        basicNumbers.forEach { (word, number) ->
            val pattern = Regex("\\b$word\\b", RegexOption.IGNORE_CASE)
            result = pattern.replace(result, number.toString())
        }
        
        // Convertir números compuestos como "veintiuno"
        val compoundNumbers = mapOf(
            "veintiuno" to 21, "veintidós" to 22, "veintitrés" to 23,
            "veinticuatro" to 24, "veinticinco" to 25, "veintiséis" to 26,
            "veintisiete" to 27, "veintiocho" to 28, "veintinueve" to 29
        )
        
        compoundNumbers.forEach { (word, number) ->
            val pattern = Regex("\\b$word\\b", RegexOption.IGNORE_CASE)
            result = pattern.replace(result, number.toString())
        }
        
        // Convertir patrones como "treinta y dos"
        val tensPattern = Regex("\\b(treinta|cuarenta|cincuenta|sesenta|setenta|ochenta|noventa)\\s+y\\s+(uno|dos|tres|cuatro|cinco|seis|siete|ocho|nueve)\\b", RegexOption.IGNORE_CASE)
        
        tensPattern.findAll(result).forEach { matchResult ->
            val groups = matchResult.groups
            val tens = groups[1]?.value?.lowercase()
            val ones = groups[2]?.value?.lowercase()
            
            if (tens != null && ones != null) {
                val tensValue = basicNumbers[tens] ?: 0
                val onesValue = basicNumbers[ones] ?: 0
                val total = tensValue + onesValue
                result = result.replace(matchResult.value, total.toString())
            }
        }
        
        return result
    }
    
    private fun parseComplexNumber(text: String): Int? {
        val words = text.lowercase().split("\\s+".toRegex())
        var result = 0
        var currentHundreds = 0
        var currentTens = 0
        var currentOnes = 0
        
        for (word in words) {
            when {
                word == "mil" -> {
                    if (result == 0) result = 1000
                    else result *= 1000
                }
                word in listOf("ciento", "doscientos", "trescientos", "cuatrocientos", 
                              "quinientos", "seiscientos", "sietecientos", "ochocientos", "novecientos") -> {
                    currentHundreds = when (word) {
                        "ciento" -> 100
                        "doscientos" -> 200
                        "trescientos" -> 300
                        "cuatrocientos" -> 400
                        "quinientos" -> 500
                        "seiscientos" -> 600
                        "sietecientos" -> 700
                        "ochocientos" -> 800
                        "novecientos" -> 900
                        else -> 0
                    }
                }
                word in listOf("treinta", "cuarenta", "cincuenta", "sesenta", "setenta", "ochenta", "noventa") -> {
                    currentTens = basicNumbers[word] ?: 0
                }
                word == "y" -> continue
                word in basicNumbers -> {
                    currentOnes = basicNumbers[word] ?: 0
                }
            }
        }
        
        val total = currentHundreds + currentTens + currentOnes
        return if (total > 0) result + total else null
    }
    
    /**
     * Detecta si el texto contiene números hablados
     */
    fun containsSpokenNumbers(text: String): Boolean {
        return numberPatterns.any { pattern ->
            pattern.containsMatchIn(text)
        }
    }
} 