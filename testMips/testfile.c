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




int main()
{
	int i;
	int n;
	int max = 0, maxn = 0;
	n=2;
	for (i = 0; i < n; i=i+1)
	{
		if (maxn < max)
			maxn = max;
	}
	printf("%d", maxn);
	return 0;
}
