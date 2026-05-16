package com.iberdrola.practicas2026.davidsc.invoices

import android.content.Context
import android.util.Log
import com.iberdrola.practicas2026.davidsc.core.utils.AppConfig
import com.iberdrola.practicas2026.davidsc.data.local.entity.InvoiceEntity
import com.iberdrola.practicas2026.davidsc.data.repository.InvoiceRepositoryImpl
import com.iberdrola.practicas2026.davidsc.utils.FakeApi
import com.iberdrola.practicas2026.davidsc.utils.FakeDao
import io.mockk.every
import io.mockk.mockkClass
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.io.File

class InvoiceRepositoryImplTest {
    private lateinit var repository: InvoiceRepositoryImpl

    private lateinit var fakeApi: FakeApi
    private lateinit var fakeDao: FakeDao
    private lateinit var fakeContext: Context

    @Before
    fun setup() {
        fakeApi = FakeApi()
        fakeDao = FakeDao()

        fakeContext = mockkClass(Context::class, relaxed = true)
        every { fakeContext.filesDir } returns File(".")
        every { fakeContext.packageName } returns "fake.package"

        repository = InvoiceRepositoryImpl(fakeApi, fakeDao, fakeContext)

        AppConfig.useMockLocal = false
    }

    @Test
    fun `getInvoices from API stores in DB and returns data`() = runTest {
        val result = repository.getInvoices()

        assertEquals(2, result.size)
        assertEquals(2, fakeDao.savedInvoices.size)
    }

    @Test
    fun `getInvoices falls back to DB when API fails`() = runTest {
        fakeApi.shouldFail = true

        fakeDao.savedInvoices = listOf(
            InvoiceEntity(99, "2026-01-01", "Cache", 10.0, "Pagada", "luz", "C/Larios")
        )

        val result = repository.getInvoices()

        assertEquals(1, result.size)
        assertEquals(99, result.first().id)
    }

}