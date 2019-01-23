package org.qimi.lab.threadmc;

public class ThreadMcApp {

    public static void main(String[] args) {
        ThreadMcService service = new ThreadMcService();
        System.out.println(service.dump());
    }
}
