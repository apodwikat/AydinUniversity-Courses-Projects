using Microsoft.AspNetCore.Authentication;
using Microsoft.AspNetCore.Authentication.Cookies;
using Microsoft.AspNetCore.Mvc;
using SiparisYonetimiNetCore.Entities;
using SiparisYonetimiNetCore.Service.Abstract;
using SiparisYonetimiNetCore.WebUI.Models;
using System.Security.Claims;

namespace SiparisYonetimiNetCore.WebUI.Controllers
{
    public class AccountController : Controller
    {
        private readonly IService<User> _userService;

        public AccountController(IService<User> userService)
        {
            _userService = userService;
        }

        public IActionResult Login()
        {
            return View();
        }

        [HttpPost]
        public async Task<IActionResult> Login(LoginViewModel model)
        {
            if (ModelState.IsValid)
            {
                try
                {
                    var user = await _userService.FirstOrDefaultAsync(u => u.Email == model.Email && u.Password == model.Password && u.IsActive);
                    if (user != null)
                    {
                        var haklar = new List<Claim>()
                        {
                            new Claim(ClaimTypes.Name, user.Name),
                            new Claim("Role", "User")
                        };
                        var kullaniciKimligi = new ClaimsIdentity(haklar, "Login");
                        ClaimsPrincipal principal = new(kullaniciKimligi);
                        await HttpContext.SignInAsync(principal);

                        return RedirectToAction("Index", "Home");
                    }
                    ModelState.AddModelError("", "Email veya şifre hatalı!");
                }
                catch
                {
                    ModelState.AddModelError("", "Hata oluştu! Tekrar deneyiniz.");
                }
            }
            return View(model);
        }

        public IActionResult Register()
        {
            return View();
        }

        [HttpPost]
        public async Task<IActionResult> Register(RegisterViewModel model)
        {
            if (ModelState.IsValid)
            {
                try
                {
                    var exists = await _userService.FirstOrDefaultAsync(u => u.Email == model.Email);
                    if (exists != null)
                    {
                        ModelState.AddModelError("", "Bu email adresi zaten kayıtlı!");
                        return View(model);
                    }

                    var user = new User
                    {
                        Name = model.Name,
                        Surname = model.Surname,
                        Email = model.Email,
                        Password = model.Password,
                        Phone = model.Phone,
                        IsActive = true,
                        IsAdmin = false,
                        CreateDate = DateTime.Now
                    };

                    await _userService.AddAsync(user);
                    await _userService.SaveChangesAsync();

                    // Auto login after registration
                    var haklar = new List<Claim>()
                    {
                        new Claim(ClaimTypes.Name, user.Name),
                        new Claim("Role", "User")
                    };
                    var kullaniciKimligi = new ClaimsIdentity(haklar, "Login");
                    ClaimsPrincipal principal = new(kullaniciKimligi);
                    await HttpContext.SignInAsync(principal);

                    return RedirectToAction("Index", "Home");
                }
                catch
                {
                    ModelState.AddModelError("", "Hata oluştu! Tekrar deneyiniz.");
                }
            }
            return View(model);
        }

        public async Task<IActionResult> Logout()
        {
            await HttpContext.SignOutAsync(CookieAuthenticationDefaults.AuthenticationScheme);
            return RedirectToAction("Index", "Home");
        }
    }
} 