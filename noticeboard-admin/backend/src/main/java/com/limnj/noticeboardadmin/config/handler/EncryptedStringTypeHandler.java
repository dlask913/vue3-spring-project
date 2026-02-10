package com.limnj.noticeboardadmin.config.handler;
import com.limnj.noticeboardadmin.util.AES256Util;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EncryptedStringTypeHandler extends BaseTypeHandler<String> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        try {
            ps.setString(i, AES256Util.encrypt(parameter)); // DB 에 저장할 때 암호화
        } catch (Exception e) {
            throw new SQLException("Encryption failed", e);
        }
    }

    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return decrypt(rs.getString(columnName));
    }

    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return decrypt(rs.getString(columnIndex));
    }

    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return decrypt(cs.getString(columnIndex));
    }

    private String decrypt(String dbData) throws SQLException {
        if (dbData == null) return null;
        try {
            return AES256Util.decrypt(dbData); // DB 에서 읽어올 때 복호화
        } catch (Exception e) {
            throw new SQLException("Decryption failed", e);
        }
    }
}