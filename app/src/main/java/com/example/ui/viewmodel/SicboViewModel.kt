package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SicboRepository
import com.example.model.FilterType
import com.example.model.PatternStats
import com.example.model.SicboPeriod
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SicboViewModel(
    private val repository: SicboRepository = SicboRepository()
) : ViewModel() {

    val periods: StateFlow<List<SicboPeriod>> = repository.periods

    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    private val _filterType = MutableStateFlow(FilterType.ALL)
    val filterType: StateFlow<FilterType> = _filterType.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isAutoRoll = MutableStateFlow(true)
    val isAutoRoll: StateFlow<Boolean> = _isAutoRoll.asStateFlow()

    private val _countdown = MutableStateFlow(45)
    val countdown: StateFlow<Int> = _countdown.asStateFlow()

    private val _isShaking = MutableStateFlow(false)
    val isShaking: StateFlow<Boolean> = _isShaking.asStateFlow()

    private val _showCustomDialog = MutableStateFlow(false)
    val showCustomDialog: StateFlow<Boolean> = _showCustomDialog.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Filtered periods for History screen
    val filteredPeriods: StateFlow<List<SicboPeriod>> = combine(
        periods,
        filterType,
        searchQuery
    ) { list, filter, query ->
        list.filter { p ->
            val matchesFilter = when (filter) {
                FilterType.ALL -> true
                FilterType.BIG -> p.isBig
                FilterType.SMALL -> p.isSmall
                FilterType.TIE -> p.isTie
                FilterType.EVEN -> p.isEven
                FilterType.ODD -> p.isOdd
                FilterType.SAME_4 -> p.same4Dice != null
            }
            val matchesQuery = query.isBlank() || p.periodNumber.contains(query.trim(), ignoreCase = true)
            matchesFilter && matchesQuery
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Statistical analysis state
    val patternStats: StateFlow<PatternStats> = periods.combine(MutableStateFlow(Unit)) { list, _ ->
        repository.computeStats(list)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), repository.computeStats(emptyList()))

    private var countdownJob: Job? = null

    init {
        startTimer()
    }

    private fun startTimer() {
        countdownJob?.cancel()
        countdownJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                if (_isAutoRoll.value) {
                    if (_countdown.value > 1) {
                        _countdown.value -= 1
                    } else {
                        _countdown.value = 45
                        triggerRoll()
                    }
                }
            }
        }
    }

    fun triggerRoll() {
        viewModelScope.launch {
            _isShaking.value = true
            delay(1200L) // Dice shaker rolling duration
            val newPeriod = repository.rollNewPeriod()
            _isShaking.value = false
            _snackbarMessage.value = "Periode ${newPeriod.periodNumber} keluar: Total ${newPeriod.total} (${if (newPeriod.isBig) "BESAR" else "KECIL"})"
        }
    }

    fun rollNowManual() {
        _countdown.value = 45
        triggerRoll()
    }

    fun toggleAutoRoll() {
        _isAutoRoll.value = !_isAutoRoll.value
        _snackbarMessage.value = if (_isAutoRoll.value) "Auto-Kocok diaktifkan (45 detik)" else "Auto-Kocok dinonaktifkan"
    }

    fun selectTab(index: Int) {
        _selectedTab.value = index
    }

    fun setFilter(filter: FilterType) {
        _filterType.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun openCustomDialog(open: Boolean) {
        _showCustomDialog.value = open
    }

    fun submitCustomPeriod(dice: List<Int>, periodNumber: String?) {
        try {
            val p = repository.addCustomPeriod(dice, periodNumber)
            _showCustomDialog.value = false
            _snackbarMessage.value = "Berhasil memasukkan periode ${p.periodNumber} (Total ${p.total})"
        } catch (e: Exception) {
            _snackbarMessage.value = "Gagal: ${e.message}"
        }
    }

    fun resetData() {
        repository.resetToSeed()
        _snackbarMessage.value = "Data berhasil direset ke 60 periode awal"
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }
}
