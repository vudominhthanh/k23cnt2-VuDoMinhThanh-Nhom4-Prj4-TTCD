package k23cnt2.nhom4.prj4.ttcd.controller;

import k23cnt2.nhom4.prj4.ttcd.dto.CartRequest;
import k23cnt2.nhom4.prj4.ttcd.entity.Cart;
import k23cnt2.nhom4.prj4.ttcd.entity.CartItem;
import k23cnt2.nhom4.prj4.ttcd.entity.User;
import k23cnt2.nhom4.prj4.ttcd.repository.CartItemRepository;
import k23cnt2.nhom4.prj4.ttcd.repository.CartRepository;
import k23cnt2.nhom4.prj4.ttcd.repository.UserRepository;
import k23cnt2.nhom4.prj4.ttcd.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
public class CartApiController {
    @Autowired
    CartService cartService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody CartRequest cartRequest, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Login needed !");
        }
        String userEmail = authentication.getName();

        try {
            cartService.addProductToCart(userEmail, cartRequest);
            return ResponseEntity.ok("Thêm vào giỏ hàng thành công !");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getCart(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Login needed !");
        }

        try {
            User user = userRepository.findByEmail(authentication.getName()).orElseThrow(() -> new RuntimeException("User not found"));

            Cart cart = cartRepository.findByUserId(user.getId())
                    .orElseGet(() -> {
                        Cart newCart = new Cart();
                        newCart.setUser(user);
                        return cartRepository.save(newCart);
                    });
            List<CartItem> items = cartItemRepository.findByCartId(cart.getId());

            List<Map<String, Object>> result = items.stream().map(item -> {
                Map<String, Object> map = new HashMap<>();
                map.put("cartItemId", item.getId());
                map.put("quantity", item.getQuantity());
                map.put("variantId", item.getVariant().getId());
                map.put("variantName", item.getVariant().getSizeName()); // n4_sizeName
                map.put("extraPrice", item.getVariant().getExtraPrice()); // n4_extraPrice
                map.put("productName", item.getVariant().getProduct().getName());
                map.put("basePrice", item.getVariant().getProduct().getBasePrice());
                map.put("imageUrl", item.getVariant().getProduct().getImageUrl());

                List<Map<String, Object>> opts = item.getOptions().stream().map(o -> {
                    Map<String, Object> om = new HashMap<>();
                    om.put("id", o.getId());
                    om.put("name", o.getOptionName());
                    om.put("price", o.getAdditionalPrice());
                    return om;
                }).collect(Collectors.toList());
                map.put("options", opts);

                return map;
            }).collect(Collectors.toList());

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
