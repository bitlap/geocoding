package org.bitlap.geocoding.region;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import com.google.common.collect.Lists;

import org.bitlap.geocoding.region.model.RegionEntity;
import org.bitlap.geocoding.region.util.JdbcUtil;
import org.bitlap.geocoding.region.util.OutUtil;

public class RegionSqlHelper {

    private static final String sqlFindAllProvinces = "select `level`, area_code as id, parent_code as parentId, "
            + "`name` as `name`, short_name as shortName, merger_name as `alias`, zip_code as zip "
            + "from cnarea_2020 where parent_code = 0 order by area_code";

    private static final String sqlFindByProvince = "select `level`, area_code as id, parent_code as parentId, "
            + "`name` as `name`, short_name as shortName, merger_name as `alias`, zip_code as zip "
            + "from cnarea_2020 where merger_name like ? order by `level`, parent_code, area_code";

    public static List<RegionEntity> findProvinces(Connection conn) {
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


    public static List<RegionEntity> findByProvince(Connection conn, String name) {
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
    
    private static List<RegionEntity> convert(ResultSet rs) throws SQLException {
        List<RegionEntity> list = Lists.newArrayList();
        while (rs != null && rs.next()) {
            RegionEntity regionEntity = new RegionEntity();
            regionEntity.setAlias(rs.getString("alias"));
            regionEntity.setId(rs.getLong("id"));
            regionEntity.setLevel(rs.getInt("level"));
            regionEntity.setName(rs.getString("name"));
            regionEntity.setParentId(rs.getLong("parentId"));
            regionEntity.setShortName(rs.getString("shortName"));
            regionEntity.setZip(rs.getString("zip"));
            list.add(regionEntity);
        }
        return list;
    }
}