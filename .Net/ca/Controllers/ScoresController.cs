using ca.Data;
using Microsoft.AspNetCore.Mvc;

// Code by Song Jinze 

namespace ca.Controllers
{
    [ApiController]
    [Route("api/scores")]
    public class ScoresController : ControllerBase
    {
        [HttpPost("add")]
        public IActionResult AddScore([FromBody] AddScoreRequest request)
        {
            try
            {
                // 通过UserDAO，根据传入的userName获得对应的userId
                int userId = UserDAO.GetUserIdByUsername(request.Username);
                if (userId == -1)
                {
                    return BadRequest("User not found.");
                }
                
                // 通过RecordDAO，在数据库的Record表中新建一条记录
                RecordDAO.AddScore(userId, request.CompletionTime);
                
                return Ok("Score added successfully.");
            }
            catch (Exception ex)
            {
                return BadRequest($"Error adding score: {ex.Message}");
            }
        }
    }
}