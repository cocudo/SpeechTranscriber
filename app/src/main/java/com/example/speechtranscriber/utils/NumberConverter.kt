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
    
    // Números compuestos especiales (veintiuno, veintidós, etc.)
    private val compoundNumbers = mapOf(
        "veintiuno" to 21, "veintidós" to 22, "veintitrés" to 23,
        "veinticuatro" to 24, "veinticinco" to 25, "veintiséis" to 26,
        "veintisiete" to 27, "veintiocho" to 28, "veintinueve" to 29
    )
    
    // Números de centenas
    private val hundredsNumbers = mapOf(
        "ciento" to 100, "doscientos" to 200, "trescientos" to 300, "cuatrocientos" to 400,
        "quinientos" to 500, "seiscientos" to 600, "sietecientos" to 700, "ochocientos" to 800, "novecientos" to 900
    )
    
    /**
     * Convierte texto que contiene números hablados a formato numérico
     * Usa la coma como delimitador para evitar capturas incorrectas
     */
    fun convertSpokenNumbersToDigits(text: String): String {
        if (!containsSpokenNumbers(text)) return text
        
        var result = text
        
        // 1. Procesar números compuestos especiales (veintiuno, veintidós, etc.)
        result = processCompoundNumbers(result)
        
        // 2. Procesar patrones de decenas + unidades (treinta y dos)
        result = processTensAndOnes(result)
        
        // 3. Procesar números de centenas + decenas + unidades
        result = processHundredsWithTensAndOnes(result)
        
        // 4. Procesar números de miles + centenas + decenas + unidades
        result = processThousandsWithHundreds(result)
        
        // 5. Procesar números básicos restantes
        result = processBasicNumbers(result)
        
        return result
    }
    
    /**
     * Procesa números compuestos especiales como "veintiuno", "veintidós"
     */
    private fun processCompoundNumbers(text: String): String {
        var result = text
        
        compoundNumbers.forEach { (word, number) ->
            val pattern = Regex("\\b$word\\b", RegexOption.IGNORE_CASE)
            result = pattern.replace(result, number.toString())
        }
        
        return result
    }
    
    /**
     * Procesa patrones de decenas + unidades como "treinta y dos"
     * Usa la coma como delimitador para evitar capturas incorrectas
     */
    private fun processTensAndOnes(text: String): String {
        var result = text
        
        // Buscar patrones que terminan en coma o punto
        val tensPattern = Regex(
            "\\b(treinta|cuarenta|cincuenta|sesenta|setenta|ochenta|noventa)\\s+y\\s+(uno|dos|tres|cuatro|cinco|seis|siete|ocho|nueve)\\s*[,.]",
            RegexOption.IGNORE_CASE
        )
        
        tensPattern.findAll(result).forEach { matchResult ->
            val groups = matchResult.groups
            val tens = groups[1]?.value?.lowercase()
            val ones = groups[2]?.value?.lowercase()
            val delimiter = matchResult.value.last()
            
            if (tens != null && ones != null) {
                val tensValue = basicNumbers[tens] ?: 0
                val onesValue = basicNumbers[ones] ?: 0
                val total = tensValue + onesValue
                result = result.replace(matchResult.value, "$total$delimiter")
            }
        }
        
        // Buscar patrones que terminan en espacio seguido de otra palabra
        val tensPatternSpace = Regex(
            "\\b(treinta|cuarenta|cincuenta|sesenta|setenta|ochenta|noventa)\\s+y\\s+(uno|dos|tres|cuatro|cinco|seis|siete|ocho|nueve)\\s+(?!y\\b)",
            RegexOption.IGNORE_CASE
        )
        
        tensPatternSpace.findAll(result).forEach { matchResult ->
            val groups = matchResult.groups
            val tens = groups[1]?.value?.lowercase()
            val ones = groups[2]?.value?.lowercase()
            
            if (tens != null && ones != null) {
                val tensValue = basicNumbers[tens] ?: 0
                val onesValue = basicNumbers[ones] ?: 0
                val total = tensValue + onesValue
                result = result.replace(matchResult.value, "$total ")
            }
        }
        
        return result
    }
    
    /**
     * Procesa números de centenas + decenas + unidades como "doscientos treinta y cuatro"
     * Usa la coma como delimitador para evitar capturas incorrectas
     */
    private fun processHundredsWithTensAndOnes(text: String): String {
        var result = text
        
        // Patrón completo con delimitador
        val hundredsPattern = Regex(
            "\\b(doscientos|trescientos|cuatrocientos|quinientos|seiscientos|sietecientos|ochocientos|novecientos)\\s+(treinta|cuarenta|cincuenta|sesenta|setenta|ochenta|noventa)\\s+y\\s+(uno|dos|tres|cuatro|cinco|seis|siete|ocho|nueve)\\s*[,.]",
            RegexOption.IGNORE_CASE
        )
        
        hundredsPattern.findAll(result).forEach { matchResult ->
            val groups = matchResult.groups
            val hundreds = groups[1]?.value?.lowercase()
            val tens = groups[2]?.value?.lowercase()
            val ones = groups[3]?.value?.lowercase()
            val delimiter = matchResult.value.last()
            
            if (hundreds != null && tens != null && ones != null) {
                val hundredsValue = hundredsNumbers[hundreds] ?: 0
                val tensValue = basicNumbers[tens] ?: 0
                val onesValue = basicNumbers[ones] ?: 0
                val total = hundredsValue + tensValue + onesValue
                result = result.replace(matchResult.value, "$total$delimiter")
            }
        }
        
        // Solo centenas + decenas con delimitador
        val hundredsTensPattern = Regex(
            "\\b(doscientos|trescientos|cuatrocientos|quinientos|seiscientos|sietecientos|ochocientos|novecientos)\\s+(treinta|cuarenta|cincuenta|sesenta|setenta|ochenta|noventa)\\s*[,.]",
            RegexOption.IGNORE_CASE
        )
        
        hundredsTensPattern.findAll(result).forEach { matchResult ->
            val groups = matchResult.groups
            val hundreds = groups[1]?.value?.lowercase()
            val tens = groups[2]?.value?.lowercase()
            val delimiter = matchResult.value.last()
            
            if (hundreds != null && tens != null) {
                val hundredsValue = hundredsNumbers[hundreds] ?: 0
                val tensValue = basicNumbers[tens] ?: 0
                val total = hundredsValue + tensValue
                result = result.replace(matchResult.value, "$total$delimiter")
            }
        }
        
        // Solo centenas con delimitador
        val hundredsOnlyPattern = Regex(
            "\\b(doscientos|trescientos|cuatrocientos|quinientos|seiscientos|sietecientos|ochocientos|novecientos)\\s*[,.]",
            RegexOption.IGNORE_CASE
        )
        
        hundredsOnlyPattern.findAll(result).forEach { matchResult ->
            val groups = matchResult.groups
            val hundreds = groups[1]?.value?.lowercase()
            val delimiter = matchResult.value.last()
            
            if (hundreds != null) {
                val hundredsValue = hundredsNumbers[hundreds] ?: 0
                result = result.replace(matchResult.value, "$hundredsValue$delimiter")
            }
        }
        
        return result
    }
    
    /**
     * Procesa números de miles + centenas + decenas + unidades
     * Usa la coma como delimitador para evitar capturas incorrectas
     */
    private fun processThousandsWithHundreds(text: String): String {
        var result = text
        
        // Patrón completo con delimitador: "mil doscientos treinta y cuatro,"
        val fullThousandsPattern = Regex(
            "\\bmil\\s+(doscientos|trescientos|cuatrocientos|quinientos|seiscientos|sietecientos|ochocientos|novecientos)\\s+(treinta|cuarenta|cincuenta|sesenta|setenta|ochenta|noventa)\\s+y\\s+(uno|dos|tres|cuatro|cinco|seis|siete|ocho|nueve)\\s*[,.]",
            RegexOption.IGNORE_CASE
        )
        
        fullThousandsPattern.findAll(result).forEach { matchResult ->
            val groups = matchResult.groups
            val hundreds = groups[1]?.value?.lowercase()
            val tens = groups[2]?.value?.lowercase()
            val ones = groups[3]?.value?.lowercase()
            val delimiter = matchResult.value.last()
            
            if (hundreds != null && tens != null && ones != null) {
                val total = 1000 + (hundredsNumbers[hundreds] ?: 0) + (basicNumbers[tens] ?: 0) + (basicNumbers[ones] ?: 0)
                result = result.replace(matchResult.value, "$total$delimiter")
            }
        }
        
        // Patrón con delimitador: "mil doscientos treinta," (sin unidades)
        val thousandsHundredsTensPattern = Regex(
            "\\bmil\\s+(doscientos|trescientos|cuatrocientos|quinientos|seiscientos|sietecientos|ochocientos|novecientos)\\s+(treinta|cuarenta|cincuenta|sesenta|setenta|ochenta|noventa)\\s*[,.]",
            RegexOption.IGNORE_CASE
        )
        
        thousandsHundredsTensPattern.findAll(result).forEach { matchResult ->
            val groups = matchResult.groups
            val hundreds = groups[1]?.value?.lowercase()
            val tens = groups[2]?.value?.lowercase()
            val delimiter = matchResult.value.last()
            
            if (hundreds != null && tens != null) {
                val total = 1000 + (hundredsNumbers[hundreds] ?: 0) + (basicNumbers[tens] ?: 0)
                result = result.replace(matchResult.value, "$total$delimiter")
            }
        }
        
        // Patrón con delimitador: "mil doscientos," (solo centenas)
        val thousandsHundredsPattern = Regex(
            "\\bmil\\s+(doscientos|trescientos|cuatrocientos|quinientos|seiscientos|sietecientos|ochocientos|novecientos)\\s*[,.]",
            RegexOption.IGNORE_CASE
        )
        
        thousandsHundredsPattern.findAll(result).forEach { matchResult ->
            val groups = matchResult.groups
            val hundreds = groups[1]?.value?.lowercase()
            val delimiter = matchResult.value.last()
            
            if (hundreds != null) {
                val total = 1000 + (hundredsNumbers[hundreds] ?: 0)
                result = result.replace(matchResult.value, "$total$delimiter")
            }
        }
        
        // Solo "mil" con delimitador
        val milPattern = Regex("\\bmil\\s*[,.]", RegexOption.IGNORE_CASE)
        result = milPattern.replace(result) { matchResult ->
            val delimiter = matchResult.value.last()
            "1000$delimiter"
        }
        
        return result
    }
    
    /**
     * Procesa números básicos restantes
     * Solo los que están seguidos de coma o punto
     */
    private fun processBasicNumbers(text: String): String {
        var result = text
        
        basicNumbers.forEach { (word, number) ->
            // Buscar números básicos seguidos de coma o punto
            val pattern = Regex("\\b$word\\s*[,.]", RegexOption.IGNORE_CASE)
            result = pattern.replace(result) { matchResult ->
                val delimiter = matchResult.value.last()
                "$number$delimiter"
            }
        }
        
        return result
    }
    
    /**
     * Detecta si el texto contiene números hablados
     */
    fun containsSpokenNumbers(text: String): Boolean {
        val allPatterns = listOf(
            // Números básicos
            Regex("\\b(cero|uno|dos|tres|cuatro|cinco|seis|siete|ocho|nueve|diez|once|doce|trece|catorce|quince|dieciséis|diecisiete|dieciocho|diecinueve|veinte|treinta|cuarenta|cincuenta|sesenta|setenta|ochenta|noventa|cien|ciento|mil|millón|millones)\\b", RegexOption.IGNORE_CASE),
            
            // Números compuestos
            Regex("\\b(veintiuno|veintidós|veintitrés|veinticuatro|veinticinco|veintiséis|veintisiete|veintiocho|veintinueve)\\b", RegexOption.IGNORE_CASE),
            
            // Patrones de decenas + unidades
            Regex("\\b(treinta|cuarenta|cincuenta|sesenta|setenta|ochenta|noventa)\\s+y\\s+(uno|dos|tres|cuatro|cinco|seis|siete|ocho|nueve)\\b", RegexOption.IGNORE_CASE),
            
            // Números de centenas
            Regex("\\b(doscientos|trescientos|cuatrocientos|quinientos|seiscientos|sietecientos|ochocientos|novecientos)\\b", RegexOption.IGNORE_CASE)
        )
        
        return allPatterns.any { pattern ->
            pattern.containsMatchIn(text)
        }
    }
} 