package com.example.kidcare.data

import org.junit.Assert.assertEquals
import org.junit.Test

class RutUtilTest {

    // ─── calcularDV ───────────────────────────────────────────────────────────

    @Test
    fun calcularDV_rutConocido_retornaDVCorrecto() {
        // 11.111.111-1 es un RUT válido ampliamente conocido
        assertEquals("1", calcularDV("11111111"))
    }

    @Test
    fun calcularDV_rutSieteDigitos_retornaDVCorrecto() {
        // 5.126.663-3: verificado con el algoritmo módulo 11
        assertEquals("3", calcularDV("5126663"))
    }

    @Test
    fun calcularDV_rutOchoDigitos_retornaDVCorrecto() {
        // 12.345.678-5: DV calculado = 5
        assertEquals("5", calcularDV("12345678"))
    }

    @Test
    fun calcularDV_dvEsK_retornaK() {
        // 8.888.888-K: suma = 232, 232%11=1, 11-1=10 → "K"
        assertEquals("K", calcularDV("8888888"))
    }

    @Test
    fun calcularDV_dvEsCero_retornaCero() {
        // 2.100.000-0: suma = 11, 11%11=0, 11-0=11 → "0"
        assertEquals("0", calcularDV("2100000"))
    }

    @Test
    fun calcularDV_cuerpoVacio_retornaVacio() {
        assertEquals("", calcularDV(""))
    }

    @Test
    fun calcularDV_cuerpoConLetras_retornaVacio() {
        assertEquals("", calcularDV("abc"))
    }

    @Test
    fun calcularDV_cuerpoMixto_retornaVacio() {
        assertEquals("", calcularDV("123abc"))
    }

    // ─── formatearRut ─────────────────────────────────────────────────────────

    @Test
    fun formatearRut_ochoDigitosMasDV_formataCorrecto() {
        // "123456785" → "12.345.678-5"
        assertEquals("12.345.678-5", formatearRut("123456785"))
    }

    @Test
    fun formatearRut_sieteDigitosMasDV_formataCorrecto() {
        // "51266633" → "5.126.663-3"
        assertEquals("5.126.663-3", formatearRut("51266633"))
    }

    @Test
    fun formatearRut_soloUnCaracter_noFormatea() {
        assertEquals("1", formatearRut("1"))
    }

    @Test
    fun formatearRut_dosCaracteres_devuelveConGuion() {
        assertEquals("1-2", formatearRut("12"))
    }
}
