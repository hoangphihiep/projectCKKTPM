package hcmute.edu.vn.baiTapCuoiKyKTPM.unit;

import hcmute.edu.vn.baiTapCuoiKyKTPM.dto.response.StudentDetailResponse;
import hcmute.edu.vn.baiTapCuoiKyKTPM.dto.response.StudentSearchResponse;
import hcmute.edu.vn.baiTapCuoiKyKTPM.model.*;
import hcmute.edu.vn.baiTapCuoiKyKTPM.repository.StudentRepository;
import hcmute.edu.vn.baiTapCuoiKyKTPM.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Kiểm thử đơn vị cho StudentService
 * Kiểm tra từng phương thức một cách độc lập
 */
@ExtendWith(MockitoExtension.class)
class StudentServiceUnitTests {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student mockStudent;
    private StudentInfo mockStudentInfo;
    private ExamParticipation mockExamParticipation;
    private ExamStatus mockExamStatus;

    @BeforeEach
    void setUp() {
        // Tạo dữ liệu mock
        mockStudentInfo = new StudentInfo();
        mockStudentInfo.setFullName("Nguyễn Văn A");
        mockStudentInfo.setStudentClass("20DTHD1");
        mockStudentInfo.setMajor("Công nghệ thông tin");
        mockStudentInfo.setFaculty("Công nghệ thông tin");
        mockStudentInfo.setDob("01/01/2000");
        mockStudentInfo.setGender("Nam");
        mockStudentInfo.setPhotoUrl("/student1.jpg");

        mockExamStatus = new ExamStatus();
        mockExamStatus.setExamEligibility("active");
        mockExamStatus.setReason(null);

        mockExamParticipation = new ExamParticipation();
        mockExamParticipation.setExamDate("2025-06-25");
        mockExamParticipation.setArea("A2");
        mockExamParticipation.setShift("Ca 1");
        mockExamParticipation.setRoom("P.201");

        mockStudent = new Student();
        mockStudent.setId("1");
        mockStudent.setStudentId("20110001");
        mockStudent.setCurrentInfo(mockStudentInfo);
        mockStudent.setStatus(mockExamStatus);
        mockStudent.setExamParticipations(Arrays.asList(mockExamParticipation));
        mockStudent.setCreatedAt(LocalDateTime.now());
        mockStudent.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void testSearchStudents_WithValidInput_ShouldReturnStudentList() {
        // Arrange
        String searchTerm = "Nguyễn Văn A";
        List<Student> mockStudents = Arrays.asList(mockStudent);
        when(studentRepository.findByStudentIdOrFullNameContaining(searchTerm))
                .thenReturn(mockStudents);

        // Act
        List<StudentSearchResponse> result = studentService.searchStudents(searchTerm);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("20110001", result.get(0).getStudentId());
        assertEquals("Nguyễn Văn A", result.get(0).getFullName());
        verify(studentRepository, times(1)).findByStudentIdOrFullNameContaining(searchTerm);
    }

    @Test
    void testSearchStudents_WithEmptyResult_ShouldReturnEmptyList() {
        // Arrange
        String searchTerm = "NonExistentStudent";
        when(studentRepository.findByStudentIdOrFullNameContaining(searchTerm))
                .thenReturn(Collections.emptyList());

        // Act
        List<StudentSearchResponse> result = studentService.searchStudents(searchTerm);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(studentRepository, times(1)).findByStudentIdOrFullNameContaining(searchTerm);
    }

    @Test
    void testGetExamAreasByDate_WithValidDate_ShouldReturnAreaList() {
        // Arrange
        LocalDate examDate = LocalDate.of(2025, 6, 25);
        List<Student> mockStudents = Arrays.asList(mockStudent);
        when(studentRepository.findByExamDate("2025-06-25")).thenReturn(mockStudents);

        // Act
        List<String> result = studentService.getExamAreasByDate(examDate);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("A2", result.get(0));
        verify(studentRepository, times(1)).findByExamDate("2025-06-25");
    }

    @Test
    void testGetExamShiftsByDateAndArea_WithValidParameters_ShouldReturnShiftList() {
        // Arrange
        LocalDate examDate = LocalDate.of(2025, 6, 25);
        String area = "A2";
        List<Student> mockStudents = Arrays.asList(mockStudent);
        when(studentRepository.findByExamDateAndArea("2025-06-25", area))
                .thenReturn(mockStudents);

        // Act
        List<String> result = studentService.getExamShiftsByDateAndArea(examDate, area);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Ca 1", result.get(0));
        verify(studentRepository, times(1)).findByExamDateAndArea("2025-06-25", area);
    }

    @Test
    void testGetStudentDetail_WithValidStudentId_ShouldReturnStudentDetail() {
        // Arrange
        String studentId = "20110001";
        when(studentRepository.findByStudentId(studentId)).thenReturn(Optional.of(mockStudent));

        // Act
        Optional<StudentDetailResponse> result = studentService.getStudentDetail(studentId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("20110001", result.get().getStudentId());
        assertEquals("Nguyễn Văn A", result.get().getCurrentInfo().getFullName());
        verify(studentRepository, times(1)).findByStudentId(studentId);
    }

    @Test
    void testGetStudentDetail_WithInvalidStudentId_ShouldReturnEmpty() {
        // Arrange
        String studentId = "99999999";
        when(studentRepository.findByStudentId(studentId)).thenReturn(Optional.empty());

        // Act
        Optional<StudentDetailResponse> result = studentService.getStudentDetail(studentId);

        // Assert
        assertFalse(result.isPresent());
        verify(studentRepository, times(1)).findByStudentId(studentId);
    }

    @Test
    void testGetStudentsByExamRoom_WithValidParameters_ShouldReturnStudentList() {
        // Arrange
        LocalDate examDate = LocalDate.of(2025, 6, 25);
        String area = "A2";
        String shift = "Ca 1";
        String room = "P.201";
        List<Student> mockStudents = Arrays.asList(mockStudent);

        when(studentRepository.findByExamDateAndAreaAndShiftAndRoom(
                "2025-06-25", area, shift, room)).thenReturn(mockStudents);

        // Act
        List<StudentSearchResponse> result = studentService.getStudentsByExamRoom(
                examDate, area, shift, room);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("20110001", result.get(0).getStudentId());
        verify(studentRepository, times(1))
                .findByExamDateAndAreaAndShiftAndRoom("2025-06-25", area, shift, room);
    }
    @Test
    void testSearchStudents_WithNullInput_ShouldHandleGracefully() {
        // Act
        List<StudentSearchResponse> result = studentService.searchStudents(null);

        // Assert
        assertNotNull(result);
        verify(studentRepository, times(1)).findByStudentIdOrFullNameContaining(null);
    }

    @Test
    void testGetStudentDetailById_WithValidId_ShouldReturnStudentDetail() {
        // Arrange
        String id = "1";
        when(studentRepository.findById(id)).thenReturn(Optional.of(mockStudent));

        // Act
        Optional<StudentDetailResponse> result = studentService.getStudentDetailById(id);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("1", result.get().getId());
        assertEquals("20110001", result.get().getStudentId());
        verify(studentRepository, times(1)).findById(id);
    }

    @Test
    void testGetStudentDetailById_found() {
        Student s = new Student();
        s.setStudentId("21000001");
        StudentInfo info = new StudentInfo();
        info.setFullName("Nguyễn Văn A");
        s.setCurrentInfo(info);

        when(studentRepository.findById("abc123")).thenReturn(Optional.of(s));

        var result = studentService.getStudentDetailById("abc123");
        assertTrue(result.isPresent());
        assertEquals("Nguyễn Văn A", result.get().getCurrentInfo().getFullName());
    }

    @Test
    void testGetStudentDetailById_notFound() {
        when(studentRepository.findById("nonexistent")).thenReturn(Optional.empty());
        var result = studentService.getStudentDetailById("nonexistent");
        assertTrue(result.isEmpty());
    }
}
