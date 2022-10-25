package com.example.curpcalculator.logic.models

import java.io.Serializable

data class FieldsCURP(
    val nombre: String,
    val primerApellido: String,
    val segundoApellido: String? = null,
    val sexo: Char,
    val fechaNacimiento: String,
    val estadoNacimiento: String
): Serializable
