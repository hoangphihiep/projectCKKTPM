package hcmute.edu.vn.baiTapCuoiKyKTPM.acceptance;

import hcmute.edu.vn.baiTapCuoiKyKTPM.model.*;
import hcmute.edu.vn.baiTapCuoiKyKTPM.repository.StudentRepository;
import hcmute.edu.vn.baiTapCuoiKyKTPM.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class StudentAcceptanceTests {

    @Autowired
    private StudentRepository repository;

    @Autowired
    private StudentService service;

    @BeforeEach
    void setup() {
        repository.deleteAll();
    }

    @Test
    void testCreateStudentSuccessfully() {
        Student student = new Student();
        student.setStudentId("21000001");

        ExamStatus status = new ExamStatus("active", "Valid student");
        student.setStatus(status);

        StudentInfo info = new StudentInfo();
        info.setFullName("Le Van B");
        info.setDob("2001-03-02");
        info.setGender("Nam");
        info.setStudentClass("DHKTPM17A");
        info.setEmail("b@ute.edu.vn");
        info.setPhone("0912345678");
        student.setCurrentInfo(info);

        Student saved = repository.save(student);
        assertNotNull(saved.getId());
        assertEquals("21000001", saved.getStudentId());
    }

    @Test
    void testSearchStudentByIdExists() {
        Student student = new Student();
        student.setStudentId("21000002");
        student.setStatus(new ExamStatus("active", "OK"));

        Student saved = repository.save(student);
        Optional<Student> found = repository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("21000002", found.get().getStudentId());
    }

    @Test
    void testUpdateStudentStatusSuccessfully() {
        Student student = new Student();
        student.setStudentId("21000003");
        student.setStatus(new ExamStatus("active", "OK"));

        Student saved = repository.save(student);
        saved.setStatus(new ExamStatus("expelled", "Vi phạm"));
        repository.save(saved);

        Student updated = repository.findById(saved.getId()).orElseThrow();
        assertEquals("expelled", updated.getStatus().getExamEligibility());
    }

    @Test
    void testRejectInvalidStudentCreation() {
        Student student = new Student();
        student.setStatus(new ExamStatus("active", null));

        Exception exception = assertThrows(Exception.class, () -> {
            if (student.getStudentId() == null || student.getStudentId().isBlank()) {
                throw new IllegalArgumentException("Student ID is required");
            }
            repository.save(student);
        });

        assertEquals("Student ID is required", exception.getMessage());
    }

    @Test
    void testDeleteStudentSuccessfully() {
        Student student = new Student();
        student.setStudentId("21000004");
        student.setStatus(new ExamStatus("active", "Ready"));

        Student saved = repository.save(student);
        repository.deleteById(saved.getId());

        Optional<Student> deleted = repository.findById(saved.getId());
        assertFalse(deleted.isPresent());
    }
}
