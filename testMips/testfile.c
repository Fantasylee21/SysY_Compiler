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




int n;

void print_move(int a, char from, char to) {
    printf("Moca move otter %d from queue %c to queue %c\n", a, from, to);
}

void move(int a[], int len, char from, char by, char to) {
    printf("%c\n", to);
}

int main() {
    n = 2;

    int a[200], i;
    for (i = 0; i < n; i = i + 1) {
        a[i] = i + 1;
    }
    move(a, n, 'A', 'B', 'C');
    return 0;
}




