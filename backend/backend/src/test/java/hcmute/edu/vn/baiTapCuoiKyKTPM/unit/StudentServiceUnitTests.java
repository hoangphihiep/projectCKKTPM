package hcmute.edu.vn.baiTapCuoiKyKTPM.unit;

import hcmute.edu.vn.baiTapCuoiKyKTPM.dto.response.StudentSearchResponse;
import hcmute.edu.vn.baiTapCuoiKyKTPM.model.Student;
import hcmute.edu.vn.baiTapCuoiKyKTPM.model.StudentInfo;
import hcmute.edu.vn.baiTapCuoiKyKTPM.repository.StudentRepository;
import hcmute.edu.vn.baiTapCuoiKyKTPM.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StudentServiceUnitTests {

    @Mock
    private StudentRepository studentRepository;

    private StudentService studentService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        studentService = new StudentService(studentRepository);
    }

    @Test
    void testGetStudentDetailById_found() {
        Student s = new Student();
        s.setStudentId("21000001");
        StudentInfo info = new StudentInfo();
        info.setFullName("Nguyen Van A");
        s.setCurrentInfo(info);

        when(studentRepository.findById("abc123")).thenReturn(Optional.of(s));

        var result = studentService.getStudentDetailById("abc123");
        assertTrue(result.isPresent());
        assertEquals("Nguyen Van A", result.get().getCurrentInfo().getFullName());
    }

    @Test
    void testGetStudentDetailById_notFound() {
        when(studentRepository.findById("nonexistent")).thenReturn(Optional.empty());
        var result = studentService.getStudentDetailById("nonexistent");
        assertTrue(result.isEmpty());
    }



    @Test
    void testSearchStudents_noMatch() {
        when(studentRepository.findAll()).thenReturn(List.of());
        var result = studentService.searchStudents("notfound");
        assertTrue(result.isEmpty());
    }
}
