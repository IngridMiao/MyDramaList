package front.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import front.app.model.*
import front.app.repository.DramaRepository
import front.app.repository.TagRepository
import front.app.network.TmdbRetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

class DramaViewModel : ViewModel() {
    private val repository = DramaRepository()
    private val tagRepository = TagRepository()
    private val userRepository = front.app.repository.UserRepository()
    private val tmdbService = TmdbRetrofitClient.instance

    private val _dramas = MutableStateFlow<List<Drama>>(emptyList())
    val dramas = _dramas.asStateFlow()

    private val _publicDramas = MutableStateFlow<List<DramaResponse>>(emptyList())
    val publicDramas = _publicDramas.asStateFlow()

    private val _tags = MutableStateFlow<List<Tag>>(emptyList())
    val tags = _tags.asStateFlow()

    private val _currentDrama = MutableStateFlow<Drama?>(null)
    val currentDrama = _currentDrama.asStateFlow()

    private val _suggestions = MutableStateFlow<List<DramaSuggestion>>(emptyList())
    val suggestions = _suggestions.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isDetailLoading = MutableStateFlow(false)
    val isDetailLoading = _isDetailLoading.asStateFlow()

    private val _hasFetchedDetail = MutableStateFlow(false)
    val hasFetchedDetail = _hasFetchedDetail.asStateFlow()

    private val _pendingRequests = MutableStateFlow<List<User>>(emptyList())
    val pendingRequests = _pendingRequests.asStateFlow()

    fun fetchPendingRequests(userId: Long) {
        viewModelScope.launch {
            try {
                val response = userRepository.getFriendRequests(userId)
                if (response.isSuccessful) {
                    _pendingRequests.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                // Error handling
            }
        }
    }

    fun acceptFriendRequest(userId: Long, requesterId: Long, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = userRepository.acceptFriendRequest(userId, requesterId)
                if (response.isSuccessful) {
                    fetchPendingRequests(userId)
                    onComplete()
                }
            } catch (e: Exception) {
                // Error handling
            }
        }
    }

    fun declineFriendRequest(userId: Long, requesterId: Long, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = userRepository.declineFriendRequest(userId, requesterId)
                if (response.isSuccessful) {
                    fetchPendingRequests(userId)
                    onComplete()
                }
            } catch (e: Exception) {
                // Error handling
            }
        }
    }

    fun addFriend(userId: Long, friendUserName: String, onResult: (String?) -> Unit) {
        viewModelScope.launch {
            try {
                val response = userRepository.addFriend(userId, friendUserName)
                if (response.isSuccessful) {
                    onResult(null) // Success
                } else {
                    onResult(response.errorBody()?.string() ?: "新增失敗")
                }
            } catch (e: Exception) {
                onResult("發生錯誤: ${e.message}")
            }
        }
    }

    fun searchTmdb(query: String) {
        if (query.isBlank()) {
            _suggestions.value = emptyList()
            return
        }

        viewModelScope.launch {
            try {
                val searchResponse = tmdbService.searchMulti(query, TmdbRetrofitClient.API_KEY)
                if (searchResponse.isSuccessful) {
                    val results = searchResponse.body()?.results?.take(5) ?: emptyList()
                    val suggestionList = results.map { result ->
                        async {
                            val creditsResponse = if (result.mediaType == "tv") {
                                tmdbService.getTvCredits(result.id, TmdbRetrofitClient.API_KEY)
                            } else {
                                tmdbService.getMovieCredits(result.id, TmdbRetrofitClient.API_KEY)
                            }
                            
                            val actors = if (creditsResponse.isSuccessful) {
                                creditsResponse.body()?.cast?.take(3)?.map { it.name } ?: emptyList()
                            } else {
                                emptyList()
                            }
                            
                            DramaSuggestion(
                                title = result.displayTitle,
                                actors = actors,
                                tmdbId = result.id,
                                posterPath = result.posterPath
                            )
                        }
                    }.awaitAll()
                    _suggestions.value = suggestionList
                }
            } catch (e: Exception) {
                _suggestions.value = emptyList()
            }
        }
    }

    fun clearSuggestions() {
        _suggestions.value = emptyList()
    }

    fun fetchDramas(userId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getDramas(userId)
                if (response.isSuccessful) {
                    _dramas.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                // Error handling
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchFriendsDramas(userId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getFriendsDramas(userId)
                if (response.isSuccessful) {
                    _publicDramas.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                // Error handling
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchPublicDramas() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getPublicDramas()
                if (response.isSuccessful) {
                    _publicDramas.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                // Error handling
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchTags(userId: Long) {
        viewModelScope.launch {
            try {
                val response = tagRepository.getTags(userId)
                if (response.isSuccessful) {
                    _tags.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                // Error handling
            }
        }
    }

    fun saveTag(tag: Tag, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = tagRepository.saveTag(tag)
                if (response.isSuccessful) {
                    fetchTags(tag.userId)
                    onComplete()
                }
            } catch (e: Exception) {
                // Error handling
            }
        }
    }

    fun fetchDrama(title: String, userId: Long) {
        viewModelScope.launch {
            _hasFetchedDetail.value = false
            _isDetailLoading.value = true
            _currentDrama.value = null 
            try {
                val response = repository.getDrama(title, userId)
                if (response.isSuccessful) {
                    _currentDrama.value = response.body()
                }
            } catch (e: Exception) {
                // Error handling
            } finally {
                _isDetailLoading.value = false
                _hasFetchedDetail.value = true
            }
        }
    }

    fun saveDrama(drama: Drama, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = repository.saveDrama(drama)
                if (response.isSuccessful) {
                    // Refresh and wait for completion before going back
                    val refreshResponse = repository.getDramas(drama.userId)
                    if (refreshResponse.isSuccessful) {
                        _dramas.value = refreshResponse.body() ?: emptyList()
                    }
                    onComplete()
                }
            } catch (e: Exception) {
                // Error handling
            }
        }
    }

    fun deleteDrama(title: String, userId: Long, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = repository.deleteDrama(title, userId)
                if (response.isSuccessful) {
                    // Refresh and wait for completion before going back
                    val refreshResponse = repository.getDramas(userId)
                    if (refreshResponse.isSuccessful) {
                        _dramas.value = refreshResponse.body() ?: emptyList()
                    }
                    onComplete()
                }
            } catch (e: Exception) {
                // Error handling
            }
        }
    }
}
