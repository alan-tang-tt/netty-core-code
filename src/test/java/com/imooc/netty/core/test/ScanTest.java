package com.imooc.netty.core.test;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class ScanTest {
    public static void main(String[] args) throws InterruptedException, AWTException {
        Robot robot = new Robot();
        for (int i = 0; i < 10; i++) {
            robot.keyPress(KeyEvent.VK_E);
            robot.keyRelease(KeyEvent.VK_E);
        }
    }
}
