package com.redkenchi.core.graphics;

public class Screen {

    int width, height;
    int[] pixels;

    public Screen(int width, int height) {
        this.width = width;
        this.height = height;
        this.pixels = new int [width * height];
    }
}
