package com.uniminuto.clinica.models;

import lombok.Data;

@Data
public class MenuRS {

    private Integer menuId;
    private String code;
    private String title;
    private String url;
    private String icon;
}
