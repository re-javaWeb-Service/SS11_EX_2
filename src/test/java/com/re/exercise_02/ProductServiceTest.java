package com.re.exercise_02;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Annotation để kích hoạt Mockito trong JUnit 5
@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @Mock
    private ProductService.ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private ProductService.Product sampleProduct;

    @BeforeEach
    void setUp() {
        // Tạo sản phẩm mẫu có tồn kho ban đầu là 50
        sampleProduct = new ProductService.Product("PROD-01", 50);
    }

    // Kịch bản 1: Thêm số lượng hợp lệ vào sản phẩm hiện có (Happy Path).
    @Test
    void testUpdateStock_AddValidQuantity() {
        when(productRepository.findById("PROD-01")).thenReturn(Optional.of(sampleProduct));

        int updatedStock = productService.updateStock("PROD-01", 20); // 50 + 20

        assertThat(updatedStock).isEqualTo(70);
        assertThat(sampleProduct.getStockQuantity()).isEqualTo(70);
    }

    // Kịch bản 2: Trừ số lượng hợp lệ từ sản phẩm hiện có (Happy Path).
    @Test
    void testUpdateStock_SubtractValidQuantity() {
        when(productRepository.findById("PROD-01")).thenReturn(Optional.of(sampleProduct));

        int updatedStock = productService.updateStock("PROD-01", -30); // 50 - 30

        assertThat(updatedStock).isEqualTo(20);
        assertThat(sampleProduct.getStockQuantity()).isEqualTo(20);
    }

    // Kịch bản 3: Cố gắng trừ số lượng lớn hơn tồn kho hiện có (Unhappy Path).
    @Test
    void testUpdateStock_SubtractMoreThanCurrentStock_ShouldThrowException() {
        when(productRepository.findById("PROD-01")).thenReturn(Optional.of(sampleProduct));

        // Trừ đi 60 trong khi chỉ có 50 -> Âm -> Báo lỗi
        assertThatThrownBy(() -> productService.updateStock("PROD-01", -60))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Resulting stock would be negative");

        // Đảm bảo không có lệnh save nào gọi xuống DB khi bị lỗi
        verify(productRepository, never()).save(any(ProductService.Product.class));
    }

    // Kịch bản 4: Cập nhật tồn kho cho sản phẩm không tồn tại (Unhappy Path).
    @Test
    void testUpdateStock_ProductNotFound_ShouldThrowException() {
        when(productRepository.findById("NON_EXIST")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateStock("NON_EXIST", 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product not found");

        // Đảm bảo không có lệnh save nào gọi xuống DB khi bị lỗi
        verify(productRepository, never()).save(any());
    }

    // Kịch bản 5: Kiểm tra xem phương thức save có được gọi đúng cách sau khi cập nhật không.
    @Test
    void testUpdateStock_VerifySaveMethodIsCalled() {
        when(productRepository.findById("PROD-01")).thenReturn(Optional.of(sampleProduct));

        productService.updateStock("PROD-01", 10);

        // Dùng verify của Mockito để xác nhận productRepository.save() được gọi đúng 1 lần với object sampleProduct
        verify(productRepository, times(1)).save(sampleProduct);
    }
}