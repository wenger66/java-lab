package org.qimi.lab.dominator;

import java.util.UUID;

public class F {

    private String id;

    private H h;

    private D d;

    public F() {
        this.id = UUID.randomUUID().toString();
    }

    public void setH(H h) {
        this.h = h;
    }

    public void setD(D d) {
        this.d = d;
    }
}
