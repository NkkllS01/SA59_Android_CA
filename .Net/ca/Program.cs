using ca.Data;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);


builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseMySql(
        builder.Configuration.GetConnectionString("DefaultConnection"),
        new MySqlServerVersion(new Version(8, 0, 30)) 
    ));


builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAndroidApp", builder =>
    {
        builder.WithOrigins("http://10.0.2.2", "http://localhost")
               .AllowAnyMethod()
               .AllowAnyHeader();
    });
});


builder.Services.AddControllers();
builder.Services.AddSwaggerGen();

var app = builder.Build();


if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}


app.UseHttpsRedirection();
app.UseCors("AllowAndroidApp");
app.UseAuthorization();
app.MapControllers();
app.UseCors("AllowAll");
app.Run();
