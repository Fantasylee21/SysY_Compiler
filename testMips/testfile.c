#include<stdio.h>
// ȫ�ֱ�������
int global_int;
char global_char;
int arr[10];
char str[100];
int getchar(){ char c; scanf("%c",&c); return (int)c; }
int getint(){ int t; scanf("%d",&t); while(getchar()!='\n'); return t; }
int getarray(int a[]){
    int n;
    scanf("%d",&n);
    int i = 0;
    for(i=0;i<n;i++)scanf("%d",&a[i]);
    return n;
}

// ȫ�ֱ�������
int globalNumbers[15];
char globalCharacters[10];
int globalCount = 5;
char globalSwitch = 'M';

// ��������
int computeMaximum(int arr[], int len) {
    int i;
    int maxValue = arr[0];
    for (i = 1; i < len; i = i + 1) {
        if (arr[i] > maxValue || arr[i] == maxValue && i % 2 == 0) {
            maxValue = arr[i];
        }
    }
    return maxValue;
}

char classifyChar(char c) {
    if (c >= 'A' && c <= 'Z') {
        return 'U';
    } else {
        if (c >= 'a' && c <= 'z') {
            return 'L';
        } else {
            return 'N'; // ����ĸ�ַ�
        }
    }
    return 'E'; // ���ɴ��֧
}

void adjustArray(int arr[], int len, int multiplier, int offset) {
    int i;
    for (i = 0; i < len; i = i + 1) {
        if (arr[i] % 2 == 0 || multiplier > offset && i % 3 == 0) {
            arr[i] = arr[i] * multiplier + offset;
        } else {
            arr[i] = arr[i] - offset;
        }
    }
}

int evaluateNestedConditions(int arr[], int len, char condition) {
    if (len > 10 || condition == 'A' && arr[0] != 0) {
        return computeMaximum(arr, len);
    } else {
        if (condition == 'B') {
            int sum = 0;
            int i;
            for (i = 0; i < len; i = i + 1) {
                sum = sum + arr[i];
            }
            return sum;
        } else {
            if (condition == 'C') {
                return -1;
            }
        }
    }
    return 0; // Ĭ�Ϸ���ֵ
}

void initializeCharacterArray(char arr[], int len) {
    int i;
    for (i = 0; i < len; i = i + 1) {
        arr[i] = 'A' + (i % 26);
    }
}

int specialSum(int a, int b, char flag) {
    if (flag == 'X' || flag == 'Y' && a > b) {
        return a * b;
    } else {
        return a + b;
    }
}

void printIntegerArray(int arr[], int len) {
    int i;
    for (i = 0; i < len; i = i + 1) {
        printf("Array[%d]: %d\n", i, arr[i]);
    }
}

// ������
int main() {
    int localArray[7];
    char charArray[8];
    int i;
    int result = 0;
    int maxVal = 0;

    // ��ʼ��ȫ������
    for (i = 0; i < 15; i = i + 1) {
        globalNumbers[i] = i + 5;
    }

    // ��ʼ���ֲ�����
    for (i = 0; i < 7; i = i + 1) {
        localArray[i] = i * 3;
    }

    // ��ʼ���ַ�����
    initializeCharacterArray(charArray, 8);

    // Ƕ�׿������ͺ�������
    if (globalSwitch == 'M' || globalCount < 10 && globalSwitch != 'N') {
        result = evaluateNestedConditions(globalNumbers, 15, 'A');
    } else {
        if (globalSwitch == 'P') {
            result = evaluateNestedConditions(localArray, 7, 'B');
        } else {
            result = specialSum(globalCount, result, 'X');
        }
    }

    // �������鲢��ӡ
    adjustArray(globalNumbers, 15, 2, 1);
    adjustArray(localArray, 7, 3, 2);
    printIntegerArray(globalNumbers, 15);
    printIntegerArray(localArray, 7);

    // ��ӡ�ַ�����
    for (i = 0; i < 8; i = i + 1) {
        char classified = classifyChar(charArray[i]);
        printf("Char %c classified as: %c\n", charArray[i], classified);
    }

    printf("Final Result: %d, Max Value: %d\n", result, maxVal);

    return 0;
}


