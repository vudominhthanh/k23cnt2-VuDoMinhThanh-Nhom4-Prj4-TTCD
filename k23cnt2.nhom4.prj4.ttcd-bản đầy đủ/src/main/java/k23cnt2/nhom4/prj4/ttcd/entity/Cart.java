package k23cnt2.nhom4.prj4.ttcd.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(indexes = {
        @Index(name = "idx_cart_user_id", columnList = "user_id")
})
@Data
public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn( unique = true, nullable = false)
    private User user;

    @Column(insertable = false, updatable = false)
    private LocalDateTime updatedAt;
}