package hcmute.edu.vn.baiTapCuoiKyKTPM.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ViolationReport {
    @Field("has_violation")
    private boolean hasViolation;

    private String level;
    private String description;
    private List<Invigilator> invigilators;
    private String notes;
}
