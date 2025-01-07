using MySql.Data.MySqlClient;

// Code by Song Jinze

namespace ca.Data;

public class RecordDAO
{
    public static void AddScore(int userId, string completionTime)
    {
        using (MySqlConnection conn = new MySqlConnection(Constants.CONNECTION_STRING =
                   "Server=localhost;Database=androidCA;User=root;Password=Heythere12#;"))
        {
            conn.Open();
            string sql = @"INSERT INTO Record (UserId, CompletionTime) 
                           VALUES (@userId, @completionTime)";

            using (var cmd = new MySqlCommand(sql, conn))
            {
                cmd.Parameters.AddWithValue("@userId", userId);
                cmd.Parameters.AddWithValue("@completionTime", TimeSpan.Parse(completionTime));
                cmd.ExecuteNonQuery();
            }
        }
    }
}