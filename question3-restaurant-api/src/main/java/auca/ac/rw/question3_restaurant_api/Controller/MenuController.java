package auca.ac.rw.question3_restaurant_api.Controller;

import auca.ac.rw.question3_restaurant_api.Model.MenuItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 * REST Controller for managing restaurant menu items
 * Provides comprehensive menu management functionality
 */
@RestController
@RequestMapping("/api/menu")
public class MenuController {

    // In-memory storage for menu items using ArrayList
    private final List<MenuItem> menuItems = new ArrayList<>();

    // AtomicLong for generating unique IDs in a thread-safe manner
    private final AtomicLong idGenerator = new AtomicLong(8);

    /**
     * Initialize sample data with 8 diverse menu items across all categories
     */
    public MenuController() {
        initializeSampleMenuItems();
    }

    /**
     * Creates eight sample menu items covering all categories
     * Includes a mix of available and unavailable items
     */
    private void initializeSampleMenuItems() {
        // Appetizers
        menuItems.add(new MenuItem(1L, "Bruschetta",
                "Toasted bread with fresh tomatoes, garlic, and basil", 8.99,
                "Appetizer", true));
        menuItems.add(new MenuItem(2L, "Calamari Fritti",
                "Crispy fried squid with marinara sauce", 12.99,
                "Appetizer", true));

        // Main Courses
        menuItems.add(new MenuItem(3L, "Grilled Salmon",
                "Fresh Atlantic salmon with lemon butter sauce", 24.99,
                "Main Course", true));
        menuItems.add(new MenuItem(4L, "Beef Tenderloin Steak",
                "8oz center-cut tenderloin with red wine reduction", 32.99,
                "Main Course", false)); // Temporarily unavailable
        menuItems.add(new MenuItem(5L, "Mushroom Risotto",
                "Creamy Arborio rice with wild mushrooms and parmesan", 18.99,
                "Main Course", true));

        // Desserts
        menuItems.add(new MenuItem(6L, "Tiramisu",
                "Classic Italian dessert with mascarpone and espresso", 9.99,
                "Dessert", true));
        menuItems.add(new MenuItem(7L, "Chocolate Lava Cake",
                "Warm chocolate cake with molten center", 8.99,
                "Dessert", false)); // Temporarily unavailable

        // Beverages
        menuItems.add(new MenuItem(8L, "House Red Wine",
                "Rich Italian red wine, glass", 11.99,
                "Beverage", true));
    }

    /**
     * GET /api/menu
     * Retrieves all menu items
     * 
     * @return ResponseEntity containing list of all menu items with HTTP status 200
     *         OK
     */
    @GetMapping
    public ResponseEntity<List<MenuItem>> getAllMenuItems() {
        return new ResponseEntity<>(menuItems, HttpStatus.OK);
    }

    /**
     * GET /api/menu/{id}
     * Retrieves a specific menu item by ID
     * 
     * @param id - Menu item ID to search for
     * @return ResponseEntity containing the menu item if found with HTTP status 200
     *         OK,
     *         otherwise HTTP status 404 NOT FOUND
     */
    @GetMapping("/{id}")
    public ResponseEntity<MenuItem> getMenuItemById(@PathVariable Long id) {
        return menuItems.stream()
                .filter(item -> item.getId().equals(id))
                .findFirst()
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * GET /api/menu/category/{category}
     * Retrieves all menu items in a specific category
     * 
     * @param category - Category to filter by (case-insensitive)
     * @return ResponseEntity containing list of items in that category with HTTP
     *         status 200 OK
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<MenuItem>> getItemsByCategory(@PathVariable String category) {
        List<MenuItem> itemsByCategory = menuItems.stream()
                .filter(item -> item.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());

        return new ResponseEntity<>(itemsByCategory, HttpStatus.OK);
    }

    /**
     * GET /api/menu/available?available=true
     * Retrieves only available menu items based on query parameter
     * 
     * @param available - Boolean query parameter to filter by availability
     * @return ResponseEntity containing filtered list with HTTP status 200 OK
     */
    @GetMapping("/available")
    public ResponseEntity<List<MenuItem>> getAvailableItems(
            @RequestParam(value = "available", defaultValue = "true") boolean available) {
        List<MenuItem> availableItems = menuItems.stream()
                .filter(item -> item.isAvailable() == available)
                .collect(Collectors.toList());

        return new ResponseEntity<>(availableItems, HttpStatus.OK);
    }

    /**
     * GET /api/menu/search?name={name}
     * Searches for menu items by name (case-insensitive partial match)
     * 
     * @param name - Name or partial name to search for
     * @return ResponseEntity containing list of matching items with HTTP status 200
     *         OK
     */
    @GetMapping("/search")
    public ResponseEntity<List<MenuItem>> searchItemsByName(@RequestParam String name) {
        List<MenuItem> matchingItems = menuItems.stream()
                .filter(item -> item.getName().toLowerCase()
                        .contains(name.toLowerCase()))
                .collect(Collectors.toList());

        return new ResponseEntity<>(matchingItems, HttpStatus.OK);
    }

    /**
     * POST /api/menu
     * Adds a new menu item to the restaurant menu
     * 
     * @param menuItem - MenuItem object from request body (without ID)
     * @return ResponseEntity containing the created item with generated ID
     *         and HTTP status 201 CREATED
     */
    @PostMapping
    public ResponseEntity<MenuItem> addMenuItem(@RequestBody MenuItem menuItem) {
        // Validate price
        if (menuItem.getPrice() <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Validate category
        if (!isValidCategory(menuItem.getCategory())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Check for duplicate name
        if (isNameExists(menuItem.getName())) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }

        // Generate new ID for the menu item
        menuItem.setId(idGenerator.incrementAndGet());
        menuItems.add(menuItem);
        return new ResponseEntity<>(menuItem, HttpStatus.CREATED);
    }

    /**
     * PUT /api/menu/{id}/availability
     * Toggles the availability status of a menu item
     * 
     * @param id - ID of the menu item to toggle
     * @return ResponseEntity with updated item and HTTP status 200 OK,
     *         or 404 NOT FOUND if item doesn't exist
     */
    @PutMapping("/{id}/availability")
    public ResponseEntity<MenuItem> toggleAvailability(@PathVariable Long id) {
        for (MenuItem item : menuItems) {
            if (item.getId().equals(id)) {
                item.setAvailable(!item.isAvailable());
                return new ResponseEntity<>(item, HttpStatus.OK);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * DELETE /api/menu/{id}
     * Removes a menu item from the menu
     * 
     * @param id - ID of the menu item to delete
     * @return ResponseEntity with HTTP status 204 NO CONTENT if successful,
     *         HTTP status 404 NOT FOUND if item doesn't exist
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable Long id) {
        boolean removed = menuItems.removeIf(item -> item.getId().equals(id));

        if (removed) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Helper method to validate menu category
     * 
     * @param category - Category to validate
     * @return true if category is valid, false otherwise
     */
    private boolean isValidCategory(String category) {
        List<String> validCategories = List.of("Appetizer", "Main Course", "Dessert", "Beverage");
        return validCategories.contains(category);
    }

    /**
     * Helper method to check if menu item name already exists
     * 
     * @param name - Name to check
     * @return true if name exists, false otherwise
     */
    private boolean isNameExists(String name) {
        return menuItems.stream()
                .anyMatch(item -> item.getName().equalsIgnoreCase(name));
    }
}