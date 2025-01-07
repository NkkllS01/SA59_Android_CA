using ca.Models;
using Microsoft.AspNetCore.Mvc;
using MySql.Data.MySqlClient;

//Team03 Kuo Chi
namespace ca.Data
{
    public class ScoreDAO
    {
        [HttpPost]
        public static List<Score> GetTopNScores(int num)
        {
            List<Score> scores = new List<Score>();

            using (MySqlConnection conn = new MySqlConnection(Constants.CONNECTION_STRING = "Server=localhost;Database=androidCA;User=root;Password=password;"))
            {
                conn.Open();
                string sql = @"
                SELECT u.Username, r.CompletionTime
                FROM User u
                INNER JOIN Record r ON u.UserId = r.UserId
                ORDER BY r.CompletionTime ASC
                LIMIT @num
                ";

                MySqlCommand cmd = new MySqlCommand(sql, conn);
                cmd.Parameters.AddWithValue("@num", num);
                MySqlDataReader reader = cmd.ExecuteReader();

                if (!reader.HasRows)
                    {
                        Console.WriteLine("No records found in the database.");
                    }

                int rank = 1;
                int tieCount = 0;
                TimeSpan? previousTime = null;

                while (reader.Read())
                {
                    string username = reader["Username"].ToString() ?? "Unknown";
                    string completionTime = reader["CompletionTime"].ToString() ?? "00:00:00";

                    if(previousTime.HasValue)
                    {
                        if(TimeSpan.Parse(completionTime) == previousTime.Value)
                        {
                            tieCount++;
                        }
                        else
                        {
                            rank += tieCount + 1;
                            tieCount = 0;
                        }
                    }

                    scores.Add(new Score(rank.ToString(), username,completionTime));

                    previousTime = TimeSpan.Parse(completionTime);
                }
            }
            return scores;
        }
    }
}

