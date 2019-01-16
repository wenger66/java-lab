package org.qimi.lab.dominator;

import java.util.UUID;

public class D {

    private String id;

    private F f;

    public D() {
        this.id = UUID.randomUUID().toString();
    }

    public void setF(F f) {
        this.f = f;
    }
}
