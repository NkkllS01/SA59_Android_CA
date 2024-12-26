using ca.Data; 
using ca.Models; 
using Microsoft.AspNetCore.Mvc;
using System.Linq;
using Microsoft.EntityFrameworkCore;
using System.Security.Cryptography;
using System.Text;


namespace ca.Controllers
{
    [Route("api/auth")]
    [ApiController]
    public class AuthController : ControllerBase
    {
        private readonly AppDbContext _context;

        public AuthController(AppDbContext context)
        {
            _context = context;
        }

        [HttpPost("login")]
        public IActionResult Login([FromBody] LoginRequest loginRequest)
        {
            // 检查 ModelState 是否有效
            if (!ModelState.IsValid)
            {
                return BadRequest(ModelState);
            }

            // 从数据库中查找用户
            var user = _context.Users.FirstOrDefault(u =>
                u.Username == loginRequest.Username && u.Password == loginRequest.Password);

            if (user != null)
            {
                return Ok(new { success = true, userType = user.UserType });
            }

            return BadRequest(new { success = false, message = "Invalid username or password." });
        }


        /// <summary>
        /// 用户注册接口
        /// </summary>
        /// <param name="registerRequest">包含用户名、密码和用户类型的请求体</param>
        /// <returns>注册结果</returns>
        [HttpPost("register")]
        public IActionResult Register([FromBody] User registerRequest)
        {
            // 检查用户名是否已存在
            if (_context.Users.Any(u => u.Username == registerRequest.Username))
            {
                return BadRequest(new { success = false, message = "Username already exists." });
            }

            // 加密密码
            registerRequest.Password = EncryptPassword(registerRequest.Password);

            // 添加新用户
            _context.Users.Add(registerRequest);
            _context.SaveChanges();

            return Ok(new { success = true, message = "User registered successfully." });
        }

        /// <summary>
        /// 使用 SHA256 对密码进行加密
        /// </summary>
        /// <param name="password">原始密码</param>
        /// <returns>加密后的密码</returns>
        private string EncryptPassword(string password)
        {
            using (SHA256 sha256 = SHA256.Create())
            {
                byte[] bytes = sha256.ComputeHash(Encoding.UTF8.GetBytes(password));
                StringBuilder builder = new StringBuilder();
                foreach (var b in bytes)
                {
                    builder.Append(b.ToString("x2")); // 转换为十六进制字符串
                }
                return builder.ToString();
            }
        }
    }
}

