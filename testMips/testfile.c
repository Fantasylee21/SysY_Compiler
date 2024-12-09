#include<stdio.h>
// 全局变量定义
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

int getIndex(int arr[], int index) {
    return arr[index];
}

int setIndex(int arr[], int index, int val) {
    int temp = arr[index];
    arr[index] = val;
    return temp;
}

int main() {
    int arr1[5] = {0, 1, 2, 3, 4};
    int arr2[5] = {0, 0, 0, 0, 0};
    printf("%d\n", setIndex(arr2, 0, getIndex(arr1, 0) + 0));
    printf("%d\n", setIndex(arr2, 1, getIndex(arr1, 1) + 1));
    printf("%d\n", setIndex(arr2, 2, getIndex(arr1, 2) + 2));
    printf("%d\n", setIndex(arr2, 3, getIndex(arr1, 3) + 3));
    printf("%d\n", setIndex(arr2, 4, getIndex(arr1, 4) + 4));
    return 0;
}



