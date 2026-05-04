package k23cnt2.nhom4.prj4.ttcd.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "user_addresses", indexes = {
        @Index(name = "idx_addr_user_id", columnList = "user_id")
})
@Data
public class UserAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "receiver_name", length = 100)
    private String receiverName;

    @Column(name = "receiver_phone", length = 20)
    private String receiverPhone;

    private String province;
    private String district;
    private String ward;

    @Column(name = "address_detail", columnDefinition = "TEXT")
    private String addressDetail;

    @Column(name = "is_default")
    private Boolean isDefault = false;
}