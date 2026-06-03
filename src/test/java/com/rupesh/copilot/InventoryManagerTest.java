package com.rupesh.copilot;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InventoryManagerTest {

    @Test
    void addProductStoresProductInInventory() {
        InventoryManager inventoryManager = new InventoryManager();

        inventoryManager.addProduct(new Product(1, "Laptop", 10));

        assertThat(inventoryManager.listProducts())
                .extracting(Product::name)
                .containsExactly("Laptop");
    }

    @Test
    void addMultipleProductsStoresAllInInsertionOrder() {
        InventoryManager inventoryManager = new InventoryManager();

        inventoryManager.addProduct(new Product(1, "Laptop", 10));
        inventoryManager.addProduct(new Product(2, "Phone", 20));
        inventoryManager.addProduct(new Product(3, "Tablet", 15));

        assertThat(inventoryManager.listProducts())
                .extracting(Product::name)
                .containsExactly("Laptop", "Phone", "Tablet");
    }

    @Test
    void addProductWithDuplicateIdThrowsIllegalArgumentException() {
        InventoryManager inventoryManager = new InventoryManager();
        inventoryManager.addProduct(new Product(1, "Laptop", 10));

        assertThatThrownBy(() -> inventoryManager.addProduct(new Product(1, "Mouse", 5)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("1");
    }

    @Test
    void addNullProductThrowsNullPointerException() {
        InventoryManager inventoryManager = new InventoryManager();

        assertThatThrownBy(() -> inventoryManager.addProduct(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Product cannot be null");
    }

    @Test
    void listProductsReturnsEmptyListWhenNoProductsAdded() {
        InventoryManager inventoryManager = new InventoryManager();

        assertThat(inventoryManager.listProducts()).isEmpty();
    }

    @Test
    void listProductsReturnsUnmodifiableList() {
        InventoryManager inventoryManager = new InventoryManager();
        inventoryManager.addProduct(new Product(1, "Laptop", 10));

        List<Product> products = inventoryManager.listProducts();

        assertThatThrownBy(() -> products.add(new Product(2, "Phone", 5)))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void removeProductReturnsTrueAndRemovesExistingProduct() {
        InventoryManager inventoryManager = new InventoryManager();
        inventoryManager.addProduct(new Product(1, "Laptop", 10));
        inventoryManager.addProduct(new Product(2, "Phone", 20));

        boolean result = inventoryManager.removeProduct(1);

        assertThat(result).isTrue();
        assertThat(inventoryManager.listProducts())
                .extracting(Product::id)
                .containsExactly(2);
    }

    @Test
    void removeProductReturnsFalseWhenIdNotFound() {
        InventoryManager inventoryManager = new InventoryManager();
        inventoryManager.addProduct(new Product(1, "Laptop", 10));

        boolean result = inventoryManager.removeProduct(999);

        assertThat(result).isFalse();
        assertThat(inventoryManager.countProducts()).isEqualTo(1);
    }

    @Test
    void findProductByIdReturnsProductWhenExists() {
        InventoryManager inventoryManager = new InventoryManager();
        inventoryManager.addProduct(new Product(1, "Laptop", 10));

        Optional<Product> result = inventoryManager.findProductById(1);

        assertThat(result).isPresent();
        assertThat(result).hasValueSatisfying(p -> assertThat(p.name()).isEqualTo("Laptop"));
        assertThat(result).hasValueSatisfying(p -> assertThat(p.quantity()).isEqualTo(10));
    }

    @Test
    void findProductByIdReturnsEmptyWhenNotFound() {
        InventoryManager inventoryManager = new InventoryManager();
        inventoryManager.addProduct(new Product(1, "Laptop", 10));

        Optional<Product> result = inventoryManager.findProductById(999);

        assertThat(result).isEmpty();
    }

    @Test
    void findProductsByNameReturnsCaseInsensitiveMatches() {
        InventoryManager inventoryManager = new InventoryManager();
        inventoryManager.addProduct(new Product(1, "Laptop", 10));
        inventoryManager.addProduct(new Product(2, "laptop stand", 5));
        inventoryManager.addProduct(new Product(3, "Phone", 20));

        List<Product> result = inventoryManager.findProductsByName("LAPTOP");

        assertThat(result)
                .extracting(Product::name)
                .containsExactly("Laptop", "laptop stand");
    }

    @Test
    void findProductsByNameReturnsEmptyListWhenNoMatch() {
        InventoryManager inventoryManager = new InventoryManager();
        inventoryManager.addProduct(new Product(1, "Laptop", 10));

        List<Product> result = inventoryManager.findProductsByName("Tablet");

        assertThat(result).isEmpty();
    }

    @Test
    void findProductsByNameWithNullThrowsNullPointerException() {
        InventoryManager inventoryManager = new InventoryManager();

        assertThatThrownBy(() -> inventoryManager.findProductsByName(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Search term cannot be null");
    }

    @Test
    void updateProductQuantityReturnsTrueAndUpdatesQuantity() {
        InventoryManager inventoryManager = new InventoryManager();
        inventoryManager.addProduct(new Product(1, "Laptop", 10));

        boolean result = inventoryManager.updateProductQuantity(1, 25);

        assertThat(result).isTrue();
        assertThat(inventoryManager.findProductById(1))
                .hasValueSatisfying(p -> assertThat(p.quantity()).isEqualTo(25));
    }

    @Test
    void updateProductQuantityReturnsFalseWhenProductNotFound() {
        InventoryManager inventoryManager = new InventoryManager();

        boolean result = inventoryManager.updateProductQuantity(999, 10);

        assertThat(result).isFalse();
    }

    @Test
    void updateProductQuantityToZeroIsAllowed() {
        InventoryManager inventoryManager = new InventoryManager();
        inventoryManager.addProduct(new Product(1, "Laptop", 10));

        boolean result = inventoryManager.updateProductQuantity(1, 0);

        assertThat(result).isTrue();
        assertThat(inventoryManager.findProductById(1))
                .hasValueSatisfying(p -> assertThat(p.quantity()).isZero());
    }

    @Test
    void updateProductQuantityWithNegativeValueThrowsIllegalArgumentException() {
        InventoryManager inventoryManager = new InventoryManager();
        inventoryManager.addProduct(new Product(1, "Laptop", 10));

        assertThatThrownBy(() -> inventoryManager.updateProductQuantity(1, -5))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("-5");
    }

    @Test
    void countProductsReturnsCorrectCount() {
        InventoryManager inventoryManager = new InventoryManager();

        assertThat(inventoryManager.countProducts()).isZero();

        inventoryManager.addProduct(new Product(1, "Laptop", 10));
        inventoryManager.addProduct(new Product(2, "Phone", 20));

        assertThat(inventoryManager.countProducts()).isEqualTo(2);
    }

    @Test
    void countProductsDecreasesAfterRemoval() {
        InventoryManager inventoryManager = new InventoryManager();
        inventoryManager.addProduct(new Product(1, "Laptop", 10));
        inventoryManager.addProduct(new Product(2, "Phone", 20));

        inventoryManager.removeProduct(1);

        assertThat(inventoryManager.countProducts()).isEqualTo(1);
    }

    @Test
    void getLowStockProductsReturnsProductsAtOrBelowThreshold() {
        InventoryManager inventoryManager = new InventoryManager();
        inventoryManager.addProduct(new Product(1, "Laptop", 2));
        inventoryManager.addProduct(new Product(2, "Phone", 5));
        inventoryManager.addProduct(new Product(3, "Tablet", 10));

        List<Product> result = inventoryManager.getLowStockProducts(5);

        assertThat(result)
                .extracting(Product::name)
                .containsExactlyInAnyOrder("Laptop", "Phone");
    }

    @Test
    void getLowStockProductsReturnsEmptyListWhenAllAboveThreshold() {
        InventoryManager inventoryManager = new InventoryManager();
        inventoryManager.addProduct(new Product(1, "Laptop", 20));
        inventoryManager.addProduct(new Product(2, "Phone", 30));

        List<Product> result = inventoryManager.getLowStockProducts(5);

        assertThat(result).isEmpty();
    }

    @Test
    void containsProductReturnsTrueForExistingProduct() {
        InventoryManager inventoryManager = new InventoryManager();
        inventoryManager.addProduct(new Product(1, "Laptop", 10));

        assertThat(inventoryManager.containsProduct(1)).isTrue();
    }

    @Test
    void containsProductReturnsFalseForNonExistingProduct() {
        InventoryManager inventoryManager = new InventoryManager();

        assertThat(inventoryManager.containsProduct(999)).isFalse();
    }

    @Test
    void containsProductReturnsFalseAfterProductIsRemoved() {
        InventoryManager inventoryManager = new InventoryManager();
        inventoryManager.addProduct(new Product(1, "Laptop", 10));

        inventoryManager.removeProduct(1);

        assertThat(inventoryManager.containsProduct(1)).isFalse();
    }

    @Test
    void clearInventoryRemovesAllProducts() {
        InventoryManager inventoryManager = new InventoryManager();
        inventoryManager.addProduct(new Product(1, "Laptop", 10));
        inventoryManager.addProduct(new Product(2, "Phone", 20));

        inventoryManager.clearInventory();

        assertThat(inventoryManager.countProducts()).isZero();
        assertThat(inventoryManager.listProducts()).isEmpty();
    }

    @Test
    void productWithNegativeIdThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> new Product(-1, "Laptop", 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("-1");
    }

    @Test
    void productWithNullNameThrowsNullPointerException() {
        assertThatThrownBy(() -> new Product(1, null, 10))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Product name cannot be null");
    }

    @Test
    void productWithBlankNameThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> new Product(1, "  ", 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product name cannot be blank");
    }

    @Test
    void productWithNegativeQuantityThrowsIllegalArgumentException() {
        assertThatThrownBy(() -> new Product(1, "Laptop", -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("-1");
    }

    @Test
    void productWithZeroQuantityIsAllowed() {
        Product product = new Product(1, "Laptop", 0);

        assertThat(product.quantity()).isZero();
    }

    @Test
    void productWithQuantityCreatesNewProductWithUpdatedQuantity() {
        Product original = new Product(1, "Laptop", 10);

        Product updated = original.withQuantity(50);

        assertThat(updated.quantity()).isEqualTo(50);
        assertThat(updated.id()).isEqualTo(original.id());
        assertThat(updated.name()).isEqualTo(original.name());
        assertThat(original.quantity()).isEqualTo(10);
    }

    @Test
    void productWithQuantityWithNegativeValueThrowsIllegalArgumentException() {
        Product product = new Product(1, "Laptop", 10);

        assertThatThrownBy(() -> product.withQuantity(-5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void productsWithSameFieldsAreEqual() {
        Product product1 = new Product(1, "Laptop", 10);
        Product product2 = new Product(1, "Laptop", 10);

        assertThat(product1).isEqualTo(product2);
        assertThat(product1.hashCode()).isEqualTo(product2.hashCode());
    }

    @Test
    void productsWithDifferentFieldsAreNotEqual() {
        Product product1 = new Product(1, "Laptop", 10);
        Product product2 = new Product(2, "Phone", 10);

        assertThat(product1).isNotEqualTo(product2);
    }
}

