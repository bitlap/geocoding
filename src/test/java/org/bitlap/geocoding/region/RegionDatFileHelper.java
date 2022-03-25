package org.bitlap.geocoding.region;

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

import org.bitlap.geocoding.model.RegionType;
import org.bitlap.geocoding.region.model.RegionEntity;
import org.bitlap.geocoding.region.util.JdbcUtil;
import kotlin.text.Charsets;

public class RegionDatFileHelper {

    final static List<String> provinceLevelCity1 = Lists.newArrayList("北京市", "天津市", "上海市", "重庆市");

    public static void writeDatFile(String pathname) throws IOException {
        write(pathname, "");
        Connection conn = JdbcUtil.getConnection();
        if (conn == null) return;
        List<RegionEntity> china = Lists.newArrayList();
        List<RegionEntity> provinces = RegionSqlHelper.findProvinces(conn);
        for (int i = 0; i < provinces.size(); i++) {
            RegionEntity province = provinces.get(i);
            List<RegionEntity> list = RegionSqlHelper.findByProvince(conn, province.getShortName() + "%");
            if (i == 0) {
                List<RegionEntity> tree = parseProvince(list);
                china.add(tree.get(0));
            } else {
                List<RegionEntity> tree = parseProvince(list);
                china.get(0).getChildren().add(tree.get(0));
            }
        }
        JdbcUtil.free(conn);
        Gson gson = new Gson();

        byte[] context = encode(gson.toJson(china.get(0)));
        write(pathname, new String(context, Charsets.UTF_8));
    }

    private static List<RegionEntity> parseProvince(List<RegionEntity> list) {
        List<RegionEntity> province = Lists.newArrayList();

        for (RegionEntity entity : list) {
            if (entity.getParentId().equals(0L)) {
                if (entity.getChildren() == null) entity.setChildren(Lists.newArrayList());
                entity.setType(of(entity.getId(), entity.getLevel(), entity.getName()));
                province.add(entity);
            }
        }

        for (RegionEntity item : province) {
            item = recursive(item, list, province.size());
        }

        return province;
    }

    private static RegionEntity recursive(RegionEntity parent, List<RegionEntity> list, int j) {
        for (int i = j; i < list.size(); i++) {
            RegionEntity entity = list.get(i);
            if (parent.getId().equals(entity.getParentId())) {
                entity = recursive(entity, list, i + 1);
                entity.setType(of(entity.getId(), entity.getLevel(), entity.getName()));
                if (parent.getChildren() == null) parent.setChildren(Lists.newArrayList());
                parent.getChildren().add(entity);
            }
        }
        return parent;
    }

    private static void write(final String fileName, final String contents) throws IOException {
        File file = new File(fileName);
        // file.deleteOnExit();
        if (!file.exists()) {
            Files.createParentDirs(file);
            file.createNewFile();
        }
        if (contents != null && !contents.trim().isEmpty()) {
            Files.write(contents.getBytes(), file);
        }
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

    public static RegionEntity readDatFile(String file) throws IOException {
        byte[] byteArray = Files.toByteArray(new File(file));
        String json = new String(byteArray);
        return new Gson().fromJson(decode(json), RegionEntity.class);
    }

    private static String decode(String str) throws IOException {
        byte decodedByteArray[] = Base64.getMimeDecoder().decode(str);
        GZIPInputStream gzipis = new GZIPInputStream(new ByteArrayInputStream(decodedByteArray));
        return new String(IOUtils.toByteArray(gzipis), Charsets.UTF_8);
    }

    private static byte[] encode(String str) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzipos = new GZIPOutputStream(out);
        gzipos.write(str.getBytes(Charsets.UTF_8));
        gzipos.close();
        return Base64.getMimeEncoder().encode(out.toByteArray());
    }
}