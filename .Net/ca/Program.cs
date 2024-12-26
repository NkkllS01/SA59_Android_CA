using ca.Data;
using Microsoft.EntityFrameworkCore;

var builder = WebApplication.CreateBuilder(args);

// ע�� MySQL ���ݿ�������
builder.Services.AddDbContext<AppDbContext>(options =>
    options.UseMySql(
        builder.Configuration.GetConnectionString("DefaultConnection"),
        new MySqlServerVersion(new Version(8, 0, 30)) // �滻Ϊ���� MySQL �汾
    ));

// ���� CORS
builder.Services.AddCors(options =>
{
    options.AddPolicy("AllowAndroidApp", builder =>
    {
        builder.WithOrigins("http://10.0.2.2", "http://localhost") // �滻Ϊ Android ģ�����ͱ���������ַ
               .AllowAnyMethod()
               .AllowAnyHeader();
    });
});

// ע��������� Swagger
builder.Services.AddControllers();
builder.Services.AddSwaggerGen();

var app = builder.Build();

// ���ÿ��������� Swagger
if (app.Environment.IsDevelopment())
{
    app.UseSwagger();
    app.UseSwaggerUI();
}

// ʹ���м��
app.UseHttpsRedirection();
app.UseCors("AllowAndroidApp"); // Ӧ�� CORS ����
app.UseAuthorization();
app.MapControllers();

app.Run();
