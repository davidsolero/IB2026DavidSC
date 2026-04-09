package com.iberdrola.practicas2026.davidsc.invoices

import com.iberdrola.practicas2026.davidsc.data.mapper.toDomainOrNull
import com.iberdrola.practicas2026.davidsc.data.remote.dto.InvoiceDto
import com.iberdrola.practicas2026.davidsc.domain.model.InvoiceType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
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

        val result = dto.toDomainOrNull()
        assertNotNull(result)
        result?.let {
            assertEquals(InvoiceType.LUZ, it.type)
            assertEquals(1, it.id)
            assertEquals(52.3, it.amount, 0.0)
        }
    }

    @Test
    fun `InvoiceDto with unknown type returns null`() {
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

        val result = dto.toDomainOrNull()
        assertNull(result)
    }

}