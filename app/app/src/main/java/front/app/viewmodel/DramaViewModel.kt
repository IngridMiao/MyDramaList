package front.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import front.app.model.Drama
import front.app.repository.DramaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DramaViewModel : ViewModel() {
    private val repository = DramaRepository()

    private val _dramas = MutableStateFlow<List<Drama>>(emptyList())
    val dramas = _dramas.asStateFlow()

    private val _currentDrama = MutableStateFlow<Drama?>(null)
    val currentDrama = _currentDrama.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    fun fetchDramas(userId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getDramas(userId)
                if (response.isSuccessful) {
                    _dramas.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchDrama(title: String, userId: Long) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getDrama(title, userId)
                if (response.isSuccessful) {
                    _currentDrama.value = response.body()
                }
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveDrama(drama: Drama, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = repository.saveDrama(drama)
                if (response.isSuccessful) {
                    onComplete()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun deleteDrama(title: String, userId: Long, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = repository.deleteDrama(title, userId)
                if (response.isSuccessful) {
                    onComplete()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }
}
