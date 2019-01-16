package org.qimi.lab.dominator;

import java.util.UUID;

public class G {

    private String id;

    private H h;

    public G() {
        this.id = UUID.randomUUID().toString();
    }

    public void setH(H h) {
        this.h = h;
    }
}
