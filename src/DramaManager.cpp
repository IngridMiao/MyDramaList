#include "DramaManager.h"
#include "Logger.h"
#include <fstream>
#include <sstream>
#include <iostream>
#include <algorithm>

void DramaManager::addDrama(const Drama& d) {
    // if drama exists
    for (auto& existing : dramas) {
        if (existing.title == d.title) {
            existing.rating = d.rating;
            existing.review = d.review;
            existing.year = d.year;
            existing.country = d.country;
            Logger::getInstance().log(LogLevel::INFO, "Updated drama: " + d.title);
            return;
        }
    }
    dramas.push_back(d);
    Logger::getInstance().log(LogLevel::INFO, "Added drama: " + d.title);
}

void DramaManager::deleteDrama(const std::string& title) {
    auto it = std::remove_if(dramas.begin(), dramas.end(),
                             [&](const Drama& d) { return d.title == title; });

    if (it != dramas.end()) {
        dramas.erase(it, dramas.end());
        Logger::getInstance().log(LogLevel::INFO, "Deleted drama: " + title);
    } else {
        Logger::getInstance().log(LogLevel::ERROR, "Attempted to delete non-existent drama: " + title);
    }
}

void DramaManager::loadFromFile(const std::string& filename) {
    std::ifstream file(filename);
    if (!file.is_open()) {
        Logger::getInstance().log(LogLevel::ERROR, "Failed to open file for loading: " + filename);
        return;
    }

    dramas.clear();
    std::string line;
    int lineCount = 0;
    while (std::getline(file, line)) {
        lineCount++;
        if (line.empty()) continue;

        std::stringstream ss(line);
        std::string segment;
        std::vector<std::string> parts;

        while (std::getline(ss, segment, ',')) {
            parts.push_back(segment);
        }

        // 預期格式: Title, Year, Country, Rating, Review
        // 因為 Review 可能包含逗號，所以長度可能 >= 5
        if (parts.size() >= 5) {
            try {
                Drama d;
                d.title = parts[0];
                d.year = std::stoi(parts[1]);
                d.country = parts[2];
                d.rating = std::stod(parts[3]);
                
                // 重組 Review (如果被逗號切開)
                d.review = parts[4];
                for (size_t i = 5; i < parts.size(); ++i) {
                    d.review += "," + parts[i];
                }

                dramas.push_back(d);
            } catch (const std::exception& e) {
                Logger::getInstance().log(LogLevel::ERROR, "Parse error on line " + std::to_string(lineCount) + ": " + e.what());
            }
        } else {
            Logger::getInstance().log(LogLevel::ERROR, "Invalid CSV format on line " + std::to_string(lineCount));
        }
    }
    
    file.close();
    Logger::getInstance().log(LogLevel::INFO, "Loaded " + std::to_string(dramas.size()) + " dramas from " + filename);
}

void DramaManager::saveToFile(const std::string& filename) {
    std::ofstream file(filename);
    if (!file.is_open()) {
        Logger::getInstance().log(LogLevel::ERROR, "Failed to open file for saving: " + filename);
        return;
    }

    for (const auto& drama : dramas) {
        file << drama.toCsvString() << "\n";
    }
    
    file.close();
    Logger::getInstance().log(LogLevel::INFO, "Saved " + std::to_string(dramas.size()) + " dramas to " + filename);
}

const std::vector<Drama>& DramaManager::getDramas() const {
    return dramas;
}