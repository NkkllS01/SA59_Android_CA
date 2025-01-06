using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;

namespace ca.Models
{
    [Table("User")]
    public class User
    {
        [Key]
        [Column("UserId")]
        public int Id { get; set; }

        [Required]
        public string Username { get; set; }

        [Required]
        public string Password { get; set; }

        // Remove [Required] if it's causing issues during login
        public string UserType { get; set; } // e.g., "free", "paid"
    }
}
