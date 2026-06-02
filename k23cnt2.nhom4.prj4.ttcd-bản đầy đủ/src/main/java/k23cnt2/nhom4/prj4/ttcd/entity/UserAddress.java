package k23cnt2.nhom4.prj4.ttcd.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(indexes = {
        @Index(name = "idx_addr_user_id", columnList = "user_id")
})
@Data
public class UserAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @Column(length = 100)
    private String receiverName;

    @Column(length = 20)
    private String receiverPhone;

    private String province;
    private String district;
    private String ward;

    @Column(columnDefinition = "TEXT")
    private String addressDetail;

    @Column()
    private Boolean isDefault = false;
}