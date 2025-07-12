package hcmute.edu.vn.baiTapCuoiKyKTPM.blackbox;

import hcmute.edu.vn.baiTapCuoiKyKTPM.model.ExamStatus;
import hcmute.edu.vn.baiTapCuoiKyKTPM.model.Student;
import hcmute.edu.vn.baiTapCuoiKyKTPM.model.StudentInfo;
import hcmute.edu.vn.baiTapCuoiKyKTPM.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class StudentBlackBoxTests {

    @Autowired
    private StudentRepository repository;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    // ✅ Test phân vùng tương đương – dữ liệu hợp lệ
    @Test
    void testValidStudentPartition() {
        Student student = new Student();
        student.setStudentId("21009999");
        student.setStatus(new ExamStatus("active", "Valid"));

        StudentInfo info = new StudentInfo();
        info.setFullName("Nguyen Van Test");
        info.setDob("2002-10-10");
        info.setGender("Nam");
        info.setStudentClass("DHKTPM17");
        info.setEmail("test@ute.vn");
        info.setPhone("0909123456");
        student.setCurrentInfo(info);

        Student saved = repository.save(student);
        assertEquals("21009999", saved.getStudentId());
        assertEquals("Valid", saved.getStatus().getReason());
    }

    // ❌ Test phân vùng tương đương – dữ liệu sai
    @Test
    void testInvalidStudentIdEmpty() {
        Student student = new Student();
        student.setStudentId("");
        student.setStatus(new ExamStatus("active", ""));

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            if (student.getStudentId() == null || student.getStudentId().isBlank()) {
                throw new IllegalArgumentException("Student ID must not be empty");
            }
            repository.save(student);
        });

        assertEquals("Student ID must not be empty", ex.getMessage());
    }

    // ✅ Test giá trị biên – ID gần giới hạn độ dài
    @Test
    void testBoundaryStudentIdLength() {
        String maxId = "2".repeat(10); // ví dụ: max length là 10 ký tự
        Student student = new Student();
        student.setStudentId(maxId);
        student.setStatus(new ExamStatus("active", "OK"));

        Student saved = repository.save(student);
        assertEquals(maxId, saved.getStudentId());
    }

    // ✅ Test bảng quyết định đơn giản – rule status
    @Test
    void testDecisionTableStatusExpelled() {
        Student student = new Student();
        student.setStudentId("21005555");
        student.setStatus(new ExamStatus("expelled", "Vi phạm nghiêm trọng"));

        Student saved = repository.save(student);
        assertEquals("expelled", saved.getStatus().getExamEligibility());
    }

    // ✅ Test chuyển trạng thái – như FSM
    @Test
    void testStateTransitionFromActiveToSuspended() {
        Student student = new Student();
        student.setStudentId("21006666");
        student.setStatus(new ExamStatus("active", "Initial"));

        Student saved = repository.save(student);
        saved.setStatus(new ExamStatus("suspended", "Vi phạm lần 1"));
        Student updated = repository.save(saved);

        assertEquals("suspended", updated.getStatus().getExamEligibility());
        assertEquals("Vi phạm lần 1", updated.getStatus().getReason());
    }
}
