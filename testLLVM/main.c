int n;

void print_move(int a, char from, char to) {
    printf("Moca move otter %d from queue %c to queue %c\n", a, from, to);
}

void move(int a[], int len, char from, char by, char to) {
    if (len == 1) {
        print_move(a[0], from, to);
        return;
    }

    move(a, len - 1, from, to, by);
    print_move(a[len - 1], from, to);
    move(a, len - 1, by, from, to);
}

int main() {
    n = 3;

    int a[200], i;
    for (i = 0; i < n; i = i + 1) {
        a[i] = i + 1;
    }
    move(a, n, 'A', 'B', 'C');
    return 0;
}
