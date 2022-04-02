package rahmouni.neil.counters.database

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import rahmouni.neil.counters.ResetType

class CountersListViewModel(private val repository: CountersListRepository) : ViewModel() {
    val allCounters: LiveData<List<CounterAugmented>> = repository.allCounters.asLiveData()
    fun getCounterIncrements(counterID: Int): LiveData<List<Increment>> =
        repository.getCounterIncrements(counterID).asLiveData()

    fun getCounter(counterID: Int): LiveData<CounterAugmented> =
        repository.getCounter(counterID).asLiveData()

    fun getCounterIncrementGroups(
        counterID: Int,
        resetType: ResetType
    ): LiveData<List<IncrementGroup>> =
        repository.getCounterIncrementGroups(
            counterID,
            resetType.entriesGroup1!!,
            resetType.entriesGroup2!!
        ).asLiveData()

    fun addIncrement(value: Int, counterID: Int) = viewModelScope.launch {
        repository.addIncrement(value, counterID)
    }

    fun addCounter(counter: Counter) = viewModelScope.launch {
        repository.addCounter(counter)
    }

    fun deleteIncrement(increment: Increment) = viewModelScope.launch {
        repository.deleteIncrement(increment)
    }

    fun deleteCounterById(counterID: Int) = viewModelScope.launch {
        repository.deleteCounterById(counterID)
    }

    fun updateCounter(counter: Counter) = viewModelScope.launch {
        repository.updateCounter(counter)
    }
}

class CountersListViewModelFactory(private val repository: CountersListRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CountersListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CountersListViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}