package com.minishop.minishopspring.service;

import com.minishop.minishopspring.entity.CartItem;
import com.minishop.minishopspring.entity.Product;
import com.minishop.minishopspring.entity.User;
import com.minishop.minishopspring.enums.Role;
import com.minishop.minishopspring.integration.BaseIntegrationTest;
import com.minishop.minishopspring.repository.CartItemRepository;
import com.minishop.minishopspring.repository.OrderRepository;
import com.minishop.minishopspring.repository.ProductRepository;
import com.minishop.minishopspring.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class CheckoutConcurrencyIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CheckoutService checkoutService;

    @MockitoBean
    private CurrentUserService currentUserService;


    @Test
    void shouldPreventOversellingWhenTwoUsersCheckoutAtSameTime() throws InterruptedException {
        User john = new User();
        john.setUsername("john");
        john.setEmail("john@gmail.com");
        john.setPassword("123456");
        john.setRole(Role.CUSTOMER);
        User savedJohn = userRepository.save(john);

        User anna = new User();
        anna.setUsername("anna");
        anna.setEmail("anna@gmail.com");
        anna.setPassword("123456");
        anna.setRole(Role.CUSTOMER);
        User savedAnna = userRepository.save(anna);

        Product iphone = new Product();
        iphone.setName("iPhone 16");
        iphone.setPrice(new BigDecimal("20000000"));
        iphone.setQuantity(5);

        iphone = productRepository.save(iphone);

        CartItem johnCart =
                new CartItem(savedJohn, iphone, 4);

        cartItemRepository.save(johnCart);

        CartItem annaCart =
                new CartItem(savedAnna, iphone, 4);

        cartItemRepository.save(annaCart);

        when(currentUserService.getCurrentUser())
                .thenAnswer(invocation -> {
                    String threadName = Thread.currentThread().getName();

                    if (threadName.equals("john-thread")) {
                        return savedJohn;
                    }

                    if (threadName.equals("anna-thread")) {
                        return savedAnna;
                    }

                    throw new IllegalStateException(
                            "Unknown thread: " + threadName
                    );
                });

        CountDownLatch startGate = new CountDownLatch(1);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        Thread johnThread = new Thread(() -> {
            try {
                startGate.await();

                checkoutService.checkout();

                successCount.incrementAndGet();
            } catch (Exception e) {
                failureCount.incrementAndGet();

                System.out.println(
                        "John checkout failed: " + e.getMessage()
                );
            }
        }, "john-thread");

        Thread annaThread = new Thread(() -> {
            try {
                startGate.await();

                checkoutService.checkout();

                successCount.incrementAndGet();
            } catch (Exception e) {
                failureCount.incrementAndGet();

                System.out.println(
                        "Anna checkout failed: " + e.getMessage()
                );
            }
        }, "anna-thread");

        johnThread.start();
        annaThread.start();

        startGate.countDown();

        johnThread.join();
        annaThread.join();

        Product productInDb =
                productRepository.findById(iphone.getId())
                        .orElseThrow();

        assertEquals(
                1,
                successCount.get(),
                "SAI successCount"
        );

        assertEquals(
                1,
                failureCount.get(),
                "SAI failureCount"
        );

        assertEquals(
                1,
                orderRepository.count(),
                "SAI orderCount"
        );

        assertEquals(
                1,
                productInDb.getQuantity(),
                "SAI stock"
        );
    }

}
