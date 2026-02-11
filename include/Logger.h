#ifndef LOGGER_H
#define LOGGER_H

#include <string>
#include <fstream>
#include <mutex>
#include <iostream>

// 定義日誌層級
enum class LogLevel {
    INFO,
    ERROR
};

class Logger {
public:
    // 取得單例實例
    static Logger& getInstance();

    // 禁止複製與賦值 (Singleton 規範)
    Logger(const Logger&) = delete;
    Logger& operator=(const Logger&) = delete;

    // 主要記錄函式
    void log(LogLevel level, const std::string& message);

private:
    // 私有建構子與解構子
    Logger();
    ~Logger();

    // 取得當前時間字串
    std::string getCurrentTime();

    // 將層級轉換為字串
    std::string getLevelString(LogLevel level);

    std::ofstream logFile; // 檔案串流
    std::mutex logMutex;   // 互斥鎖，確保線程安全
    const std::string LOG_DIR = "logs";
    const std::string LOG_FILE = "logs/app.log";
};

#endif // LOGGER_H
