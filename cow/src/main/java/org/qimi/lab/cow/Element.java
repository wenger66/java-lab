package org.qimi.lab.cow;
import java.util.UUID;

public class Element {
    private String uuid;
    private String uuid2;
    private String uuid3;
    private String uuid4;
    private String uuid5;

    public Element() {
        this.uuid = UUID.randomUUID().toString();
        this.uuid2 = UUID.randomUUID().toString();
        this.uuid3 = UUID.randomUUID().toString();
        this.uuid4 = UUID.randomUUID().toString();
        this.uuid5 = UUID.randomUUID().toString();
    }


    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid2() {
        return uuid2;
    }

    public void setUuid2(String uuid2) {
        this.uuid2 = uuid2;
    }

    public String getUuid3() {
        return uuid3;
    }

    public void setUuid3(String uuid3) {
        this.uuid3 = uuid3;
    }

    public String getUuid4() {
        return uuid4;
    }

    public void setUuid4(String uuid4) {
        this.uuid4 = uuid4;
    }

    public String getUuid5() {
        return uuid5;
    }

    public void setUuid5(String uuid5) {
        this.uuid5 = uuid5;
    }
}
