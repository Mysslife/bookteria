package com.devteria.gateway;

public class Test {
    public static void main(String[] args) {
        int[] arr = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        int n = 6;
        n = arr[arr[6] / 2];

        System.out.println("n: " + n);
        System.out.println((float) arr[n] / 2);
    }
}
