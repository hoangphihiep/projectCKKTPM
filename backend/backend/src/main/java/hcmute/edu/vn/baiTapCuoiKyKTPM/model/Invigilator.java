package hcmute.edu.vn.baiTapCuoiKyKTPM.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Invigilator {
    @Field("staff_id")
    private String staffId;

    private String name;
}
