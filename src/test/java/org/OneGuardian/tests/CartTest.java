package org.OneGuardian.tests;

import org.OneGuardian.base.BaseTest;
import org.OneGuardian.pages.CartPage;
import org.OneGuardian.pages.HomePage;
import org.OneGuardian.pages.ProductDetailPage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CartTest extends BaseTest {

    private static final Logger log = LogManager.getLogger(CartTest.class);


    // ─────────────────────────────────────────
    // TEST 1 — Verify Cart Page Loads
    // ─────────────────────────────────────────

    @Test(enabled = true)
    public void verifyCartPageLoads() {
        log.info("TEST: Verifying cart page loads...");
        HomePage  homePage = new HomePage(driver);
        HomePage.navigateTo();
        homePage.navigateToBestsellers();
        homePage.clickATCByProductName("WHITE OUD");

        CartPage cartPage = new CartPage(driver);

        Assert.assertTrue(cartPage.isCartPageDisplayed(),
                "Cart page did not load!");

        log.info("✅ Cart page loaded successfully");
    }

    // ─────────────────────────────────────────
    // TEST 2 — Verify Product is Added to Cart
    // ─────────────────────────────────────────
    @Test(enabled = true)
    public void verifyATCForSpecificProductInBestsellers() {

        HomePage homePage = new HomePage(driver);
        HomePage.navigateTo();

        // Step 1 — Go to Bestsellers
        homePage.navigateToBestsellers();

        // Step 2 — Pass product name dynamically
        // Change this to any product name you want to test
        String targetProduct = "WHITE OUD";

        // Step 3 — Click ATC for that specific product
        boolean productFound = homePage.clickATCByProductName(targetProduct);


        // Step 4 — Verify product was found
        Assert.assertTrue(productFound,
                "Product '" + targetProduct + "' was not found in Bestsellers!");
        log.info("Product found and ATC clicked ✅");

        // Step 5 — Verify cart count increased
        CartPage cartPage = new CartPage(driver);
        String cartItemName= cartPage.getCartItemName(); // Get cartItem name to ensure cart sidebar is loaded
        Assert.assertTrue(
                cartItemName.toLowerCase().contains(targetProduct.toLowerCase()),
                "Expected product to be present in cart: " + targetProduct +
                        " | Actual: " + cartItemName
        );

        cartPage.closeCart(); // Close cart sidebar to see header cart count
        String cartCount = cartPage.getCartItemCount();
        log.info("Cart count after ATC → " + cartCount);

        Assert.assertTrue(Integer.parseInt(cartCount) > 0,
                "Cart count should be > 0 after ATC!");

        log.info("✅ Dynamic ATC verified for product: " + targetProduct
                + " | Cart Count: " + cartCount);
    }



    // ─────────────────────────────────────────
    // TEST 3 — Verify Cart Item Count > 0
    // ─────────────────────────────────────────
    @Test(enabled = true)
    public void verifyCartItemCount() {
        log.info("TEST: Verifying cart item count...");

        HomePage homePage = new HomePage(driver);
        HomePage.navigateTo();
        homePage.navigateToBestsellers();
        homePage.clickATCByProductName("WHITE OUD");


        CartPage cartPage = new CartPage(driver);
        cartPage.closeCart();
        int count = Integer.parseInt(cartPage.getCartItemCount());

        log.info("Cart count: " + count);

        Assert.assertTrue(count > 0,
                "Cart count should be > 0 after adding product!");

        log.info("✅ Cart count verified: " + count);
    }

    // ─────────────────────────────────────────
    // TEST 4 — Verify Increase Quantity
    // ─────────────────────────────────────────
    @Test(enabled = true)
    public void verifyIncreaseQuantityInCart() {
        log.info("TEST: Verifying increase quantity in cart...");

        HomePage homePage = new HomePage(driver);
        HomePage.navigateTo();
        homePage.navigateToBestsellers();
        homePage.clickATCByProductName("WHITE OUD");


        CartPage cartPage = new CartPage(driver);
//        cartPage.clickCartIcon();

        // Get quantity before clicking +
        int quantityBefore = Integer.parseInt(cartPage.getQuantity());
        log.info("Quantity before: " + quantityBefore);

        cartPage.clickIncreaseQuantity();

        // Get quantity after clicking +
        int quantityAfter = Integer.parseInt(cartPage.getQuantity());
        log.info("Quantity after: " + quantityAfter);

        Assert.assertEquals(quantityAfter, quantityBefore + 1,
                "Quantity should increase by 1!");

        log.info("✅ Quantity increased: " + quantityBefore + " → " + quantityAfter);
    }

    // ─────────────────────────────────────────
    // TEST 5 — Verify Decrease Quantity
    // ─────────────────────────────────────────
    @Test(enabled = true)
    public void verifyDecreaseQuantityInCart() {
        log.info("TEST: Verifying decrease quantity in cart...");

        HomePage homePage = new HomePage(driver);
        HomePage.navigateTo();
        homePage.navigateToBestsellers();
        homePage.clickATCByProductName("WHITE OUD");

        CartPage cartPage = new CartPage(driver);
//        cartPage.clickCartIcon();

        // First increase to 2 so we can decrease back to 1
        cartPage.clickIncreaseQuantity();
        int quantityBefore = Integer.parseInt(cartPage.getQuantity());
        log.info("Quantity before decrease: " + quantityBefore);

        cartPage.clickDecreaseQuantity();

        int quantityAfter = Integer.parseInt(cartPage.getQuantity());
        log.info("Quantity after decrease: " + quantityAfter);

        Assert.assertEquals(quantityAfter, quantityBefore - 1,
                "Quantity should decrease by 1!");

        log.info("✅ Quantity decreased: " + quantityBefore + " → " + quantityAfter);
    }

    // ─────────────────────────────────────────
    // TEST 6 — Verify Remove Item from Cart
    // ─────────────────────────────────────────
    @Test(enabled = true)
    public void verifyRemoveItemFromCart() {
        log.info("TEST: Verifying remove item from cart...");

        HomePage homePage = new HomePage(driver);
        HomePage.navigateTo();
        homePage.navigateToBestsellers();
        homePage.clickATCByProductName("WHITE OUD");

        CartPage cartPage = new CartPage(driver);
//        cartPage.clickCartIcon();

        cartPage.clickRemoveItem();
//        cartPage.clickCartIcon();

        // After removing — cart should be empty
        Assert.assertTrue(cartPage.isCartEmpty(),
                "Cart should be empty after removing item!");

        log.info("✅ Item removed — cart is empty");
    }

    // ─────────────────────────────────────────
    // TEST 7 — Verify Cart Total Price Displayed
    // ─────────────────────────────────────────
    @Test(enabled = true)
    public void verifyCartTotalPrice() {
        log.info("TEST: Verifying cart total price is displayed...");

        HomePage homePage = new HomePage(driver);
        HomePage.navigateTo();
        homePage.navigateToBestsellers();
        homePage.clickATCByProductName("WHITE OUD");

        CartPage cartPage = new CartPage(driver);
//        cartPage.clickCartIcon();

        String totalPrice = cartPage.getCartTotalPrice();
        log.info("Cart total price: " + totalPrice);

        Assert.assertTrue(totalPrice.contains("₹"),
                "Cart total should show price with ₹ symbol!");

        log.info("✅ Cart total price displayed: " + totalPrice);
    }
}