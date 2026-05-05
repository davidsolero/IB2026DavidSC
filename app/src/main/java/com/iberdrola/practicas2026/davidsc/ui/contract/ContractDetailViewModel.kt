package com.iberdrola.practicas2026.davidsc.ui.contract

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.iberdrola.practicas2026.davidsc.domain.model.Contract
import com.iberdrola.practicas2026.davidsc.domain.usecase.GetContractsUseCase
import com.iberdrola.practicas2026.davidsc.domain.usecase.UpdateContractEmailUseCase
import com.iberdrola.practicas2026.davidsc.ui.util.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import jakarta.inject.Named
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@HiltViewModel
class ContractDetailViewModel @Inject constructor(
    private val getContractsUseCase: GetContractsUseCase,
    private val updateContractEmailUseCase: UpdateContractEmailUseCase,
    @Named("io") private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _contract = MutableStateFlow<Contract?>(null)
    val contract: StateFlow<Contract?> = _contract.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _legalChecked = MutableStateFlow(false)
    val legalChecked: StateFlow<Boolean> = _legalChecked.asStateFlow()

    val canContinue: StateFlow<Boolean> = combine(_email, _legalChecked) { email, checked ->
        isValidEmail(email) && checked
    }.let { flow ->
        val state = MutableStateFlow(false)
        viewModelScope.launch {
            flow.collect { state.value = it }
        }
        state
    }

    fun loadContract(contractId: String) {
        if (_contract.value?.id == contractId) {
            Log.d(TAG, "loadContract: guard hit — id=$contractId email=${_contract.value?.email}")
            return
        }

        viewModelScope.launch(ioDispatcher) {
            _isLoading.value = true
            try {
                val loaded = getContractsUseCase().firstOrNull { it.id == contractId }
                Log.d(TAG, "loadContract: loaded — id=$contractId email=${loaded?.email}")
                _contract.value = loaded
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun commitEmail(newEmail: String) {
        val contractId = _contract.value?.id
        Log.d(
            TAG,
            "commitEmail called — contractId=$contractId newEmail=$newEmail instance=${
                System.identityHashCode(this)
            }"
        )

        if (contractId == null) {
            Log.e(TAG, "commitEmail: _contract is null, nothing to update")
            return
        }

        viewModelScope.launch(ioDispatcher) {
            updateContractEmailUseCase(contractId, newEmail)
            _contract.value = _contract.value?.copy(email = newEmail)
            Log.d(TAG, "commitEmail: done — _contract.email is now ${_contract.value?.email}")
        }
    }

    fun onEmailChange(value: String) {
        _email.value = value
    }

    fun onLegalCheckedChange(value: Boolean) {
        _legalChecked.value = value
    }

    companion object {
        private const val TAG = "ContractDetailVM"
    }
}