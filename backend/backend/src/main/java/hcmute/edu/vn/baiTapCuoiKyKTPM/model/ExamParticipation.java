package hcmute.edu.vn.baiTapCuoiKyKTPM.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExamParticipation {
    @Field("exam_date")
    private String examDate;

    private String area;
    private String room;
    private String shift;
    private ViolationReport violation;
}
