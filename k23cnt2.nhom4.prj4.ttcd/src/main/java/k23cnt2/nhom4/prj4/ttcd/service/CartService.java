package k23cnt2.nhom4.prj4.ttcd.service;

import jakarta.transaction.Transactional;
import k23cnt2.nhom4.prj4.ttcd.dto.CartRequest;
import k23cnt2.nhom4.prj4.ttcd.entity.*;
import k23cnt2.nhom4.prj4.ttcd.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CartService {
    @Autowired private UserRepository userRepository;
    @Autowired private ProductOptionRepository productOptionRepository;
    @Autowired private ProductVariantRepository productVariantRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;

    private boolean isSameOptions(Set<ProductOption> a, Set<ProductOption> b) {
        Set<Integer> aIds = a.stream().map(ProductOption::getId).collect(Collectors.toSet());
        Set<Integer> bIds = b.stream().map(ProductOption::getId).collect(Collectors.toSet());
        return aIds.equals(bIds);
    }

    @Transactional
    public void addProductToCart(String email, CartRequest cartRequest) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUserId(user.getId()).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setUser(user);
            return cartRepository.save(newCart);
        });

        ProductVariant variant = productVariantRepository.findById(cartRequest.getVariantId()).orElseThrow(() -> new RuntimeException("Product Variant not found"));

        Set<ProductOption> selectedoptions = new HashSet<>();
        if (cartRequest.getOptionIds() != null && !cartRequest.getOptionIds().isEmpty()) {
            selectedoptions.addAll(productOptionRepository.findAllById(cartRequest.getOptionIds()));
        }

        if (cartRequest.getQuantity() <= 0) {
            throw new RuntimeException("Quantity must be > 0");
        }

        List<CartItem> currentItems = cartItemRepository.findByCartId(cart.getId());
        CartItem existingItem = null;

        for (CartItem cartItem : currentItems) {
            if (cartItem.getVariant().getId().equals(variant.getId()) && isSameOptions(cartItem.getOptions(), selectedoptions)) {
                existingItem = cartItem;
                break;
            }
        }

        if (existingItem != null) {
            existingItem.setQuantity(existingItem.getQuantity() + cartRequest.getQuantity());
            cartItemRepository.save(existingItem);
        } else {
            CartItem newCartItem = new CartItem();
            newCartItem.setCart(cart);
            newCartItem.setVariant(variant);
            newCartItem.setQuantity(cartRequest.getQuantity());
            newCartItem.setOptions(selectedoptions);
            cartItemRepository.save(newCartItem);
        }
    }
}
