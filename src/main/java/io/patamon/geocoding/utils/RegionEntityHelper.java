package io.patamon.geocoding.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.google.common.collect.Lists;

import io.patamon.geocoding.model.RegionEntityEx;

public class RegionEntityHelper {

    private static final String sqlFindAllProvinces = "select `level`, area_code as id, parent_code as parentId, "
            + "`name` as `name`, short_name as shortName, merger_name as `alias`, zip_code as zip "
            + "from cnarea_2020 where parent_code = 0 order by area_code";

    private static final String sqlFindByProvince = "select `level`, area_code as id, parent_code as parentId, "
            + "`name` as `name`, short_name as shortName, merger_name as `alias`, zip_code as zip "
            + "from cnarea_2020 where merger_name like ? order by `level`, parent_code, area_code";

    public static List<RegionEntityEx> findProvinces(Connection conn) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sqlFindAllProvinces);
            rs = pstmt.executeQuery();
            OutUtil.info(sqlFindAllProvinces);
            return convert(rs);
        } catch (SQLException sqle) {
            OutUtil.err("Exception: RegionEntityHelper.findProvinces " + sqle.getMessage());
        }finally {
            JdbcUtil.free(rs, pstmt);
        }
        return Lists.newArrayList();
    }


    public static List<RegionEntityEx> findByProvince(Connection conn, String name) {
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = conn.prepareStatement(sqlFindByProvince);
            pstmt.setString(1, name);
            rs = pstmt.executeQuery();
            OutUtil.info(sqlFindByProvince.replace("?", "'" + name + "'"));
            return convert(rs);
        } catch (SQLException sqle) {
            OutUtil.err("Exception: RegionEntityHelper.findByProvince " + sqle.getMessage());
        } finally {
            JdbcUtil.free(rs, pstmt);
        }
        return Lists.newArrayList();
    }
    
    private static List<RegionEntityEx> convert(ResultSet rs) throws SQLException {
        List<RegionEntityEx> list = Lists.newArrayList();
        while (rs != null && rs.next()) {
            RegionEntityEx regionEntityEx = new RegionEntityEx();
            regionEntityEx.setAlias(rs.getString("alias"));
            regionEntityEx.setId(rs.getLong("id"));
            regionEntityEx.setLevel(rs.getInt("level"));
            regionEntityEx.setName(rs.getString("name"));
            regionEntityEx.setParentId(rs.getLong("parentId"));
            regionEntityEx.setShortName(rs.getString("shortName"));
            regionEntityEx.setZip(rs.getString("zip"));
            list.add(regionEntityEx);
        }
        return list;
    }
}