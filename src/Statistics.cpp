// 統計邏輯實作
#include "Statistics.h"
#include <iostream>
#include <vector>
#include <string>
#include <sstream>

std::string Statistics::getRatingSummary(const std::vector<Drama>& dramas, const std::string& title) {
    int count = 0;
    int recommendedCount = 0; // 符合「值得一看」以上的數量
    double totalRating = 0.0;

    // 圓餅圖數據統計
    int superCount = 0; // >= 0.9
    int worthCount = 0; // >= 0.7
    int okayCount = 0;  // >= 0.5
    int wasteCount = 0; // < 0.5

    for (const auto& d : dramas) {
        if (d.title == title) {
            count++;
            totalRating += d.rating;

            if (d.rating >= 0.9) superCount++;
            else if (d.rating >= 0.7) worthCount++;
            else if (d.rating >= 0.5) okayCount++;
            else wasteCount++;

            // 根據你的新邏輯：rating >= 0.7 算入推薦率 (包含值得一看與超級推薦)
            if (d.rating >= 0.7) {
                recommendedCount++;
            }
        }
    }

    if (count == 0) return "尚未有評分紀錄";

    // 邏輯 A：人數小於 5 筆，顯示具體人數與最新一筆的評價等級
    if (count < 5) {
        // 找最後一筆評分來給出相對評價
        double latestRating = 0;
        for (const auto& d : dramas) {
            if (d.title == title) latestRating = d.rating;
        }

        std::string level;
        if (latestRating >= 0.9) level = "超級推薦";
        else if (latestRating >= 0.7) level = "值得一看";
        else if (latestRating >= 0.5) level = "看也行不看也不可惜";
        else level = "別浪費時間";

        return "目前有 " + std::to_string(count) + " 人評價，最新評價為：【" + level + "】";
    } 
    // 邏輯 B：人數大於等於 5 筆，顯示推薦率
    else {
        int percent = (int)((double)recommendedCount / count * 100);
        
        // 計算平均分以決定綜合評價等級
        double avgRating = totalRating / count;
        std::string level;
        if (avgRating >= 0.9) level = "超級推薦";
        else if (avgRating >= 0.7) level = "值得一看";
        else if (avgRating >= 0.5) level = "看也行不看也不可惜";
        else level = "別浪費時間";

        std::stringstream ss;
        ss << "大眾推薦率：" << percent << "% (基於 " << count << " 人評價) - 綜合評價：【" << level << "】\n";
        ss << "評分分佈: 超級推薦(" << superCount << ") 值得一看(" << worthCount 
           << ") 普通(" << okayCount << ") 差評(" << wasteCount << ")";
        
        return ss.str();
    }
}