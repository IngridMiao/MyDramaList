package front.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import front.app.model.User
import front.app.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import java.io.InterruptedIOException

class LoginViewModel : ViewModel() {
    private val repository = UserRepository()

    private val _loginState = MutableStateFlow<LoginResult?>(null)
    val loginState = _loginState.asStateFlow()

    fun login(userName: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginResult.Loading
            try {
                val response = repository.login(User(userName = userName, password = password))
                if (response.isSuccessful) {
                    _loginState.value = LoginResult.Success(response.body()!!)
                } else {
                    val message = when (response.code()) {
                        404 -> "查無此帳號"
                        401 -> "密碼錯誤"
                        else -> "登入失敗: ${response.code()}"
                    }
                    _loginState.value = LoginResult.Error(message)
                }
            } catch (e: Exception) {
                _loginState.value = LoginResult.Error(networkErrorMessage(e))
            }
        }
    }

    fun register(userName: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginResult.Loading
            try {
                val response = repository.createUser(User(userName = userName, password = password))
                if (response.isSuccessful) {
                    _loginState.value = LoginResult.Success(response.body()!!)
                } else {
                    val message = when (response.code()) {
                        409 -> "此帳號已存在"
                        400 -> "註冊資料不完整"
                        else -> "註冊失敗: ${response.code()}"
                    }
                    _loginState.value = LoginResult.Error(message)
                }
            } catch (e: Exception) {
                _loginState.value = LoginResult.Error(networkErrorMessage(e))
            }
        }
    }

    private fun networkErrorMessage(e: Exception): String = when (e) {
        // SocketTimeoutException、callTimeout 都屬於 InterruptedIOException
        is InterruptedIOException -> "伺服器回應逾時（可能正在喚醒），請再按一次登入"
        is IOException -> "無法連線到伺服器，請確認網路後再試"
        else -> "發生錯誤: ${e.message}"
    }

    fun resetState() {
        _loginState.value = null
    }

    sealed class LoginResult {
        object Loading : LoginResult()
        data class Success(val user: User) : LoginResult()
        data class Error(val message: String) : LoginResult()
    }
}
