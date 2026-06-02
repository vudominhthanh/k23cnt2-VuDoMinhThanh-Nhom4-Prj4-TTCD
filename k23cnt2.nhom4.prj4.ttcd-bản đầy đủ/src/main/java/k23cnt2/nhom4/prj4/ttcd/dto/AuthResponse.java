package k23cnt2.nhom4.prj4.ttcd.dto;

import k23cnt2.nhom4.prj4.ttcd.entity.ENUMS;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private String message;
    private String role;

    public AuthResponse(String token, String fullName) {
        this.token = token;
        this.message = message;
        this.role = null;
    }
}
