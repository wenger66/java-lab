package org.qimi.lab.dominator;

import java.util.UUID;

public class E {

    private String id;

    private G g;

    public E() {
        this.id = UUID.randomUUID().toString();
    }

    public void setG(G g) {
        this.g = g;
    }
}
