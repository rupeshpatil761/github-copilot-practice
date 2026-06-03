package com.rupesh.copilot;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Manages an inventory of products supporting add, remove, update and query operations.
 * Products are uniquely identified by their ID.
 */
public class InventoryManager {

    private final Map<Integer, Product> productById = new LinkedHashMap<>();

    /**
     * Adds a product to the inventory.
     *
     * @param product the product to add
     * @throws NullPointerException     if product is null
     * @throws IllegalArgumentException if a product with the same ID already exists
     */
    public void addProduct(Product product) {
        Objects.requireNonNull(product, "Product cannot be null");
        if (productById.containsKey(product.id())) {
            throw new IllegalArgumentException(
                    "Product with ID %d already exists".formatted(product.id()));
        }
        productById.put(product.id(), product);
    }

    /**
     * Removes the product with the given ID.
     *
     * @param id the product ID to remove
     * @return true if removed, false if not found
     */
    public boolean removeProduct(int id) {
        return productById.remove(id) != null;
    }

    /**
     * Returns a list of all products in insertion order.
     *
     * @return unmodifiable list of products
     */
    public List<Product> listProducts() {
        return List.copyOf(productById.values());
    }

    /**
     * Finds a product by its ID.
     *
     * @param id the product ID to look up
     * @return an Optional containing the product, or empty if not found
     */
    public Optional<Product> findProductById(int id) {
        return Optional.ofNullable(productById.get(id));
    }

    /**
     * Finds all products whose name contains the given string (case-insensitive).
     *
     * @param namePart the substring to search for
     * @return list of matching products
     * @throws NullPointerException if namePart is null
     */
    public List<Product> findProductsByName(String namePart) {
        Objects.requireNonNull(namePart, "Search term cannot be null");
        return productById.values().stream()
                .filter(p -> p.name().toLowerCase().contains(namePart.toLowerCase()))
                .toList();
    }

    /**
     * Updates the quantity of a product identified by ID.
     * Since Product is immutable, the old entry is replaced with a new one.
     *
     * @param id          the product ID
     * @param newQuantity the new quantity
     * @return true if updated, false if product not found
     * @throws IllegalArgumentException if newQuantity is negative
     */
    public boolean updateProductQuantity(int id, int newQuantity) {
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative: " + newQuantity);
        }
        Product existing = productById.get(id);
        if (existing == null) {
            return false;
        }
        productById.put(id, existing.withQuantity(newQuantity));
        return true;
    }

    /**
     * Returns the total number of products in the inventory.
     *
     * @return product count
     */
    public int countProducts() {
        return productById.size();
    }

    /**
     * Returns all products whose quantity is at or below the given threshold.
     *
     * @param threshold the maximum quantity to consider low stock
     * @return list of low stock products
     */
    public List<Product> getLowStockProducts(int threshold) {
        return productById.values().stream()
                .filter(p -> p.quantity() <= threshold)
                .toList();
    }

    /**
     * Returns whether the inventory contains a product with the given ID.
     *
     * @param id the product ID
     * @return true if found
     */
    public boolean containsProduct(int id) {
        return productById.containsKey(id);
    }

    /**
     * Removes all products from the inventory.
     */
    public void clearInventory() {
        productById.clear();
    }
}