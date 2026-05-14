# My Drama List (觀影清單管理系統)

![Kotlin](https://img.shields.io/badge/Language-Kotlin-blue.svg)
![Java](https://img.shields.io/badge/Language-Java_17+-blue.svg)
![Spring Boot](https://img.shields.io/badge/Framework-Spring_Boot-brightgreen.svg)
![Android](https://img.shields.io/badge/Platform-Android-green.svg)
![Supabase](https://img.shields.io/badge/Database-Supabase-brightgreen.svg)
![License](https://img.shields.io/badge/License-MIT-green.svg)
![Status](https://img.shields.io/badge/Status-Active-brightgreen.svg)

## 📖 專案簡介 (Introduction)

**My Drama List** 是一個前端基於 **Kotlin (Android)**，後端採用 **Java Spring Boot**，並搭配 **Supabase** 作為資料庫的觀影清單管理系統。旨在幫助使用者高效地透過手機 App 記錄、管理並分析看過的劇集。

本專案不僅提供基礎的增刪查改 (CRUD) 功能，還內建了針對觀影數據的統計分析模組，特別針對樣本數不足 (N < 5) 的類別進行了特殊的加權或過濾處理，以確保評分統計的客觀性。

## ✨ 功能特色 (Features)

*   **劇集管理**：支援新增、編輯、刪除劇集資訊。
*   **多維度屬性**：
    *   📅 **年份 (Year)**：記錄劇集發行年份。
    *   🌍 **國家/地區 (Country)**：支援多國分類（如：韓劇、美劇、台劇等）。
    *   ⭐ **評分 (Rating)**：使用者自訂評分 (1-10 分)。
    *      **心得(ViewPoint)**：使用者可以文字形式留下自己對這部劇集的想法，或影響評分的幾個關鍵點。
*   **進階統計分析**：
    *   計算特定國家或年份的平均分數。
    *   **智慧統計邏輯 (N < 5)**：當特定分類下的觀影數量 (N) 小於 5 部時，系統會自動標記為「樣本不足」或採用特殊計算權重，避免極端值影響整體統計參考價值。
    *   **資料持久化與雲端同步**：透過 Supabase 進行穩定可靠的雲端數據儲存與即時同步。

## 🔮 未來擴展 (Future Roadmap)

*   **社群帳戶綁定**：未來將支援連結 LINE、Instagram 等社交平台帳戶登入。
*   **社群互動 (朋友圈)**：建立專屬的觀影朋友圈，與好友分享片單、觀影心得及互相推薦劇集。

## 🛠️ 技術棧 (Tech Stack)

*   **前端 (Frontend)**：Android (Kotlin)
*   **後端 (Backend)**：Java 17+, Spring Boot (Spring Web)
*   **資料層 (Data Layer)**：Spring Data JPA, Hibernate
*   **資料庫 (Database)**：Supabase (PostgreSQL 核心)
*   **建置工具 (Build Tool)**：Gradle (Android) / Maven 或 Gradle (Spring Boot)

## 🚀 安裝與執行 (Installation & Run)

### 前置需求
*   確保您的系統已安裝 **JDK 17** 或以上版本。
*   確保您的系統已安裝最新的 **Android Studio**。
*   準備好您的 **Supabase** 專案，並取得資料庫連線字串 (JDBC URL)。

### 下載專案
```bash
git clone https://github.com/yourusername/My-Drama-List.git
cd My-Drama-List
