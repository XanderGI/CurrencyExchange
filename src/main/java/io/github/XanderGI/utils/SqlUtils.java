package io.github.XanderGI.utils;

import org.sqlite.SQLiteErrorCode;
import org.sqlite.SQLiteException;

import java.sql.SQLException;

public final class SqlUtils {

    private SqlUtils() {

    }

    public static boolean  isUniqueConstraintViolation(SQLException e) {
        return e instanceof SQLiteException sqlEx &&
                sqlEx.getResultCode() == SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE;
    }
}