//推薦統計

#ifndef STATISTICS_H
#define STATISTICS_H

#include "Drama.h"
#include <vector>
#include <string>

class Statistics {
public:
    /**
     * @brief 取得影劇的統計摘要
     * @param dramas 完整的影劇清單
     * @param title 要查詢的劇名
     * @return std::string 格式化後的結果（N < 5 顯示人數，N >= 5 顯示好評率）
     */
    static std::string getRatingSummary(const std::vector<Drama>& dramas, const std::string& title);
};

#endif