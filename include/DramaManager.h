//管理增刪查改

// 檔案內容包含：
// 私有成員：一個 std::vector<Drama>，用來存放在記憶體裡的劇集清單。

// 方法宣告：
// addDrama：新增一筆劇集。
// loadFromFile：從資料庫讀取。
// saveToFile：存檔到資料庫。
// getDramas：提供資料給 Statistics 類別使用。

#ifndef DRAMAMANAGER_H
#define DRAMAMANAGER_H

#include "Drama.h"
#include <vector>
#include <string>

class DramaManager {
private:
    std::vector<Drama> dramas; // 記憶體中的資料庫

public:
    // 基礎 CRUD 功能
    void addDrama(const Drama& d);
    void loadFromFile(const std::string& filename);
    void saveToFile(const std::string& filename);

    // 唯讀介面：讓 Statistics.cpp 可以讀取資料進行運算
    const std::vector<Drama>& getDramas() const;
};

#endif