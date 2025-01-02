//Team03 Kuo Chi
public class Score
{
    public string Rank {get; set;}
    public string Username {get; set;}
    public string CompletionTime {get; set;}

    public Score(string rank, string username, string completionTime)
    {
        this.Rank = rank;
        this.Username = username;
        this.CompletionTime = completionTime;
    }
}