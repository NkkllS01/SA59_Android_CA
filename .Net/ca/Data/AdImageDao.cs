using System.Diagnostics;
using Microsoft.AspNetCore.Mvc;
using MySql.Data.MySqlClient;
using ca.Models;

public class AdImageDao{
    string connectionString =@"server=localhost;uid=root;pwd=Heythere12#;database=password";

        public List<AdImage> GetAlladImageUrl(){

        List<AdImage> adimages =new List<AdImage>();
        using(MySqlConnection conn =new MySqlConnection(connectionString)){
            conn.Open();
            string sql =@"SELECT * FROM adimage";
            MySqlCommand cmd = new MySqlCommand(sql,conn);
            MySqlDataReader reader = cmd.ExecuteReader();
            while(reader.Read()){
                AdImage adimage = new AdImage(){
                    Id = (int)reader["Id"],
                    imageurl = (string)reader["imageurl"],
                };
                adimages.Add(adimage);
            }
            conn.Close();
        }
        return adimages;

        }
}