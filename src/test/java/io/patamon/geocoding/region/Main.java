package io.patamon.geocoding.region;

import java.io.IOException;

import io.patamon.geocoding.region.model.RegionEntity;

public class Main {
    
    // 导入数据库成功后，执行china.sql，插入数据项：【中国】
    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        String pathname = "~/Documents/cnarea" + 20210707 + ".dat";
        RegionDatFileHelper.writeDatFile(pathname);
        long end = System.currentTimeMillis();
        System.out.println(String.format("cost %s ms", end - start));
        RegionEntity regionEntity = null;
        regionEntity = RegionDatFileHelper.readDatFile(pathname);
        System.out.println(regionEntity);
    }

}
