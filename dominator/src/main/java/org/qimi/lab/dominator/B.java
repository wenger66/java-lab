package org.qimi.lab.dominator;

import java.util.UUID;

public class B {

    private String id;
    private C c;

    public B() {
        this.id = UUID.randomUUID().toString();
    }

    public void setC(C c) {
        this.c = c;
    }
}
