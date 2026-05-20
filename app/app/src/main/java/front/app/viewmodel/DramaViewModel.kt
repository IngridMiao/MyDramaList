package front.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import front.app.model.Drama
import front.app.model.DramaResponse
import front.app.model.Tag
import front.app.repository.DramaRepository
import front.app.repository.TagRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DramaViewModel : ViewModel() {
    private val repository = DramaRepository()
    private val tagRepository = TagRepository()

    private val _dramas = MutableStateFlow<List<Drama>>(emptyList())
    val dramas = _dramas.asStateFlow()

    private val _publicDramas = MutableStateFlow<List<DramaResponse>>(emptyList())
    val publicDramas = _publicDramas.asStateFlow()

    private val _tags = MutableStateFlow<List<Tag>>(emptyList())
    val tags = _tags.asStateFlow()

    private val _currentDrama = MutableStateFlow<Drama?>(null)
    val currentDrama = _currentDrama.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isDetailLoading = MutableStateFlow(false)
    val isDetailLoading = _isDetailLoading.asStateFlow()

    private val _hasFetchedDetail = MutableStateFlow(false)
    val hasFetchedDetail = _hasFetchedDetail.asStateFlow()

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
