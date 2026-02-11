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
        std::cout << "2. 新增/修改劇集 (Add/Update)\n";
        std::cout << "3. 刪除劇集 (Delete)\n";
        std::cout << "4. 查詢劇集統計 (Query Statistics)\n";
        std::cout << "5. 儲存並離開 (Save & Exit)\n";
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
            Drama d;
            std::cout << "輸入劇名 (Title): ";
            std::getline(std::cin, d.title);
            
            std::cout << "輸入年份 (Year): ";
            while (!(std::cin >> d.year)) {
                std::cin.clear();
                std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
                std::cout << "無效年份，請重新輸入: ";
            }
            std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');

            std::cout << "輸入國家 (Country): ";
            std::getline(std::cin, d.country);

            std::cout << "輸入評分 (0.0 - 10.0): ";
            while (!(std::cin >> d.rating) || d.rating < 0 || d.rating > 10) {
                std::cin.clear();
                std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');
                std::cout << "無效評分，請重新輸入 (0-10): ";
            }
            std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');

            std::cout << "輸入評論 (Review): ";
            std::getline(std::cin, d.review);

            manager.addDrama(d);
            std::cout << "劇集已新增/更新。\n";

        } else if (choice == 3) {
            std::cout << "請輸入要刪除的劇名: ";
            std::string title;
            std::getline(std::cin, title);
            manager.deleteDrama(title);
            std::cout << "刪除操作已執行 (若劇名存在)。\n";

        } else if (choice == 4) {
            std::cout << "請輸入要查詢的劇名: ";
            std::string title;
            std::getline(std::cin, title);

            std::string result = Statistics::getRatingSummary(dramas, title);
            std::cout << "\n>>> 統計報告 <<<\n" << result << "\n";
        } else if (choice == 5) {
            std::cout << "正在儲存變更...\n";
            manager.saveToFile(dbPath);
            break;
        } else {
            std::cout << "無效選項。\n";
        }
    }

    Logger::getInstance().log(LogLevel::INFO, "Application Shutting Down.");

    return 0;
}
