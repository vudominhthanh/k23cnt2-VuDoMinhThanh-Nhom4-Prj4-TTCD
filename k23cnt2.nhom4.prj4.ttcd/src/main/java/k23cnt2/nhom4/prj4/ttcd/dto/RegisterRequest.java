package k23cnt2.nhom4.prj4.ttcd.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String fullName;
    private String password;
    private String phone;
}
