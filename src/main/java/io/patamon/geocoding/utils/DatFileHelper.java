package io.patamon.geocoding.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.util.Base64;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.google.gson.Gson;

import io.patamon.geocoding.model.RegionEntityEx;
import io.patamon.geocoding.model.RegionType;
import kotlin.text.Charsets;

public class DatFileHelper {

    final static List<String> provinceLevelCity1 = Lists.newArrayList("北京市", "天津市", "上海市", "重庆市");

    // 导入数据库成功后，执行china.sql，插入数据项：【中国】
    public static void main(String[] args) throws IOException {
        long start = System.currentTimeMillis();
        String pathname = "~/Documents/cnarea" + 20210707 + ".dat";
        writeDatFile(pathname);
        long end = System.currentTimeMillis();
        System.out.println(String.format("cost %s ms", end - start));
        RegionEntityEx regionEntity = null;
        regionEntity = fromDatFile(pathname);
        System.out.println(regionEntity);
    }

    private static void writeDatFile(String pathname) throws IOException {
        Connection conn = JdbcUtil.getConnection();
        if (conn == null) return;
        List<RegionEntityEx> china = Lists.newArrayList();
        List<RegionEntityEx> provinces = RegionEntityHelper.findProvinces(conn);
        for (int i = 0; i < provinces.size(); i++) {
            RegionEntityEx province = provinces.get(i);
            List<RegionEntityEx> list = RegionEntityHelper.findByProvince(conn, province.getShortName() + "%");
            if (i == 0) {
                List<RegionEntityEx> tree = parseProvince(list);
                china.add(tree.get(0));
            } else {
                List<RegionEntityEx> tree = parseProvince(list);
                china.get(0).getChildren().add(tree.get(0));
            }
        }
        JdbcUtil.free(conn);
        Gson gson = new Gson();

        byte[] context = encode(gson.toJson(china.get(0)));
        write(pathname, new String(context, Charsets.UTF_8));
    }

    public static List<RegionEntityEx> parseProvince(List<RegionEntityEx> list) {
        List<RegionEntityEx> province = Lists.newArrayList();

        for (RegionEntityEx entity : list) {
            if (entity.getParentId().equals(0L)) {
                if (entity.getChildren() == null) entity.setChildren(Lists.newArrayList());
                entity.setType(of(entity.getId(), entity.getLevel(), entity.getName()));
                province.add(entity);
            }
        }

        for (RegionEntityEx item : province) {
            item = recursive(item, list, province.size());
        }

        return province;
    }

    public static RegionEntityEx recursive(RegionEntityEx parent, List<RegionEntityEx> list, int j) {
        for (int i = j; i < list.size(); i++) {
            RegionEntityEx entity = list.get(i);
            if (parent.getId().equals(entity.getParentId())) {
                entity = recursive(entity, list, i + 1);
                entity.setType(of(entity.getId(), entity.getLevel(), entity.getName()));
                if (parent.getChildren() == null) parent.setChildren(Lists.newArrayList());
                parent.getChildren().add(entity);
            }
        }
        return parent;
    }

    public static void write(final String fileName, final String contents) throws IOException {
        File file = new File(fileName);
        file.deleteOnExit();
        file.createNewFile();
        Files.write(contents.getBytes(), file);
    }

    private static RegionType of(Long id, int level, String name) {
        if (id.equals(100000000000L)) return RegionType.Country;
        if (level == 0) {
            if (provinceLevelCity1.contains(name)) return RegionType.ProvinceLevelCity1;
            return RegionType.Province;
        }
        if (level == 1) {
            if ("直辖区".equalsIgnoreCase(name)) return RegionType.ProvinceLevelCity2;
            if ("直辖县".equalsIgnoreCase(name)) return RegionType.CityLevelDistrict;
            return RegionType.City;
        }
        if (level == 2) return RegionType.District;
        if (level == 3) {
            if (name.matches("乡$")) return RegionType.Town;
            if (name.matches("镇$")) return RegionType.Town;
            return RegionType.PlatformL4;
        }
        if (level == 4) return RegionType.Village;
        return RegionType.Undefined;
    }

    private static RegionEntityEx fromDatFile(String file) throws IOException {
        byte[] byteArray = Files.toByteArray(new File(file));
        String json = new String(byteArray);
        return new Gson().fromJson(decode(json), RegionEntityEx.class);
    }

    private static String decode(String str) throws IOException {
        byte decodedByteArray[] = Base64.getMimeDecoder().decode(str);
        GZIPInputStream gzipis = new GZIPInputStream(new ByteArrayInputStream(decodedByteArray));
        return new String(IOUtils.toByteArray(gzipis), Charsets.UTF_8);
    }

    public static byte[] encode(String str) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzipos = new GZIPOutputStream(out);
        gzipos.write(str.getBytes(Charsets.UTF_8));
        gzipos.close();
        return Base64.getMimeEncoder().encode(out.toByteArray());
    }
}