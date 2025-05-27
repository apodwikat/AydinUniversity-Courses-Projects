using Microsoft.AspNetCore.Mvc;
using SiparisYonetimiNetCore.Entities;
using System.Collections.Generic;
using SiparisYonetimiNetCore.WebUI.Extensions;
using SiparisYonetimiNetCore.Service.Abstract;
using Microsoft.AspNetCore.Authorization;

namespace SiparisYonetimiNetCore.WebUI.Controllers
{
    [Authorize]
    public class CartController : Controller
    {
        private readonly IService<Product> _productService;

        public CartController(IService<Product> productService)
        {
            _productService = productService;
        }

        public async Task<IActionResult> Index()
        {
            var cart = HttpContext.Session.Get<List<CartItem>>("Cart") ?? new List<CartItem>();
            
            // Load product details for each cart item
            foreach (var item in cart)
            {
                item.Product = await _productService.FindAsync(item.ProductId);
            }
            
            return View(cart);
        }

        [HttpPost]
        public async Task<IActionResult> AddToCart(int productId, int quantity = 1)
        {
            var cart = HttpContext.Session.Get<List<CartItem>>("Cart") ?? new List<CartItem>();
            
            var existingItem = cart.FirstOrDefault(x => x.ProductId == productId);
            if (existingItem != null)
            {
                existingItem.Quantity += quantity;
            }
            else
            {
                var product = await _productService.FindAsync(productId);
                cart.Add(new CartItem { 
                    ProductId = productId, 
                    Quantity = quantity,
                    Product = product
                });
            }

            HttpContext.Session.Set("Cart", cart);
            
            return Json(new { 
                success = true, 
                count = cart.Count,
                productName = cart.FirstOrDefault(x => x.ProductId == productId)?.Product?.Name 
            });
        }

        [HttpPost]
        public IActionResult RemoveFromCart(int productId)
        {
            var cart = HttpContext.Session.Get<List<CartItem>>("Cart") ?? new List<CartItem>();
            var item = cart.FirstOrDefault(x => x.ProductId == productId);
            if (item != null)
            {
                cart.Remove(item);
                HttpContext.Session.Set("Cart", cart);
            }
            return Json(new { success = true, count = cart.Count });
        }

        [HttpGet]
        public IActionResult GetCartCount()
        {
            var cart = HttpContext.Session.Get<List<CartItem>>("Cart") ?? new List<CartItem>();
            return Json(new { count = cart.Count });
        }

        [HttpPost]
        public IActionResult CompleteOrder()
        {
            // Clear the cart by setting it to an empty list
            HttpContext.Session.Set("Cart", new List<CartItem>());
            return Json(new { success = true });
        }
    }
} 