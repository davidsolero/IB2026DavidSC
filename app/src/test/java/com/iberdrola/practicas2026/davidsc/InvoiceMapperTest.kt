package com.iberdrola.practicas2026.davidsc

import com.iberdrola.practicas2026.davidsc.data.mapper.toDomain
import com.iberdrola.practicas2026.davidsc.data.remote.dto.InvoiceDto
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class InvoiceMapperTest {

    @Test
    fun `InvoiceDto with type luz maps to domain with type LUZ`() {
        val dto = InvoiceDto(
            id = 1,
            startDate = "2026-01-01",
            endDate = "2026-01-31",
            description = "Factura Luz",
            amount = 52.3,
            status = "Pagada",
            type = "luz",
            street = "C/Larios"
        )

        val result = dto.toDomain()

        assertEquals(InvoiceType.LUZ, result.type)
        assertEquals(1, result.id)
        assertEquals(52.3, result.amount, 0.0)
    }

    @Test
    fun `InvoiceDto with unknown type throws IllegalArgumentException`() {
        val dto = InvoiceDto(
            id = 2,
            startDate = "2026-01-01",
            endDate = "2026-01-31",
            description = "Factura desconocida",
            amount = 10.0,
            status = "Pagada",
            type = "agua",
            street = "C/Larios"
        )

        assertThrows(IllegalArgumentException::class.java) {
            dto.toDomain()
        }
    }
}