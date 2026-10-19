package org.OneGuardian.tests;

import org.OneGuardian.base.BaseTest;
import org.OneGuardian.pages.CartPage;
import org.OneGuardian.pages.ProductDetailPage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Product Detail Page (PDP) coverage — previously untested even though a ProductDetailPage object
 * existed. Drives a known product page directly (stable slug on the preview storefront) and verifies
 * the core buy-path: product info renders, the quantity stepper works, and Add-to-Cart adds the
 * right product and quantity.
 */
public class ProductDetailPageTest extends BaseTest {

    private static final Logger log = LogManager.getLogger(ProductDetailPageTest.class);

    private static final String PRODUCT_URL =
            "https://bvl---live-59c56cb80d07a4341b5e.o2.myshopify.dev/products/white-oud-unisex-luxury-perfume";
    private static final String PRODUCT_NAME = "WHITE OUD";

    // PDP-01
    @Test(enabled = true)
    public void verifyPdpLoadsWithTitlePriceAndReviews() {
        driver.get(PRODUCT_URL);
        ProductDetailPage pdp = new ProductDetailPage(driver);

        String title = pdp.getPdpTitle();
        log.info("PDP title: " + title);
        Assert.assertTrue(title.toUpperCase().contains(PRODUCT_NAME),
                "PDP title did not contain expected product name. Actual: " + title);

        String salePrice = pdp.getSalePrice();
        log.info("PDP sale price: " + salePrice);
        Assert.assertTrue(salePrice.contains("₹"),
                "PDP sale price not shown with ₹. Actual: " + salePrice);

        Assert.assertTrue(pdp.isReviewWidgetPresent(),
                "Reviews widget did not render on the PDP");
    }

    // PDP-02
    @Test(enabled = true)
    public void verifyQuantityStepperIncrementDecrement() {
        driver.get(PRODUCT_URL);
        ProductDetailPage pdp = new ProductDetailPage(driver);

        int start = pdp.getQuantityValue();
        Assert.assertEquals(start, 1, "Quantity stepper should start at 1");

        pdp.increaseQuantity();
        Assert.assertEquals(pdp.getQuantityValue(), 2, "Quantity should be 2 after one increase");

        pdp.decreaseQuantity();
        Assert.assertEquals(pdp.getQuantityValue(), 1, "Quantity should return to 1 after decrease");
    }

    // PDP-03
    @Test(enabled = true)
    public void verifyAddToCartFromPdpOpensDrawer() {
        driver.get(PRODUCT_URL);
        ProductDetailPage pdp = new ProductDetailPage(driver);

        pdp.clickAddToCart();

        CartPage cartPage = new CartPage(driver);
        Assert.assertTrue(cartPage.isCartPageDisplayed(),
                "Cart drawer did not open after Add to Cart from PDP");
        Assert.assertTrue(cartPage.getCartItemName().toUpperCase().contains(PRODUCT_NAME),
                "Cart did not contain the product added from the PDP. Actual: " + cartPage.getCartItemName());
    }

    // PDP-05
    @Test(enabled = true)
    public void verifyProductInfoAccordionsExpand() {
        driver.get(PRODUCT_URL);
        ProductDetailPage pdp = new ProductDetailPage(driver);

        for (String section : new String[]{"KEY BENEFITS", "HOW TO USE", "PERFUME NOTES", "ALL INGREDIENTS"}) {
            pdp.clickAccordion(section);
            Assert.assertTrue(pdp.isAccordionContentVisible(section),
                    "Accordion content did not expand for section: " + section);
            log.info("Accordion expanded: " + section);
        }
    }

    // PDP-07
    @Test(enabled = true)
    public void verifyReviewsSectionLoads() {
        driver.get(PRODUCT_URL);
        ProductDetailPage pdp = new ProductDetailPage(driver);

        Assert.assertTrue(pdp.isReviewWidgetPresent(), "Reviews widget did not render on the PDP");
        Assert.assertTrue(pdp.isRatingStarsPresent(), "Rating stars were not displayed on the PDP");
    }

    // PDP-04
    @Test(enabled = true)
    public void verifyAddToCartWithQuantityTwo() {
        driver.get(PRODUCT_URL);
        ProductDetailPage pdp = new ProductDetailPage(driver);

        pdp.setQuantity(2);
        Assert.assertEquals(pdp.getQuantityValue(), 2, "PDP quantity should be 2 before adding to cart");

        pdp.clickMainAddToCart();

        CartPage cartPage = new CartPage(driver);
        Assert.assertTrue(cartPage.isCartPageDisplayed(),
                "Cart drawer did not open after Add to Cart");
        Assert.assertEquals(cartPage.getQuantity(), "2",
                "Cart line quantity should be 2 when PDP quantity was 2");
    }
}
