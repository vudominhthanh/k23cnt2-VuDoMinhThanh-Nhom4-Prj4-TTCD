package k23cnt2.nhom4.prj4.ttcd.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn()
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn()
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn()
    private Order order;

    private Integer rating;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(insertable = false, updatable = false)
    private LocalDateTime createdAt;
}