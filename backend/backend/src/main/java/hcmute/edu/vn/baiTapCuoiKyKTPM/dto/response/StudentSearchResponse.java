package hcmute.edu.vn.baiTapCuoiKyKTPM.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentSearchResponse {
    private String id;
    private String studentId;
    private String fullName;
    private String studentClass;
    private String major;
    private String faculty;
    private String examEligibility;
    private String photoUrl; // URL ảnh sinh viên
}
