package com.javaexpert.intellij.plugins.eclipseclasspath;

import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * User: piotrga
 * Date: 2007-01-26
 * Time: 15:14:33
 */
public class SpeedTest extends TestCase {
    private static final int THREAD_COUNT = 10;
    private static final int OPERATIONS = 20000 * 1000 * 1 * 1;

    public void speedTest() throws InterruptedException {
        Runnable runnable = new Runnable() {
            public void run() {
                String s = "";
                double j = 1.111111111;
                for (long i = 0; i < OPERATIONS / THREAD_COUNT; i++) {
//                    s+="12345";
                    j = j * j * j * j * j * j + j + j + j + j + j;
                }
                s = "";
                System.out.println(s + j);
            }
        };
        List<Thread> t = new ArrayList<Thread>();
        for (int i = 0; i < THREAD_COUNT; i++)
            t.add(new Thread(runnable));
        for (int i = 0; i < THREAD_COUNT; i++)
            t.get(i).start();
        for (int i = 0; i < THREAD_COUNT; i++)
            t.get(i).join();

    }
}