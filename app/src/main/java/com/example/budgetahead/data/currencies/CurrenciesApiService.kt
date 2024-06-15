package com.example.budgetahead.data.currencies
import kotlinx.serialization.Serializable
import retrofit2.http.GET
import retrofit2.http.Query


interface CurrenciesApiService {

    @GET("rates/latest")
    suspend fun getCurrencies(@Query("apikey") apiKey: String) : CurrenciesResponse
}


@Serializable
data class CurrenciesResponse(
    val date: String,
    val base: String,
    val rates: Map<String, String>
)

