package front.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import front.app.model.User
import front.app.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val repository = UserRepository()

    private val _loginState = MutableStateFlow<LoginResult?>(null)
    val loginState = _loginState.asStateFlow()

    fun login(userName: String, password: String) {
        viewModelScope.launch {
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
                _loginState.value = LoginResult.Error("發生錯誤: ${e.message}")
            }
        }
    }

    fun register(userName: String, password: String) {
        viewModelScope.launch {
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
                _loginState.value = LoginResult.Error("發生錯誤: ${e.message}")
            }
        }
    }

    fun resetState() {
        _loginState.value = null
    }

    sealed class LoginResult {
        data class Success(val user: User) : LoginResult()
        data class Error(val message: String) : LoginResult()
    }
}
