package com.chenyuan;

/**
 * ParamsEntity
 *
 * @author <a href="mailto:natimercy93@gmail.com">natimercy</a>
 * @version 1.0.0
 */
public class ParamsEntity {

    private String name;
    private String dataType;
    private String value;

    public ParamsEntity() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDataType() {
        return this.dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String toString() {
        return "ParamsEntity{name='" + this.name + "', dataType='" + this.dataType + "', value='" + this.value + "'}";
    }

}
