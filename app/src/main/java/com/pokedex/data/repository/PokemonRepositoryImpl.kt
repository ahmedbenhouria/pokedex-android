package com.pokedex.data.repository

import com.pokedex.data.remote.PokeApi
import com.pokedex.data.remote.responses.PokemonListResponse
import com.pokedex.domain.mapper.toDomainPokemonDetails
import com.pokedex.domain.model.PokemonDetails
import com.pokedex.domain.repository.PokemonRepository
import com.pokedex.util.Constants.PAGE_SIZE
import com.pokedex.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class PokemonRepositoryImpl @Inject constructor(private val api: PokeApi) : PokemonRepository {

   override suspend fun getPokemonList(curPage: Int): Resource<PokemonListResponse> {
      val response = try {
         api.getPokemonList(PAGE_SIZE, curPage * PAGE_SIZE)
      } catch(e: Exception) {
         return Resource.Error("An unknown error occurred.")
      }
      return Resource.Success(response)
   }

   override suspend fun getPokemonDetails(id: String): Resource<PokemonDetails> {
      val response = try {
         api.getPokemonDetails(id).toDomainPokemonDetails()
      } catch(e: Exception) {
         return Resource.Error("An unknown error occurred.")
      }
      return Resource.Success(response)
   }

}