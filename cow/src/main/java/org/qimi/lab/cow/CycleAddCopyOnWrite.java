package org.qimi.lab.cow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 循环增加COW
 */
public class CycleAddCopyOnWrite {

    // 测试对象的个数
    private static int TEST_OBJECT_SIZE = 1000000;

    // 测试对象缓存列表
    private static List<Element> cache = new CopyOnWriteArrayList<Element>();

    public static void main(String[] args) {
        initialize();
    }

    private static void initialize() {
        long start = System.currentTimeMillis();
        for(int i=0;i< TEST_OBJECT_SIZE; i++) {
            cache.add(new Element());
        }
        long end = System.currentTimeMillis();
        System.out.println("success to initialize, use "+(end-start)+" ms ");
    }

    private static void initialize2() {
        long start = System.currentTimeMillis();
        List<Element> elements = new ArrayList<Element>();
        for(int i=0;i< TEST_OBJECT_SIZE; i++) {
            elements.add(new Element());
        }
        long end = System.currentTimeMillis();
        System.out.println("success to add arraylist, use "+(end-start)+" ms ");
        cache.addAll(elements);
        end = System.currentTimeMillis();
        System.out.println("success to initialize, use "+(end-start)+" ms ");
    }
}
