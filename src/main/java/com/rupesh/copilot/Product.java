package com.rupesh.copilot;

import java.util.Objects;

/**
 * Immutable product record with id, name and quantity.
 * Provides value-based equals, hashCode and toString automatically.
 */
public record Product(int id, String name, int quantity) {

    public Product {
        if (id <= 0) {
            throw new IllegalArgumentException("Product ID must be positive: " + id);
        }
        Objects.requireNonNull(name, "Product name cannot be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be blank");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Product quantity cannot be negative: " + quantity);
        }
    }

    /**
     * Returns a new Product with the updated quantity.
     *
     * @param newQuantity the new quantity value
     * @return a new Product instance with the updated quantity
     * @throws IllegalArgumentException if newQuantity is negative
     */
    public Product withQuantity(int newQuantity) {
        return new Product(id, name, newQuantity);
    }

    @Override
    public String toString() {
        return "Product{id=%d, name='%s', quantity=%d}".formatted(id, name, quantity);
    }
}