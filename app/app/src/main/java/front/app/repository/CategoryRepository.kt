package front.app.repository

import front.app.model.Category
import front.app.network.RetrofitClient
import retrofit2.Response

class CategoryRepository {
    private val apiService = RetrofitClient.instance

    suspend fun getCategories(userId: Long): Response<List<Category>> {
        return apiService.getCategories(userId)
    }

    suspend fun saveCategory(category: Category): Response<Category> {
        return apiService.saveCategory(category)
    }
}
