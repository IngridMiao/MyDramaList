# 編譯器設定
CXX = g++

# 編譯選項
# -std=c++17: 專案使用 filesystem，需 C++17
# -Iinclude: 讓編譯器在 include 資料夾尋找標頭檔
# -Wall -Wextra: 顯示警告訊息，保持代碼品質
CXXFLAGS = -std=c++17 -Iinclude -Wall -Wextra

# 執行檔名稱
TARGET = drama_server

# 原始碼檔案
SRCS = src/main.cpp src/Logger.cpp

# 物件檔案 (將 .cpp 換成 .o)
OBJS = $(SRCS:.cpp=.o)

# 預設目標
all: $(TARGET)

# 連結
$(TARGET): $(OBJS)
	$(CXX) $(CXXFLAGS) -o $(TARGET) $(OBJS)

# 編譯
%.o: %.cpp
	$(CXX) $(CXXFLAGS) -c $< -o $@

# 清除
clean:
	rm -f $(OBJS) $(TARGET)

.PHONY: all clean
