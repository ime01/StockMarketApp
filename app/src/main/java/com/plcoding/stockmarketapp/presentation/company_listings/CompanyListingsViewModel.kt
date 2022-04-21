package com.plcoding.stockmarketapp.presentation.company_listings

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.plcoding.stockmarketapp.domain.repository.StockRepository
import com.plcoding.stockmarketapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CompanyListingsViewModel @Inject constructor (private val repository: StockRepository): ViewModel(){

    var state by mutableStateOf(CompanyListingsState())
    private var searchJob: Job? = null

    init {
        getCompanyListings()
    }

    fun onEvent(event: CompanyListingsEvent){
        when(event){
            is CompanyListingsEvent.Refresh ->{
                getCompanyListings(fetchFromRemote = true)
            }
            is CompanyListingsEvent.OnSearchQueryChanges ->{
                state = state.copy(searchQuery = event.query)

                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(500L)
                    getCompanyListings()

                }

            }

        }
    }


    private fun getCompanyListings(query:String =state.searchQuery.lowercase(), fetchFromRemote: Boolean = false){

        viewModelScope.launch {
            repository
                .getCompanyListings(fetchFromRemote,query)
                .collect{ result->

                    when(result){
                    is Resource.Success->{
                        result.data?.let { listings->
                            state = state.copy(
                               companies = listings
                            )
                        }
                    }

                    is Resource.Error ->{
                        println("Error is ${result.message}")
                    }
                    is Resource.Loading ->{
                        state = state.copy(isLoading = result.isLoading)
                    }

                    }

                }
        }

    }
}