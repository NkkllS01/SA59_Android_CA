using System.Diagnostics;
using Microsoft.AspNetCore.Mvc;


[ApiController]
public class AdController: Controller{
    private readonly AdImageDao adimageDao = new AdImageDao();
    private string _lastReturnedImageUrl=null;
    

    [Route("api/adimage")]
    [HttpGet]
    public IActionResult Index(){
        List<AdImage> images = adimageDao.GetAlladImageUrl();

        //Check if getAllimages
        if(images==null||images.Count==0){
            return NotFound("No ad image available.");
        }

        //Generate a random imageURL
        string randomImageUrl;
        do{
            var random = new Random();
            int index = random.Next(images.Count);
            randomImageUrl = images[index].imageurl;
        }while(randomImageUrl== _lastReturnedImageUrl&&images.Count>1);

        _lastReturnedImageUrl = randomImageUrl;

 
        return Ok(new{ImageUrl=randomImageUrl});
    }

}