package cn.itcast.erp.entity;

public class Storealert {
    private Long uuid;
    private String name;
    private Long storenum;
    private Long outnum;

    public Long getUuid() {
        return uuid;
    }

    public void setUuid(Long uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getStorenum() {
        return storenum;
    }

    public void setStorenum(Long storenum) {
        this.storenum = storenum;
    }

    public Long getOutnum() {
        return outnum;
    }

    public void setOutnum(Long outnum) {
        this.outnum = outnum;
    }
}
