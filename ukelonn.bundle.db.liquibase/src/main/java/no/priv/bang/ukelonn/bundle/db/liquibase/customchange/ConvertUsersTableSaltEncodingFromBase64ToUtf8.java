package no.priv.bang.ukelonn.bundle.db.liquibase.customchange;

import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;

import org.apache.shiro.codec.CodecSupport;

import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;

/***
 * This class will be called from a liquibase changelist.
 *
 * The class will retrive all values of the "salt" column in the "users" table, decode the "salt"
 * values with base64, then write the values to the "password_salt" column, encoded with UTF-8.
 *
 * The reason for this is that the project has been using a custom Shiro realm, UkelonnRealm,
 * that assumes both the encrypted password and the salt are encoded with base64, and I tried
 * replacing this realm with the shiro JdbcRealm, and the JdbcRealm assumes base64 encoding
 * of the encrypted password, but UTF-8 encoding on the salt.  And this behaviour us hard-coded
 * and not configurable.
 *
 * So I'm changing the database structure to be aligned with JdbcRealm. I'm changing the column
 * name at the same time (from "salt" to "password_salt") to be the same as the default value
 * of JdbcRealm (the SQL query to fetch the password and salt was configurable, so that part
 * wasn't a blocker).
 *
 * @author Steinar Bang
 *
 */
public class ConvertUsersTableSaltEncodingFromBase64ToUtf8 implements CustomTaskChange {

    @Override
    public void execute(Database database) throws CustomChangeException {
        JdbcConnection connection = (JdbcConnection) database.getConnection();
        try {
            PreparedStatement getsalt = connection.prepareStatement("select user_id, salt from users");
            PreparedStatement updateUtf8EncodedSalt = connection.prepareStatement("update users set password_salt=? where user_id=?");
            ResultSet result = getsalt.executeQuery();
            while(result.next()) {
                int userId = result.getInt(0);
                String base64EncodedSalt = result.getString(1);
                byte[] decodedSalt = Base64.getDecoder().decode(base64EncodedSalt);
                char[] utf8EncodedSalt = CodecSupport.toChars(decodedSalt);
                //String utf8EncodedSalt = CodecSupport.toString(decodedSalt);
                //String utf8EncodedSalt = new String(decodedSalt, StandardCharsets.UTF_8);
                updateUtf8EncodedSalt.setString(0, new String(utf8EncodedSalt));
                updateUtf8EncodedSalt.setInt(1, userId);
                updateUtf8EncodedSalt.executeUpdate();
            }
        } catch (Exception e) {
            throw new CustomChangeException("Failed to convert users table salt values from base64 to utf-8", e);
        }
    }

    @Override
    public String getConfirmationMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setUp() throws SetupException {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFileOpener(ResourceAccessor resourceAccessor) {
        // TODO Auto-generated method stub

    }

    @Override
    public ValidationErrors validate(Database database) {
        // TODO Auto-generated method stub
        return null;
    }

}
