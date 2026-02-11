// 劇集物件實作

// 檔案內容包含：
// 欄位宣告：劇名、年份、國家、評分、評論。
// CSV 轉換函式：方便將資料轉成一行文字存檔。


#ifndef DRAMA_H
#define DRAMA_H

#include <string>

struct Drama {
    std::string title;
    int year;
    std::string country;
    double rating;
    std::string review;

    // 將資料轉為 CSV 格式的一行文字
    std::string toCsvString() const {
        return title + "," + std::to_string(year) + "," + 
               country + "," + std::to_string(rating) + "," + review;
    }
};

#endif