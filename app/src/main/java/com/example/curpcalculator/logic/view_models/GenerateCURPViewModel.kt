package com.example.curpcalculator.logic.view_models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.curpcalculator.logic.models.FieldsCURP
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class GenerateCURPViewModel : ViewModel() {

    val flowState by lazy { MutableLiveData<GenerateCURPFlowState>() }

    fun setFlowState(state: GenerateCURPFlowState) {
        flowState.value = state
    }

    fun generateCURP(curpFields: FieldsCURP) {
        setFlowState(GenerateCURPFlowState.OnLoading)
        viewModelScope.launch {
            val curp = getCURP(curpFields)
            delay(1000)
            setFlowState(GenerateCURPFlowState.OnCURPGenerated(curp))
        }
    }

    private fun getCURP(curpFields: FieldsCURP): String {
        var curp = ""
        with(curpFields) {
            curp += primerApellido[0]
            curp += obtenerPrimeraVocal(primerApellido)
            curp += if (segundoApellido.isNullOrEmpty()) "X" else segundoApellido[0]
            curp += nombre[0]
            curp += fechaNacimiento.replace("/", "").subSequence(2, 8)
            curp += sexo
            curp += estadoNacimiento
            curp += obtenerConsonanteInterna(primerApellido)
            curp += if (segundoApellido.isNullOrEmpty()) "X" else obtenerConsonanteInterna(
                segundoApellido
            )
            curp += obtenerConsonanteInterna(nombre)
        }

        return curp
    }

    private fun obtenerConsonanteInterna(campo: String): Char {
        campo.forEachIndexed { index, c ->
            if (index != 0) {
                if (!esVocal(c)) {
                    return if (c == 'Ñ' || c == 'ñ') 'X' else c
                }
            }
        }
        return 'X'
    }

    private fun obtenerPrimeraVocal(texto: String): Char {
        texto.forEachIndexed { _, c -> if (esVocal(c)) return c }
        return 'X'
    }

    private fun esVocal(c: Char): Boolean =
        c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u' || c == 'A' || c == 'E' || c == 'I' || c == 'O' || c == 'U' || c == 'Á' || c == 'É' || c == 'Í' || c == 'Ó' || c == 'Ú' || c == 'á' || c == 'é' || c == 'í' || c == 'ó' || c == 'ú' || c == '/' || c == '-'

    sealed class GenerateCURPFlowState {
        object OnLoading : GenerateCURPFlowState()
        data class OnCURPGenerated(val curp: String) : GenerateCURPFlowState()
    }
}