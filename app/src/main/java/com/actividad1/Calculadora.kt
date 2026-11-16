package com.actividad1

//Resultado de la simulación
class Resultado(
    val brutoAnual: Double,
    val brutoMensual: Double,
    val seguridadSocial: Double,
    val minimoExento: Double,
    val tipoIrpf: Double,
    val retencionIrpf: Double,
    val netoAnual: Double,
    val netoMensual: Double
)

fun calcularSalario(
    brutoAnual: Double,
    pagas: Int,
    situacion: Int,
    hijos: Int,
    indefinido: Boolean,
    discapacidad: Boolean
): Resultado {

    //Tasas de la Seguridad Social
    var tasaSS = 0.0655
    if (indefinido) {
        tasaSS = 0.0650
    }
    var baseSS = brutoAnual
    if (baseSS > 61214.40) {
        baseSS = 61214.40
    }
    val seguridadSocial = baseSS * tasaSS

    //Base del IRPF
    val baseIrpf = brutoAnual - seguridadSocial
    var minimoExento = 15876.0
    if (situacion == 1) {
        minimoExento = 17197.0
        if (hijos == 1) {
            minimoExento = 18130.0
        }
        if (hijos >= 2) {
            minimoExento = 19262.0
        }
    } else if (situacion == 2 || hijos > 0) {
        minimoExento = 17644.0
        if (hijos >= 2) {
            minimoExento = 18694.0
        }
    }

    //Retenciones IRPF
    var tipoIrpf = 47.0
    if (baseIrpf <= 12450) {
        tipoIrpf = 19.0
    } else if (baseIrpf <= 20200) {
        tipoIrpf = 24.0
    } else if (baseIrpf <= 35200) {
        tipoIrpf = 30.0
    } else if (baseIrpf <= 60000) {
        tipoIrpf = 37.0
    } else if (baseIrpf <= 300000) {
        tipoIrpf = 45.0
    }

    //En caso de no superar el mínimo no hay retención
    var retencion = baseIrpf * tipoIrpf / 100
    if (brutoAnual <= minimoExento) {
        retencion = 0.0
        tipoIrpf = 0.0
    }

    //Aquí se calcula el salario neto
    val netoAnual = brutoAnual - seguridadSocial - retencion

    return Resultado(
        brutoAnual = brutoAnual,
        brutoMensual = brutoAnual / pagas,
        seguridadSocial = seguridadSocial,
        minimoExento = minimoExento,
        tipoIrpf = tipoIrpf,
        retencionIrpf = retencion,
        netoAnual = netoAnual,
        netoMensual = netoAnual / pagas
    )
}