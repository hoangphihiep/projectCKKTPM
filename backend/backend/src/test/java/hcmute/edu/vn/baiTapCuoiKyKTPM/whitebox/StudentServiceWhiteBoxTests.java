// 📁 src/test/java/hcmute/edu/vn/baiTapCuoiKyKTPM/whitebox/StudentServiceWhiteBoxTests.java

package hcmute.edu.vn.baiTapCuoiKyKTPM.whitebox;

import hcmute.edu.vn.baiTapCuoiKyKTPM.model.Student;
import hcmute.edu.vn.baiTapCuoiKyKTPM.model.StudentInfo;
import hcmute.edu.vn.baiTapCuoiKyKTPM.repository.StudentRepository;
import hcmute.edu.vn.baiTapCuoiKyKTPM.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StudentServiceWhiteBoxTests {

    private StudentRepository studentRepository;
    private StudentService studentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        studentRepository = mock(StudentRepository.class);
        studentService = new StudentService(studentRepository);
    }

    private Student createStudent(String id, String fullName) {
        Student s = new Student();
        s.setStudentId(id);
        StudentInfo info = new StudentInfo();
        info.setFullName(fullName);
        s.setCurrentInfo(info);
        return s;
    }

    @Test
    void testSearchStudents_matchStudentId() {
        Student s = createStudent("21001234", "Nguyen Van A");
        when(studentRepository.findAll()).thenReturn(List.of(s));

        var results = studentService.searchStudents("21001234");
        assertEquals(1, results.size(), "Expected 1 match for studentId");
        assertEquals("21001234", results.get(0).getStudentId());
    }

    @Test
    void testSearchStudents_matchFullName() {
        Student s = createStudent("21001235", "Tran Thi B");
        when(studentRepository.findAll()).thenReturn(List.of(s));

        var results = studentService.searchStudents("Tran Thi");
        assertEquals(1, results.size(), "Expected 1 match for full name");
        assertTrue(results.get(0).getFullName().toLowerCase().contains("tran thi".toLowerCase()));
    }

    @Test
    void testSearchStudents_caseInsensitiveFullName() {
        Student s = createStudent("21001236", "Le Van C");
        when(studentRepository.findAll()).thenReturn(List.of(s));

        var results = studentService.searchStudents("le van c");
        assertEquals(1, results.size(), "Expected 1 match with case-insensitive name");
        assertEquals("Le Van C", results.get(0).getFullName());
    }

    @Test
    void testSearchStudents_nullCurrentInfo() {
        Student s = new Student();
        s.setStudentId("21001237");
        s.setCurrentInfo(null);

        when(studentRepository.findAll()).thenReturn(List.of(s));

        var results = studentService.searchStudents("21001237");
        assertEquals(0, results.size());
    }

    @Test
    void testSearchStudents_emptyList() {
        when(studentRepository.findAll()).thenReturn(List.of());
        var results = studentService.searchStudents("abc");
        assertTrue(results.isEmpty());
    }

    @Test
    void testSearchStudents_nullQuery() {
        Student s = createStudent("21001238", "Nguyen Van D");
        when(studentRepository.findAll()).thenReturn(List.of(s));

        var results = studentService.searchStudents(null);
        assertEquals(0, results.size());
    }

    @Test
    void testSearchStudents_mismatchQuery() {
        Student s = createStudent("21001239", "Pham Van E");
        when(studentRepository.findAll()).thenReturn(List.of(s));

        var results = studentService.searchStudents("nonexistent");
        assertEquals(0, results.size());
    }
}
