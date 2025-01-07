using MySql.Data.MySqlClient;

// Code by Song Jinze

namespace ca.Data;

public class UserDAO
{
    public static int GetUserIdByUsername(string username)
    {
        using (MySqlConnection conn = new MySqlConnection(Constants.CONNECTION_STRING =
                   "Server=localhost;Database=androidCA;User=root;Password=Heythere12#;"))
        {
            conn.Open();
            string sql = @"SELECT UserId FROM User WHERE Username = @username";
            
            using (var cmd = new MySqlCommand(sql, conn))
            {
                cmd.Parameters.AddWithValue("@username", username);
                var result = cmd.ExecuteScalar();
                return result != null ? Convert.ToInt32(result) : -1;
            }
        }
    }
}