package org.qimi.lab.dominator;

import java.util.UUID;

public class A {

    private String id;

    private C c;

    public A() {
        this.id = UUID.randomUUID().toString();
    }

    public void setC(C c) {
        this.c = c;
    }
}
