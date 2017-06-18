package no.priv.bang.ukelonn.bundle.db.liquibase.customchange;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

import org.apache.shiro.codec.CodecSupport;
import org.junit.Test;
import org.mockito.Mockito;

import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.DatabaseException;

public class ConvertUsersTableSaltEncodingFromBase64ToUtf8Test {

    /**
     * Used in a sparse mock to capture a prepared update statement parameter
     * (ie. the value used in an SQL "update ..." statement setting a
     * "password_salt" value with the recoded value from the "salt" column).
     */
    abstract class CaptureStringArgumentStatement implements PreparedStatement {

        private String capturedParameter;

        @Override
        public void setString(int parameterIndex, String parameterValue) throws SQLException {
            capturedParameter = parameterValue;

        }

        public String getCapturedParameter() {
            return capturedParameter;
        }

    }

    @Test
    public void testExecute() throws CustomChangeException, DatabaseException, SQLException {
        String originalSalt = "Iu9wre2JgXuxvRT2MA0CGQ==";
        Database database = mock(Database.class);
        JdbcConnection connection = mock(JdbcConnection.class);
        PreparedStatement select = mock(PreparedStatement.class);
        ResultSet results = mock(ResultSet.class);
        when(results.next()).thenReturn(true).thenReturn(false);
        when(results.getInt(0)).thenReturn(1);
        when(results.getString(1)).thenReturn(originalSalt);
        when(select.executeQuery()).thenReturn(results);
        CaptureStringArgumentStatement update = mock(CaptureStringArgumentStatement.class, Mockito.CALLS_REAL_METHODS);
        when(connection.prepareStatement(eq("select user_id, salt from users"))).thenReturn(select);
        when(connection.prepareStatement(eq("update users set password_salt=? where user_id=?"))).thenReturn(update);
        when(database.getConnection()).thenReturn(connection);

        ConvertUsersTableSaltEncodingFromBase64ToUtf8 task = new ConvertUsersTableSaltEncodingFromBase64ToUtf8();
        task.execute(database);

        // try to emulate how salt is decoded in the realms
        // salt decoding from UkelonnRealm
        byte[] decodedOriginalSalt = Base64.getDecoder().decode(originalSalt);
        // salt decoding as done in the JdbcRealm
        String utf8EncodedSalt = update.getCapturedParameter();
        byte[] decodedUtf8EncodedSalt = CodecSupport.toBytes(utf8EncodedSalt);
        assertEquals(decodedOriginalSalt, decodedUtf8EncodedSalt);
    }

}
