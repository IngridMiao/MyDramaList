#include "Logger.h"
#include <filesystem>
#include <chrono>
#include <iomanip>
#include <sstream>

// 使用 C++17 filesystem
namespace fs = std::filesystem;

Logger& Logger::getInstance() {
    static Logger instance;
    return instance;
}

Logger::Logger() {
    // 檢查並建立 logs 目錄
    if (!fs::exists(LOG_DIR)) {
        try {
            fs::create_directories(LOG_DIR);
        } catch (const std::exception& e) {
            std::cerr << "Logger Initialization Error: " << e.what() << std::endl;
        }
    }

    // 開啟檔案 (Append 模式)
    logFile.open(LOG_FILE, std::ios::app);
    if (!logFile.is_open()) {
        std::cerr << "Logger Error: Failed to open log file at " << LOG_FILE << std::endl;
    }
}

Logger::~Logger() {
    if (logFile.is_open()) {
        logFile.close();
    }
}

void Logger::log(LogLevel level, const std::string& message) {
    // 使用 lock_guard 自動管理鎖的生命週期 (RAII)
    std::lock_guard<std::mutex> lock(logMutex);

    std::string timeStr = getCurrentTime();
    std::string levelStr = getLevelString(level);
    
    // 格式化日誌內容: [時間] [層級] 訊息
    std::stringstream ss;
    ss << "[" << timeStr << "] " << "[" << levelStr << "] " << message;
    std::string finalLog = ss.str();

    // 1. 輸出到控制台
    if (level == LogLevel::ERROR) {
        std::cerr << "\033[31m" << finalLog << "\033[0m" << std::endl; // 紅色文字 (ANSI Code)
    } else {
        std::cout << "\033[32m" << finalLog << "\033[0m" << std::endl; // 綠色文字
    }

    // 2. 輸出到檔案
    if (logFile.is_open()) {
        logFile << finalLog << std::endl;
        // 企業級應用建議適時 flush，避免崩潰時日誌遺失，但會輕微影響效能
        // logFile.flush(); 
    }
}

std::string Logger::getCurrentTime() {
    auto now = std::chrono::system_clock::now();
    auto in_time_t = std::chrono::system_clock::to_time_t(now);

    std::stringstream ss;
    // 使用 put_time 格式化時間 (YYYY-MM-DD HH:MM:SS)
    ss << std::put_time(std::localtime(&in_time_t), "%Y-%m-%d %H:%M:%S");
    return ss.str();
}

std::string Logger::getLevelString(LogLevel level) {
    switch (level) {
        case LogLevel::INFO:  return "INFO";
        case LogLevel::ERROR: return "ERROR";
        default:              return "UNKNOWN";
    }
}
