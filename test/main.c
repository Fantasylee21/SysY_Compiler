void print_str(char str[]) {
    int i = 0;
    for (; str[i] != '\0'; i = i + 1)
    {
        printf("%c", str[i]);
    }
}

int main()
{
    char ch[15] = "Hello, world!\n";
    print_str(ch);
    return 0;
}