package org.qimi.lab.dominator;

import java.util.UUID;

public class C {

    private String id;

    private D d;

    private E e;

    public C() {
        this.id = UUID.randomUUID().toString();
    }

    public void setD(D d) {
        this.d = d;
    }

    public void setE(E e) {
        this.e = e;
    }
}
