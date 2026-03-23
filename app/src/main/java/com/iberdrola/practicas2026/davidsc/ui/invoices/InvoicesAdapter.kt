package com.iberdrola.practicas2026.davidsc.ui.invoices

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.iberdrola.practicas2026.davidsc.databinding.ItemInvoiceBinding
import com.iberdrola.practicas2026.davidsc.domain.model.Invoice

class InvoicesAdapter(
    private val onInvoiceClick: () -> Unit
) : RecyclerView.Adapter<InvoicesAdapter.InvoiceViewHolder>() {

    private var invoices: List<Invoice> = emptyList()

    fun submitList(newInvoices: List<Invoice>) {
        invoices = newInvoices
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceViewHolder {
        val binding = ItemInvoiceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return InvoiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) {
        holder.bind(invoices[position])
    }

    override fun getItemCount(): Int = invoices.size

    inner class InvoiceViewHolder(
        private val binding: ItemInvoiceBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(invoice: Invoice) {
            binding.textDate.text = invoice.date
            binding.textDescription.text = invoice.description
            binding.textAmount.text = "%.2f€".format(invoice.amount)
            binding.textStatus.text = invoice.status
            binding.textStatus.setBackgroundColor(getStatusColor(invoice.status))
            binding.root.setOnClickListener { onInvoiceClick() }
        }

        private fun getStatusColor(status: String): Int {
            return when (status) {
                "Pagada" -> 0xFF4CAF50.toInt()
                "Pendiente de Pago" -> 0xFFFF5722.toInt()
                "En trámite de cobro" -> 0xFFFF9800.toInt()
                "Anulada" -> 0xFF9E9E9E.toInt()
                "Cuota Fija" -> 0xFF2196F3.toInt()
                else -> 0xFF9E9E9E.toInt()
            }
        }
    }
}