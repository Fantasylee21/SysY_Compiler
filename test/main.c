
const int aaaaaa = 100, bbbbbb = 200, cccccc = 300;
const int MAX = 1000000;
const char MAX_CHAR = 'z';
const int dp[20]= {1,2,4};
const char dp1[20]= {'a','b','c'};
const char fas[7]= "dsadsa";


int isPrime(int n) {
	if (n <= 1) return 0;
	int i;
	for (i = 2; i * i <= n; i=i+1) {
		if (n % i == 0) return 0;
	}
	return 1;
}

char getAChar() {
	return 'a';
}

int getSum(int a, int b, int c) {
	return a + b + c;
}

int getSum1(int a, int b, int c) {
	return a + b / c;
}

char getAChar1() {
	return getAChar();
}

void sumOne2Ten() {
	int sum = 0;
	int i;
	for (i = 1; i <= 100; i=i+1) {
		sum = sum + i;
	}
	printf("%d\n", sum);
}

void sumOne2Ten1() {
	int sum = 0;
	int i;
	for (i = 1; i <= 100; i=i+1) {
		sum = sum + i;
	}
	if (sum > 0) {
		return;
	}
	printf("%d\n", sum);
}

void BubbleSort(int arr[], int len) {
	int i,j;
	for (i = 0; i < len; i=i+1) {
		int flag = 0;
		for (j = 0; j < len - i - 1; j=j+1) {
			if (arr[j] > arr[j+1]) {
				int temp = arr[j];
				arr[j] = arr[j+1];
				arr[j+1] = temp;
				flag = 1;
			}
		}
		if (flag == 0) break;
	}
}

int main()
{
	const char love = 'l';
	const int age = 20;
	int aaaaaa[100];
	char bbbbb[7] = "dsadsa";
	int newBestScoreF;
	newBestScoreF = getint();
	char newBestScoreA;
	newBestScoreA = getchar();
	int j,k;
	k = 0;
	j = k;
	j = !j;
	int now = 0, pre = 0, temp = 0;
	char nowChar = 'a', preChar = 'a', tempChar = 'a';
	printf("22371147\n");
	printf("%d %c\n", newBestScoreF, newBestScoreA);
	int i;
	int cnt = 0;
	for (i = 0; i < 100; i=i+1) {

		if (isPrime(i)) {
			cnt = cnt + 1;
		}
	}
	i = 0;
	{

	}
	for (; i < 100; i=i+1) {
		if (isPrime(i)) {
			cnt = cnt + 1;
		}
	}
	i = 0;
	for (;;i = i + 1) {
		const int iMax = 100;
		if (i >= iMax) break;
		if (isPrime(i)) {
			cnt = cnt + 1;
		}
	}
	for (i = 0;;i=i+1) {
		if (i >= 100) break;
		if (isPrime(i)) {
			cnt = cnt + 1;
		}
	}
	for (i = 0; i < 100;) {
		if (isPrime(i)) {
			cnt = cnt + 1;
		}
		i = i + 1;
	}
	for (i = 0;;) {
		if (i >= 100) break;
		if (isPrime(i)) {
			cnt = cnt + 1;
		}
		i = i + 1;
	}
	i = 0;
	for (;;) {
		if (i >= 100) break;
		if (isPrime(i)) {
			cnt = cnt + 1;
		}
		i = i + 1;
	}
	i = 0;
	for (;i < 100;) {
		if (isPrime(i)) {
			cnt = cnt + 1;
		}
		i = i + 1;
	}
	i = 0;
	for (i = 0; i < 100; i = i + 1) {
		if (i > 50) {
			continue;
		}
	}
	for (i = 0; i < 100; i = i + 1) {
		if (i > 50) {
			cnt = cnt + 1;
			break;;
		}
	}
	// cnt = cnt + 1;

	/*
	cnt = cnt + 10;
	cnt = cnt + 102;
	*/
	cnt = cnt * 10;
	cnt = cnt / 2;
	cnt = cnt % 3;
	cnt = (cnt + (4*78 / 2)) -99;
	if (cnt > 0) {
		cnt = -cnt;
	} else {
		cnt = cnt;
	}
	if (cnt < 0) {
		cnt = -cnt;
	} else {
		cnt = cnt;
	}
	if (cnt != 0) {
		cnt = cnt;
	}
	char x = 'x';
	if (x >= 'a') {
		x = x + 1;
	}
	if (x <= 'z') {
		x = x + 1;
	}
	if (x != 'z') {
		x = x - 1;
	}
	if (x == 'z') {
		x = x - 10;
	}
	{
		int x = 10;
		x = x + 1;
		printf("%d\n", x);
	}
	if (cnt != 0) {
		cnt = getSum(1, 2, cnt);
	}
	if ((2*3/4 + 5 - 6) * 7 > 0) {
		cnt = cnt + 1;
	}
	int ans = cnt;
	int ans1 = (2*3/4 + 5 - 6) * 7;
	printf("%d %c %d %c %c %d %c %d %d %d %d\n", cnt, love, age, getAChar(),getAChar1(), getSum(1, 2, age),x, cnt, ans, ans1, getSum1(1, 2, 3));
	sumOne2Ten();
	sumOne2Ten1();
	int ans2 = cnt;
	if (ans2 > 0 && ans2 < 100) {
		if (ans2 % 2 == 0 || ans2 % 3 == 0) {
			cnt = cnt + 1;
		}
		if ( ans2 % 5 == 0 || isPrime(ans2)) {
			cnt = cnt + 1;
		}
	}
	printf("%d\n", ans2);
	int arr[30] = {-1,-2,3,34,5,16,7,8,-9,10,131,-12,13,124,15,16,137,-18,19};
	int gg = 0;
	gg = ++gg;
	int len = 20;
	for (i = 0; i < len; i=i+1) {
		int flag = 0;
		for (j = 0; j < len - i - 1; j=j+1) {
			if (arr[j] > arr[j+1]) {
				int temp = arr[j];
				arr[j] = arr[j+1];
				arr[j+1] = temp;
				flag = 1;
			}
		}
		if (flag == 0) break;
	}
	printf("%d%d%d\n", arr[0], arr[1], arr[2]);
	return 0;
}
