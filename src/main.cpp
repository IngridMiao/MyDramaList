// 程式進入點
#include "Logger.h"
#include "DramaManager.h"
#include "Statistics.h"
#include <iostream>
#include <limits>

#ifdef _WIN32
#include <windows.h>
#endif

int main() {
    // 設定 Windows 控制台編碼為 UTF-8 以解決亂碼問題
    #ifdef _WIN32
    SetConsoleOutputCP(65001);
    SetConsoleCP(65001);
    #endif

    Logger::getInstance().log(LogLevel::INFO, "Application Starting...");

    DramaManager manager;
    // 根據 README 結構，資料位於 data/database.csv
    std::string dbPath = "data/database.csv";
    
    std::cout << "正在載入資料庫: " << dbPath << " ..." << std::endl;
    manager.loadFromFile(dbPath);

    const auto& dramas = manager.getDramas();
    std::cout << "載入完成，共 " << dramas.size() << " 筆資料。" << std::endl;

    while (true) {
        std::cout << "\n========================================\n";
        std::cout << "       My Drama List 統計測試系統       \n";
        std::cout << "========================================\n";
        std::cout << "1. 顯示所有原始資料 (List All)\n";
        std::cout << "2. 查詢劇集統計 (Query Statistics)\n";
        std::cout << "3. 離開 (Exit)\n";
        std::cout << "請選擇: ";

        int choice;
        if (!(std::cin >> choice)) {
            std::cin.clear();
            std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
            std::cout << "無效輸入，請重試。\n";
            continue;
        }
        std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n'); // 清除換行符

        if (choice == 1) {
            if (dramas.empty()) {
                std::cout << "目前清單為空。\n";
            } else {
                std::cout << "\n[劇集清單]\n";
                for (const auto& d : dramas) {
                    std::cout << "Title: " << d.title 
                              << " | Year: " << d.year 
                              << " | Rating: " << d.rating << "\n";
                }
            }
        } else if (choice == 2) {
            std::cout << "請輸入要查詢的劇名: ";
            std::string title;
            std::getline(std::cin, title);

            std::string result = Statistics::getRatingSummary(dramas, title);
            std::cout << "\n>>> 統計報告 <<<\n" << result << "\n";
        } else if (choice == 3) {
            break;
        } else {
            std::cout << "無效選項。\n";
        }
    }

    Logger::getInstance().log(LogLevel::INFO, "Application Shutting Down.");

    return 0;
}
