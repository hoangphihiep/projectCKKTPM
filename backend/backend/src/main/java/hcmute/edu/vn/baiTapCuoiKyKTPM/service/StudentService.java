package hcmute.edu.vn.baiTapCuoiKyKTPM.service;

import hcmute.edu.vn.baiTapCuoiKyKTPM.dto.response.*;
import hcmute.edu.vn.baiTapCuoiKyKTPM.model.ExamParticipation;
import hcmute.edu.vn.baiTapCuoiKyKTPM.model.Student;
import hcmute.edu.vn.baiTapCuoiKyKTPM.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    // Tìm kiếm sinh viên theo mã sinh viên hoặc họ tên
    public List<StudentSearchResponse> searchStudents(String searchTerm) {
        List<Student> students = studentRepository.findByStudentIdOrFullNameContaining(searchTerm);
        for (Student s : students){
            System.out.println ("Giá trị: " + s.getStudentId() + " " + s.getCurrentInfo().getFullName());
        }
        System.out.println ("Search Item: " + searchTerm);
        System.out.println ("Có vào đây: " + students.size());
        return students.stream()
                .map(this::convertToSearchResponse)
                .collect(Collectors.toList());
    }

    // Lấy danh sách khu vực thi theo ngày
    public List<String> getExamAreasByDate(LocalDate examDate) {
        String examDateStr = examDate.toString(); // "2025-06-25"

        List<Student> students = studentRepository.findByExamDate(examDateStr);

        return students.stream()
                .flatMap(student -> student.getExamParticipations().stream())
                .filter(exam -> exam.getExamDate().contains(examDateStr))
                .map(ExamParticipation::getArea)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // Lấy danh sách ca thi theo ngày và khu vực
    public List<String> getExamShiftsByDateAndArea(LocalDate examDate, String area) {
        String examDateStr = examDate.toString();
        System.out.println ("Thời gian: " + examDateStr + " Khu vực: " + area);
        List<Student> students = studentRepository.findByExamDateAndArea(examDateStr, area);
        for (Student s : students){
            System.out.println ("Học sinh: " + s.getStudentId());
        }
        return students.stream()
                .flatMap(student -> student.getExamParticipations().stream())
                .filter(exam -> exam.getExamDate().contains(examDateStr) && exam.getArea().contains(area))
                .map(ExamParticipation::getShift)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // Lấy danh sách phòng thi theo ngày, khu vực và ca thi
    public List<String> getExamRoomsByDateAreaAndShift(LocalDate examDate, String area, String shift) {
        String examDateStr = examDate.toString();
        List<Student> students = studentRepository.findByExamDateAndAreaAndShift(examDateStr, area, shift);
        return students.stream()
                .flatMap(student -> student.getExamParticipations().stream())
                .filter(exam -> exam.getExamDate().contains(examDateStr)
                        && exam.getArea().equals(area)
                        && exam.getShift().equals(shift))
                .map(ExamParticipation::getRoom)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    // Lấy danh sách sinh viên trong phòng thi
    public List<StudentSearchResponse> getStudentsByExamRoom(LocalDate examDate, String area, String shift, String room) {
        String examDateStr = examDate.toString();
        List<Student> students = studentRepository.findByExamDateAndAreaAndShiftAndRoom(examDateStr, area, shift, room);
        return students.stream()
                .map(this::convertToSearchResponse)
                .collect(Collectors.toList());
    }

    // Lấy thông tin chi tiết sinh viên
    public Optional<StudentDetailResponse> getStudentDetail(String studentId) {
        Optional<Student> studentOpt = studentRepository.findByStudentId(studentId);
        return studentOpt.map(this::convertToDetailResponse);
    }

    // Lấy thông tin chi tiết sinh viên theo ID
    public Optional<StudentDetailResponse> getStudentDetailById(String id) {
        Optional<Student> studentOpt = studentRepository.findById(id);
        return studentOpt.map(this::convertToDetailResponse);
    }

    // Convert Student to StudentSearchResponse
    private StudentSearchResponse convertToSearchResponse(Student student) {
        return new StudentSearchResponse(
                student.getId(),
                student.getStudentId(),
                student.getCurrentInfo().getFullName(),
                student.getCurrentInfo().getStudentClass(),
                student.getCurrentInfo().getMajor(),
                student.getCurrentInfo().getFaculty(),
                student.getStatus() != null ? student.getStatus().getExamEligibility() : "active",
                student.getCurrentInfo().getPhotoUrl() // Tạo URL ảnh
        );
    }

    // Convert Student to StudentDetailResponse
    private StudentDetailResponse convertToDetailResponse(Student student) {
        return new StudentDetailResponse(
                student.getId(),
                student.getStudentId(),
                student.getCurrentInfo(),
                student.getInfoHistory(),
                student.getExamParticipations(),
                student.getStatus(),
                student.getCurrentInfo().getPhotoUrl(),
                student.getCreatedAt(),
                student.getUpdatedAt()
        );
    }
}
