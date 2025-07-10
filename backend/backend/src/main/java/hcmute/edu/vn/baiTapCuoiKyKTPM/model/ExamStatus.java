package hcmute.edu.vn.baiTapCuoiKyKTPM.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamStatus {
    @Field("exam_eligibility")
    private String examEligibility; // active, suspended, expelled

    private String reason;
}
