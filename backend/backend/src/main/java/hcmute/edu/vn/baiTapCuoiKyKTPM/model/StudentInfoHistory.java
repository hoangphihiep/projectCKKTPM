package hcmute.edu.vn.baiTapCuoiKyKTPM.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
public class StudentInfoHistory extends StudentInfo{
    @Field("updated_at")
    private LocalDateTime updatedAt;

    public StudentInfoHistory(String fullName, String dob, String gender,
                              String studentClass, String identityCard, String hometown, String email, String phone,
                              String major, String faculty, LocalDateTime updatedAt) {
        super(fullName, dob, gender, studentClass, identityCard, hometown, email, phone, major, faculty);
        this.updatedAt = updatedAt;
    }
}
