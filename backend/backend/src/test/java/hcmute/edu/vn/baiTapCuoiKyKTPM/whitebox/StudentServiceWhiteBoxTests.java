// 📁 src/test/java/hcmute/edu/vn/baiTapCuoiKyKTPM/whitebox/StudentServiceWhiteBoxTests.java

package hcmute.edu.vn.baiTapCuoiKyKTPM.whitebox;

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
 * Kiểm thử hộp trắng - Kiểm tra cấu trúc nội bộ và logic của code
 * Kiểm tra các đường dẫn thực thi, điều kiện rẽ nhánh, vòng lặp
 */
@ExtendWith(MockitoExtension.class)
class StudentServiceWhiteBoxTests {

    @Mock
    private StudentRepository studentRepository;

    @InjectMocks
    private StudentService studentService;

    private Student mockStudent;
    private StudentInfo mockStudentInfo;
    private ExamParticipation mockExamParticipation;

    @BeforeEach
    void setUp() {
        mockStudentInfo = new StudentInfo();
        mockStudentInfo.setFullName("Nguyễn Văn A");
        mockStudentInfo.setStudentClass("20DTHD1");
        mockStudentInfo.setMajor("Công nghệ thông tin");
        mockStudentInfo.setFaculty("Công nghệ thông tin");

        mockExamParticipation = new ExamParticipation();
        mockExamParticipation.setExamDate("2025-06-25");
        mockExamParticipation.setArea("A2");
        mockExamParticipation.setShift("Ca 1");
        mockExamParticipation.setRoom("P.201");

        mockStudent = new Student();
        mockStudent.setId("1");
        mockStudent.setStudentId("20110001");
        mockStudent.setCurrentInfo(mockStudentInfo);
        mockStudent.setExamParticipations(Arrays.asList(mockExamParticipation));
    }

    @Test
    void testSearchStudents_BranchCoverage_EmptyQuery() {
        // Kiểm tra nhánh: query rỗng hoặc null

        // Test với null
        when(studentRepository.findByStudentIdOrFullNameContaining(null))
                .thenReturn(Collections.emptyList());

        List<StudentSearchResponse> result1 = studentService.searchStudents(null);
        assertNotNull(result1);
        assertTrue(result1.isEmpty());

        // Test với chuỗi rỗng
        when(studentRepository.findByStudentIdOrFullNameContaining(""))
                .thenReturn(Collections.emptyList());

        List<StudentSearchResponse> result2 = studentService.searchStudents("");
        assertNotNull(result2);
        assertTrue(result2.isEmpty());

        // Test với chuỗi chỉ có khoảng trắng
        when(studentRepository.findByStudentIdOrFullNameContaining("   "))
                .thenReturn(Collections.emptyList());

        List<StudentSearchResponse> result3 = studentService.searchStudents("   ");
        assertNotNull(result3);
        assertTrue(result3.isEmpty());
    }
    @Test
    void testGetExamAreasByDate_BranchCoverage_ValidDate() {
        // Kiểm tra nhánh: ngày hợp lệ

        LocalDate examDate = LocalDate.of(2025, 6, 25);
        List<Student> mockStudents = Arrays.asList(mockStudent);
        when(studentRepository.findByExamDate("2025-06-25")).thenReturn(mockStudents);

        List<String> result = studentService.getExamAreasByDate(examDate);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("A2", result.get(0));
        verify(studentRepository, times(1)).findByExamDate("2025-06-25");
    }
    @Test
    void testGetStudentsByExamRoom_PathCoverage_AllParameters() {
        // Kiểm tra đường dẫn: tất cả tham số hợp lệ

        LocalDate examDate = LocalDate.of(2025, 6, 25);
        String area = "A2";
        String shift = "Ca 1";
        String room = "P.201";

        List<Student> mockStudents = Arrays.asList(mockStudent);
        when(studentRepository.findByExamDateAndAreaAndShiftAndRoom(
                "2025-06-25", area, shift, room)).thenReturn(mockStudents);

        List<StudentSearchResponse> result = studentService.getStudentsByExamRoom(
                examDate, area, shift, room);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("20110001", result.get(0).getStudentId());

        verify(studentRepository, times(1))
                .findByExamDateAndAreaAndShiftAndRoom("2025-06-25", area, shift, room);
    }

    @Test
    void testDataTransformation_InternalLogic() {
        // Kiểm tra logic chuyển đổi dữ liệu nội bộ

        // Tạo student với thông tin phức tạp
        Student complexStudent = new Student();
        complexStudent.setId("complex1");
        complexStudent.setStudentId("20110002");

        StudentInfo info = new StudentInfo();
        info.setFullName("Trần Thị B");
        info.setStudentClass("20DTHD2");
        info.setMajor("Khoa học máy tính");
        info.setFaculty("Công nghệ thông tin");
        info.setDob("15/05/2000");
        info.setGender("Nữ");
        info.setPhotoUrl("/student2.jpg");
        complexStudent.setCurrentInfo(info);

        ExamStatus status = new ExamStatus();
        status.setExamEligibility("suspended");
        status.setReason("Vi phạm quy chế thi");
        complexStudent.setStatus(status);

        List<ExamParticipation> participations = new ArrayList<>();
        ExamParticipation participation1 = new ExamParticipation();
        participation1.setExamDate("2025-06-25");
        participation1.setArea("B1");
        participation1.setShift("Ca 2");
        participation1.setRoom("P.301");
        participations.add(participation1);

        ExamParticipation participation2 = new ExamParticipation();
        participation2.setExamDate("2025-06-26");
        participation2.setArea("B2");
        participation2.setShift("Ca 1");
        participation2.setRoom("P.302");
        participations.add(participation2);

        complexStudent.setExamParticipations(participations);
        complexStudent.setCreatedAt(LocalDateTime.now());
        complexStudent.setUpdatedAt(LocalDateTime.now());

        when(studentRepository.findByStudentId("20110002")).thenReturn(Optional.of(complexStudent));

        // Test chuyển đổi dữ liệu
        Optional<StudentDetailResponse> result = studentService.getStudentDetail("20110002");

        assertTrue(result.isPresent());
        StudentDetailResponse response = result.get();

        // Kiểm tra chuyển đổi thông tin cơ bản
        assertEquals("complex1", response.getId());
        assertEquals("20110002", response.getStudentId());

        // Kiểm tra chuyển đổi thông tin chi tiết
        assertEquals("Trần Thị B", response.getCurrentInfo().getFullName());
        assertEquals("20DTHD2", response.getCurrentInfo().getStudentClass());
        assertEquals("Khoa học máy tính", response.getCurrentInfo().getMajor());

        // Kiểm tra chuyển đổi trạng thái
        assertEquals("suspended", response.getStatus().getExamEligibility());
        assertEquals("Vi phạm quy chế thi", response.getStatus().getReason());

        // Kiểm tra chuyển đổi danh sách thi
        assertEquals(2, response.getExamParticipations().size());
        assertEquals("B1", response.getExamParticipations().get(0).getArea());
        assertEquals("B2", response.getExamParticipations().get(1).getArea());
    }

    @Test
    void testExceptionHandling_InternalMethods() {
        // Kiểm tra xử lý exception trong các phương thức nội bộ

        // Test khi repository throw exception
        when(studentRepository.findByStudentId("ERROR_ID"))
                .thenThrow(new RuntimeException("Database error"));

        // Phương thức phải xử lý exception gracefully
        assertThrows(RuntimeException.class, () -> {
            studentService.getStudentDetail("ERROR_ID");
        });
    }

    @Test
    void testEdgeCases_BoundaryValues() {
        // Kiểm tra các trường hợp biên

        // Test với chuỗi rất dài
        String longQuery = "A".repeat(1000);
        when(studentRepository.findByStudentIdOrFullNameContaining(longQuery))
                .thenReturn(Collections.emptyList());

        List<StudentSearchResponse> result1 = studentService.searchStudents(longQuery);
        assertNotNull(result1);
        assertTrue(result1.isEmpty());

        // Test với ngày ở biên
        LocalDate minDate = LocalDate.MIN;
        LocalDate maxDate = LocalDate.MAX;

        List<String> result2 = studentService.getExamAreasByDate(minDate);
        assertNotNull(result2);

        List<String> result3 = studentService.getExamAreasByDate(maxDate);
        assertNotNull(result3);
    }
}
