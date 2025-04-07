package com.example.model;

public class District {
    private Integer id;
    private String code;
    private String name;
    private String cityCode;

    // Getter 方法
    public Integer getId() {
        return id;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getCityCode() {
        return cityCode;
    }

    // Setter 方法
    public void setId(Integer id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }
}