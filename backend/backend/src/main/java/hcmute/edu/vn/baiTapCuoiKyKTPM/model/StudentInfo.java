package hcmute.edu.vn.baiTapCuoiKyKTPM.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentInfo {
    @Field("full_name")
    private String fullName;

    private String dob;
    private String gender;

    @Field("class")
    private String studentClass;

    @Field("identity_card")
    private String identityCard;

    private String hometown;
    private String email;
    private String phone;
    private String major;
    private String faculty;
    private String photoUrl;
}
