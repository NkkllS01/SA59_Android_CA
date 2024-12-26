using ca.Data;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

// 注册 MySQL 数据库上下文
builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseMySql(
        builder.Configuration.GetConnectionString("DefaultConnection"),
        new MySqlServerVersion(new Version(8, 0, 30)) // 替换为您的 MySQL 版本
    ));

// 启用 CORS
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAndroidApp", builder =>
    {
        builder.WithOrigins("http://10.0.2.2", "http://localhost") // 替换为 Android 模拟器和本地主机地址
               .AllowAnyMethod()
               .AllowAnyHeader();
    });
});

// 注册控制器和 Swagger
builder.Services.AddControllers();
builder.Services.AddSwaggerGen();

var app = builder.Build();

// 启用开发环境的 Swagger
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

// 使用中间件
app.UseHttpsRedirection();
app.UseCors("AllowAndroidApp"); // 应用 CORS 策略
app.UseAuthorization();
app.MapControllers();

app.Run();
