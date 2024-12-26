using System.ComponentModel.DataAnnotations;

namespace ca.Models
{
    public class User
    {
        public int Id { get; set; }

        [Required]
        public string Username { get; set; }

        [Required]
        public string Password { get; set; }

        // Remove [Required] if it's causing issues during login
        public string UserType { get; set; } // e.g., "free", "paid"
    }
}
