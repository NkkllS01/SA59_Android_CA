using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Threading.Tasks;
using ca.Data;
using ca.Models;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Logging;
using System.Text.Json;
using Newtonsoft.Json;

//Team03 Kuo Chi
namespace ca.Controllers
{
    [Route("api/leaderboard")]
    [ApiController]
    public class LeaderboardController : Controller
    {
        [HttpPost("display")]
        public IActionResult Display([FromBody] TopRank rank)
        {
            var num = 0;

            if (rank == null) {
                return BadRequest("Invalid data");
            } else {
                num = rank.Top;
            }

            try {
                List<Score> scores = ScoreDAO.GetTopNScores(num);
                return Ok(scores);
            } catch (Exception e) {
                Console.WriteLine("Error: " + e.Message);
                return StatusCode(500, "An error occurred while fetching the scores.");
            }
        }

        public IActionResult Error()
        {
            return Problem("An error occurred.");
        }
    }
}