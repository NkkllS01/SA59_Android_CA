using Microsoft.EntityFrameworkCore;
using ca.Models;

namespace ca.Data
{
    public class AppDbContext : DbContext
    {
        public AppDbContext(DbContextOptions<AppDbContext> options) : base(options)
        {
        }

        public DbSet<User> Users { get; set; } // 定义 Users 表
    }
}
