package com.dearzs.app.entity;

import java.io.Serializable;
import java.util.List;

/**
 * 疾病分类实体类（大类）
 * @author 鲁延龙
 * @version 1.0
 */
public class EntityDiseaseDpmtInfo implements Serializable {
    private String department;
    private List<EntityDiseaseType> types;
    private String[] list;

    public String[] getList() {
        return list;
    }

    public void setList(String[] list) {
        this.list = list;
    }

    public List<EntityDiseaseType> getTypes() {
        return types;
    }

    public void setTypes(List<EntityDiseaseType> types) {
        this.types = types;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public class EntityDiseaseType{
        private String[] list;
        private String name;

        public String getName() {
            return name;
        }

        public String[] getList() {
            return list;
        }

        public void setList(String[] list) {
            this.list = list;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
