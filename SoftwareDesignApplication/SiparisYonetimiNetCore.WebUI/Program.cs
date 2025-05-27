using Microsoft.AspNetCore.Authentication.Cookies; // Admin girişi için gerekli kütüphane
using SiparisYonetimiNetCore.Data;
using SiparisYonetimiNetCore.Service.Abstract;
using SiparisYonetimiNetCore.Service.Concrete;

var builder = WebApplication.CreateBuilder(args);

// Add services to the container.
builder.Services.AddControllersWithViews();
builder.Services.AddSession(options =>
{
    options.IdleTimeout = TimeSpan.FromMinutes(30);
    options.Cookie.HttpOnly = true;
    options.Cookie.IsEssential = true;
});

builder.Services.AddDbContext<DatabaseContext>(); // DatabaseContext dosyasını bu şekilde servis olarak projeye ekliyoruz
// Authentication configuration
builder.Services.AddAuthentication(CookieAuthenticationDefaults.AuthenticationScheme)
    .AddCookie(x =>
    {
        x.LoginPath = "/Account/Login";
        x.LogoutPath = "/Account/Logout";
        x.AccessDeniedPath = "/Account/AccessDenied";
        x.Cookie.Name = "UserAuth";
        x.Cookie.MaxAge = TimeSpan.FromDays(7);
    });

// Authorization configuration
builder.Services.AddAuthorization(options =>
{
    options.AddPolicy("AdminPolicy", policy => policy.RequireClaim("Role", "Admin"));
    options.AddPolicy("UserPolicy", policy => policy.RequireClaim("Role", "User"));
});
// Container
builder.Services.AddTransient(typeof(ICategoryService), typeof(CategoryService)); // Repository sınıfını servis olarak kullanabilmek için
builder.Services.AddTransient(typeof(IService<>), typeof(Service<>));
builder.Services.AddTransient<IBrandService, BrandService>(); // servis eklemek için diğer yazım tarzı
builder.Services.AddTransient<IProductService, ProductService>();

builder.Services.AddSingleton<IHttpContextAccessor, HttpContextAccessor>(); // View sayfalarında giriş yapan kullanıcı bilgileri vb ye erişebilmemizi sağlar.

//builder.Services.AddScoped
//builder.Services.AddSingleton

//Diğer Dependency Injection yöntemleri :

// AddSingleton : Uygulama ayağa kalkarken ıalınan ConfigureServices metodunda bu yöntem ile tanımladığımız her sınıftan sadece bir örnek oluşturulur. Kim nereden çağrırsa çağırsın kendisine bu örnek gönderilir. Uygulama yeniden bağlayana kadar yenisi ıretilmez.
// AddTransient : Uygulama ıalıma zamanında belirli koşullarda ıretilir veya varolan ırneği kullanır. 
// AddScoped : Uygulama ıalırken her istek için ayrı ayrı nesne ıretilir.

var app = builder.Build();

// Configure the HTTP request pipeline.
if (!app.Environment.IsDevelopment())
{
    app.UseExceptionHandler("/Home/Error");
    // The default HSTS value is 30 days. You may want to change this for production scenarios, see https://aka.ms/aspnetcore-hsts.
    app.UseHsts();
}

app.UseHttpsRedirection();
app.UseStaticFiles();
app.UseSession();
app.UseRouting();

app.UseAuthentication(); // önce UseAuthentication(kullanıcı girişi)
app.UseAuthorization(); // sonra UseAuthorization(yetkilendirme)

app.MapControllerRoute(
            name: "admin",
            pattern: "{area:exists}/{controller=Main}/{action=Index}/{id?}"
          );

app.MapControllerRoute(
    name: "default",
    pattern: "{controller=Home}/{action=Index}/{id?}");

app.Run();
