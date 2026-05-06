# My Drama List (觀影清單管理系統)

![Java](https://img.shields.io/badge/Language-Java_17+-blue.svg)
![Spring Boot](https://img.shields.io/badge/Framework-Spring_Boot-brightgreen.svg)
![License](https://img.shields.io/badge/License-MIT-green.svg)
![Status](https://img.shields.io/badge/Status-Active-brightgreen.svg)

## 📖 專案簡介 (Introduction)

**My Drama List** 是一個基於 **Java Spring Boot** 開發的 Web 觀影清單管理系統。旨在幫助使用者高效地透過網頁介面記錄、管理並分析看過的劇集。

本專案不僅提供基礎的增刪查改 (CRUD) 功能，還內建了針對觀影數據的統計分析模組，特別針對樣本數不足 (N < 5) 的類別進行了特殊的加權或過濾處理，以確保評分統計的客觀性。

## ✨ 功能特色 (Features)

*   **劇集管理**：支援新增、編輯、刪除劇集資訊。
*   **多維度屬性**：
    *   📅 **年份 (Year)**：記錄劇集發行年份。
    *   🌍 **國家/地區 (Country)**：支援多國分類（如：韓劇、美劇、台劇等）。
    *   ⭐ **評分 (Rating)**：使用者自訂評分 (1-10 分)。
*   **進階統計分析**：
    *   計算特定國家或年份的平均分數。
    *   **智慧統計邏輯 (N < 5)**：當特定分類下的觀影數量 (N) 小於 5 部時，系統會自動標記為「樣本不足」或採用特殊計算權重，避免極端值影響整體統計參考價值。
    *   **資料持久化**：透過關聯式資料庫 (如 MySQL / PostgreSQL) 進行穩定可靠的數據儲存。

## 🛠️ 技術棧 (Tech Stack)

*   **前端 (Frontend)**：React / Vue.js (或 Spring Thymeleaf) 搭配 Tailwind CSS
*   **後端 (Backend)**：Java 17+, Spring Boot (Spring Web)
*   **資料層 (Data Layer)**：Spring Data JPA, Hibernate
*   **資料庫 (Database)**：MySQL / PostgreSQL (開發與測試環境可使用 H2 Database)
*   **建置工具 (Build Tool)**：Maven 或 Gradle

## 🚀 安裝與執行 (Installation & Run)

### 前置需求
*   確保您的系統已安裝 **JDK 17** 或以上版本。
*   (選擇性) 安裝並設定好資料庫 (如 MySQL)，或直接使用專案預設的 H2 記憶體資料庫。
*   (若包含獨立前端) 安裝 **Node.js** 與 npm。

### 下載專案
```bash
git clone https://github.com/yourusername/My-Drama-List.git
cd My-Drama-List
