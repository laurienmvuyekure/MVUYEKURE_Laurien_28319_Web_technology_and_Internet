package auca.ac.rw.question4_e_commerce_api.Controller;

import auca.ac.rw.question4_e_commerce_api.model.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * REST Controller for managing e-commerce product catalog
 * Provides comprehensive product management with filtering and pagination
 */
@RestController
@RequestMapping("/api/products")
public class ProductController {

    // In-memory storage for products using ArrayList
    private final List<Product> products = new ArrayList<>();

    // AtomicLong for generating unique IDs in a thread-safe manner
    private final AtomicLong idGenerator = new AtomicLong(12);

    /**
     * Initialize sample data with 12 diverse products across different categories,
     * brands, and prices
     */
    public ProductController() {
        initializeSampleProducts();
    }

    /**
     * Creates twelve sample products covering various categories, brands, and price
     * ranges
     */
    private void initializeSampleProducts() {
        // Electronics
        products.add(new Product(1L, "iPhone 14 Pro",
                "Latest Apple smartphone with A16 chip and 48MP camera", 1099.99,
                "Electronics", 15, "Apple"));
        products.add(new Product(2L, "Samsung Galaxy S23",
                "Flagship Android phone with 200MP camera", 999.99,
                "Electronics", 8, "Samsung"));
        products.add(new Product(3L, "Sony WH-1000XM5",
                "Wireless noise-canceling headphones", 399.99,
                "Electronics", 0, "Sony")); // Out of stock

        // Laptops
        products.add(new Product(4L, "MacBook Pro 16",
                "Apple M2 Pro chip, 16GB RAM, 512GB SSD", 2499.99,
                "Laptops", 5, "Apple"));
        products.add(new Product(5L, "Dell XPS 15",
                "Intel i9, 32GB RAM, 1TB SSD, 4K OLED", 2199.99,
                "Laptops", 3, "Dell"));
        products.add(new Product(6L, "HP Spectre x360",
                "Convertible laptop with touchscreen", 1499.99,
                "Laptops", 0, "HP")); // Out of stock

        // Clothing
        products.add(new Product(7L, "Nike Air Max 270",
                "Men's running shoes with air cushioning", 149.99,
                "Clothing", 25, "Nike"));
        products.add(new Product(8L, "Adidas Ultraboost 22",
                "Women's running shoes", 179.99,
                "Clothing", 12, "Adidas"));
        products.add(new Product(9L, "Levi's 501 Jeans",
                "Classic straight fit jeans", 89.99,
                "Clothing", 30, "Levi's"));

        // Home & Kitchen
        products.add(new Product(10L, "Instant Pot Duo",
                "7-in-1 electric pressure cooker", 99.99,
                "Home & Kitchen", 18, "Instant Pot"));
        products.add(new Product(11L, "KitchenAid Stand Mixer",
                "Professional 5-quart mixer", 429.99,
                "Home & Kitchen", 4, "KitchenAid"));
        products.add(new Product(12L, "Ninja Air Fryer",
                "4-quart air fryer with crisping technology", 129.99,
                "Home & Kitchen", 7, "Ninja"));
    }

    /**
     * GET /api/products
     * Retrieves all products with optional pagination
     * 
     * @param page  - Page number (0-based, default 0)
     * @param limit - Number of items per page (default all)
     * @return ResponseEntity containing paginated list of products with HTTP status
     *         200 OK
     */
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "0") int limit) {

        List<Product> result = products;

        // Apply pagination if limit > 0
        if (limit > 0) {
            int start = page * limit;
            int end = Math.min(start + limit, products.size());
            if (start < products.size()) {
                result = products.subList(start, end);
            } else {
                result = new ArrayList<>();
            }
        }

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * GET /api/products/{productId}
     * Retrieves a specific product by its ID
     * 
     * @param productId - Product ID to search for
     * @return ResponseEntity containing the product if found with HTTP status 200
     *         OK,
     *         otherwise HTTP status 404 NOT FOUND
     */
    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable Long productId) {
        return products.stream()
                .filter(product -> product.getProductId().equals(productId))
                .findFirst()
                .map(product -> new ResponseEntity<>(product, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET /api/products/category/{category}
     * Retrieves all products in a specific category
     * 
     * @param category - Category to filter by
     * @return ResponseEntity containing list of products in that category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        List<Product> productsByCategory = products.stream()
                .filter(product -> product.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());

        return new ResponseEntity<>(productsByCategory, HttpStatus.OK);
    }

    /**
     * GET /api/products/brand/{brand}
     * Retrieves all products by a specific brand
     * 
     * @param brand - Brand to filter by
     * @return ResponseEntity containing list of products by that brand
     */
    @GetMapping("/brand/{brand}")
    public ResponseEntity<List<Product>> getProductsByBrand(@PathVariable String brand) {
        List<Product> productsByBrand = products.stream()
                .filter(product -> product.getBrand().equalsIgnoreCase(brand))
                .collect(Collectors.toList());

        return new ResponseEntity<>(productsByBrand, HttpStatus.OK);
    }

    /**
     * GET /api/products/search?keyword={keyword}
     * Searches for products by keyword in name or description (case-insensitive)
     * 
     * @param keyword - Keyword to search for
     * @return ResponseEntity containing list of matching products
     */
    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword) {
        String searchTerm = keyword.toLowerCase();
        List<Product> matchingProducts = products.stream()
                .filter(product -> product.getName().toLowerCase().contains(searchTerm) ||
                        product.getDescription().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());

        return new ResponseEntity<>(matchingProducts, HttpStatus.OK);
    }

    /**
     * GET /api/products/price-range?min={min}&max={max}
     * Retrieves products within a specific price range
     * 
     * @param min - Minimum price (optional)
     * @param max - Maximum price (optional)
     * @return ResponseEntity containing list of products within price range
     */
    @GetMapping("/price-range")
    public ResponseEntity<List<Product>> getProductsByPriceRange(
            @RequestParam(required = false) Double min,
            @RequestParam(required = false) Double max) {

        List<Product> filteredProducts = products.stream()
                .filter(product -> {
                    boolean meetsMin = min == null || product.getPrice() >= min;
                    boolean meetsMax = max == null || product.getPrice() <= max;
                    return meetsMin && meetsMax;
                })
                .collect(Collectors.toList());

        return new ResponseEntity<>(filteredProducts, HttpStatus.OK);
    }

    /**
     * GET /api/products/in-stock
     * Retrieves products with stock quantity greater than 0
     * 
     * @return ResponseEntity containing list of in-stock products
     */
    @GetMapping("/in-stock")
    public ResponseEntity<List<Product>> getInStockProducts() {
        List<Product> inStockProducts = products.stream()
                .filter(product -> product.getStockQuantity() > 0)
                .collect(Collectors.toList());

        return new ResponseEntity<>(inStockProducts, HttpStatus.OK);
    }

    /**
     * POST /api/products
     * Adds a new product to the catalog
     * 
     * @param product - Product object from request body (without ID)
     * @return ResponseEntity containing the created product with generated ID
     *         and HTTP status 201 CREATED
     */
    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        // Validate price and stock
        if (product.getPrice() <= 0 || product.getStockQuantity() < 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Check for duplicate product name
        if (isProductNameExists(product.getName())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        // Generate new ID for the product
        product.setProductId(idGenerator.incrementAndGet());
        products.add(product);
        return new ResponseEntity<>(product, HttpStatus.CREATED);
    }

    /**
     * PUT /api/products/{productId}
     * Updates complete product details
     * 
     * @param productId      - ID of the product to update
     * @param updatedProduct - Updated product object
     * @return ResponseEntity containing updated product or appropriate error status
     */
    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long productId,
            @RequestBody Product updatedProduct) {
        for (Product existingProduct : products) {
            if (existingProduct.getProductId().equals(productId)) {
                // Validate price and stock
                if (updatedProduct.getPrice() <= 0 || updatedProduct.getStockQuantity() < 0) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

                // Check name conflict (if name is being changed)
                if (!existingProduct.getName().equalsIgnoreCase(updatedProduct.getName()) &&
                        isProductNameExists(updatedProduct.getName())) {
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                }

                // Update product information
                existingProduct.setName(updatedProduct.getName());
                existingProduct.setDescription(updatedProduct.getDescription());
                existingProduct.setPrice(updatedProduct.getPrice());
                existingProduct.setCategory(updatedProduct.getCategory());
                existingProduct.setStockQuantity(updatedProduct.getStockQuantity());
                existingProduct.setBrand(updatedProduct.getBrand());

                return new ResponseEntity<>(existingProduct, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * PATCH /api/products/{productId}/stock?quantity={quantity}
     * Updates only the stock quantity of a product
     * 
     * @param productId - ID of the product
     * @param quantity  - New stock quantity (can be positive, negative, or zero)
     * @return ResponseEntity with updated product or appropriate error status
     */
    @PatchMapping("/{productId}/stock")
    public ResponseEntity<Product> updateStock(
            @PathVariable Long productId,
            @RequestParam int quantity) {

        for (Product product : products) {
            if (product.getProductId().equals(productId)) {
                // Ensure stock doesn't go negative
                if (product.getStockQuantity() + quantity < 0) {
                    return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                }

                product.setStockQuantity(product.getStockQuantity() + quantity);
                return new ResponseEntity<>(product, HttpStatus.OK);
            }
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * DELETE /api/products/{productId}
     * Removes a product from the catalog
     * 
     * @param productId - ID of the product to delete
     * @return ResponseEntity with HTTP status 204 NO CONTENT if successful,
     *         HTTP status 404 NOT FOUND if product doesn't exist
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        boolean removed = products.removeIf(product -> product.getProductId().equals(productId));

        if (removed) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Helper method to check if product name already exists
     * 
     * @param name - Product name to check
     * @return true if name exists, false otherwise
     */
    private boolean isProductNameExists(String name) {
        return products.stream()
                .anyMatch(product -> product.getName().equalsIgnoreCase(name));
    }
}