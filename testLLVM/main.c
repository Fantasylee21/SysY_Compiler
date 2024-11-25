// 全局变量定义
int globalNumbers[15];
char globalCharacters[10];
int globalCount = 5;
char globalSwitch = 'M';


char classifyChar(char c) {
    if (c >= 'A' && c <= 'Z') {
        return 'U';
    } else {
        if (c >= 'a' && c <= 'z') {
            return 'L';
        } else {
            return 'N'; // 非字母字符
        }
    }
    return 'E'; // 不可达分支
}


void initializeCharacterArray(char arr[], int len) {
    int i;
    for (i = 0; i < len; i = i + 1) {
        arr[i] = 'A' + (i % 26);
    }
}


void printIntegerArray(int arr[], int len) {
    int i;
    for (i = 0; i < len; i = i + 1) {
        printf("Array[%d]: %d\n", i, arr[i]);
    }
}

// 主函数
int main() {
    int localArray[7];
    char charArray[8] = {'A', 'B', 'C'};
    int i;

    printIntegerArray(localArray, 7);

    char classified = classifyChar(charArray[0]);
    printf("Char %c classified as: %c\n", charArray[i], classified);


    return 0;
}
