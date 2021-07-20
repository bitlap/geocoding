package io.patamon.geocoding.model;

import java.util.List;

import com.google.gson.annotations.Expose;

public class RegionEntityEx {

    private Long id = 0L;
    private Long parentId = 0L;
    @Expose(serialize = false, deserialize = false)
    private Integer level = 0;
    private String name = "";
    @Expose(serialize = false, deserialize = false)
    private String shortName = "";
    private String alias = "";
    private RegionType type = RegionType.Undefined;
    private String zip = "";
    private List<RegionEntityEx> children = null;
    private List<String> orderedNames = null;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public RegionType getType() {
        return type;
    }

    public void setType(RegionType type) {
        this.type = type;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public List<RegionEntityEx> getChildren() {
        return children;
    }

    public void setChildren(List<RegionEntityEx> children) {
        this.children = children;
    }

    public List<String> getOrderedNames() {
        return orderedNames;
    }

    public void setOrderedNames(List<String> orderedNames) {
        this.orderedNames = orderedNames;
    }
}