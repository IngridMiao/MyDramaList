// 程式進入點
#include "Logger.h"
#include <thread>
#include <vector>

void worker(int id) {
    // 模擬多執行緒寫入
    Logger::getInstance().log(LogLevel::INFO, "Worker thread " + std::to_string(id) + " started task.");
    
    // 模擬錯誤情況
    if (id % 2 == 0) {
        Logger::getInstance().log(LogLevel::ERROR, "Worker thread " + std::to_string(id) + " encountered an error!");
    }
}

int main() {
    Logger::getInstance().log(LogLevel::INFO, "Application Starting...");

    // 測試多執行緒安全性
    std::vector<std::thread> threads;
    for (int i = 0; i < 5; ++i) {
        threads.emplace_back(worker, i);
    }

    for (auto& t : threads) {
        t.join();
    }

    Logger::getInstance().log(LogLevel::INFO, "Application Shutting Down.");

    return 0;
}
