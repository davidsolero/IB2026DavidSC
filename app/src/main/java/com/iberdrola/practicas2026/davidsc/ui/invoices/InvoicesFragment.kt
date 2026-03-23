package com.iberdrola.practicas2026.davidsc.ui.invoices

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.iberdrola.practicas2026.davidsc.R
import com.iberdrola.practicas2026.davidsc.databinding.FragmentInvoicesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InvoicesFragment : Fragment() {

    private var _binding: FragmentInvoicesBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InvoicesViewModel by viewModels()

    private lateinit var adapter: InvoicesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInvoicesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupTabs()
        observeViewModel()
        viewModel.loadInvoices()
    }

    private fun setupRecyclerView() {
        adapter = InvoicesAdapter {
            showInvoiceNotAvailableDialog()
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun setupTabs() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.tab_luz)))
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(getString(R.string.tab_gas)))
    }

    private fun observeViewModel() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.skeletonLayout.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.recyclerView.visibility = if (isLoading) View.GONE else View.VISIBLE
        }

        viewModel.invoices.observe(viewLifecycleOwner) { invoices ->
            if (invoices.isNotEmpty()) {
                val last = invoices.first()
                binding.textLastInvoiceAmount.text = "%.2f€".format(last.amount)
                binding.textLastInvoiceDate.text = last.date
                binding.textLastInvoiceStatus.text = last.status
            }
            adapter.submitList(invoices)
        }
    }

    private fun showInvoiceNotAvailableDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage(getString(R.string.invoice_not_available))
            .setPositiveButton(getString(R.string.accept)) { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}