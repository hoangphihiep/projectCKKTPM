package hcmute.edu.vn.baiTapCuoiKyKTPM.service;

import hcmute.edu.vn.baiTapCuoiKyKTPM.dto.response.*;
import hcmute.edu.vn.baiTapCuoiKyKTPM.model.ExamParticipation;
import hcmute.edu.vn.baiTapCuoiKyKTPM.model.Student;
import hcmute.edu.vn.baiTapCuoiKyKTPM.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class ViolationReportService {

    private final StudentRepository studentRepository;

    // Lấy tất cả vi phạm của một sinh viên
    public List<ViolationReportResponse> getStudentViolations(String studentId) {
        Optional<Student> studentOpt = studentRepository.findByStudentId(studentId);
        if (studentOpt.isEmpty()) {
            return new ArrayList<>();
        }

        Student student = studentOpt.get();
        List<ExamParticipation> examParticipations = student.getExamParticipations();
        if (examParticipations == null) {
            return new ArrayList<>();
        }

        return examParticipations.stream()
                .filter(exam -> exam.getViolation() != null && exam.getViolation().isHasViolation())
                .map(exam -> convertToViolationResponse(student, exam))
                .collect(Collectors.toList());
    }

    // Lấy tóm tắt vi phạm của một sinh viên
    public Optional<ViolationSummaryResponse> getStudentViolationSummary(String studentId) {
        Optional<Student> studentOpt = studentRepository.findByStudentId(studentId);
        if (studentOpt.isEmpty()) {
            return Optional.empty();
        }

        Student student = studentOpt.get();
        List<ExamParticipation> examParticipations = student.getExamParticipations();
        if (examParticipations == null) {
            examParticipations = new ArrayList<>();
        }

        List<ExamParticipation> violations = examParticipations.stream()
                .filter(exam -> exam.getViolation() != null && exam.getViolation().isHasViolation())
                .collect(Collectors.toList());

        ViolationSummaryResponse summary = new ViolationSummaryResponse();
        summary.setStudentId(student.getStudentId());
        summary.setStudentName(student.getCurrentInfo().getFullName());
        summary.setStudentClass(student.getCurrentInfo().getStudentClass());
        summary.setCurrentExamStatus(student.getStatus() != null ? student.getStatus().getExamEligibility() : "active");
        summary.setStatusReason(student.getStatus() != null ? student.getStatus().getReason() : null);
        summary.setTotalViolations(violations.size());

        // Đếm vi phạm theo mức độ
        Map<String, Long> violationCounts = violations.stream()
                .collect(Collectors.groupingBy(
                        exam -> exam.getViolation().getLevel().toLowerCase(),
                        Collectors.counting()
                ));

        summary.setKhienTrachCount(violationCounts.getOrDefault("khiển trách", 0L).intValue());
        summary.setCanhCaoCount(violationCounts.getOrDefault("cảnh cáo", 0L).intValue());
        summary.setDinhChiCount(violationCounts.getOrDefault("đình chỉ", 0L).intValue() +
                violationCounts.getOrDefault("đình chỉ thi", 0L).intValue());
        summary.setDuoiHocCount(violationCounts.getOrDefault("đuổi học", 0L).intValue());

        // Tìm vi phạm gần nhất
        violations.stream()
                .max(Comparator.comparing(ExamParticipation::getExamDate))
                .ifPresent(latest -> {
                    summary.setLatestViolationDate(latest.getExamDate().toString());
                    summary.setLatestViolationLevel(latest.getViolation().getLevel());
                });

        return Optional.of(summary);
    }

    // Lấy vi phạm theo phòng thi
    public ExamRoomViolationResponse getExamRoomViolations(LocalDate examDate, String area, String shift, String room) {
        String examDateStr = examDate.toString();
        List<Student> students = studentRepository.findByExamDateAndAreaAndShiftAndRoom(examDateStr, area, shift, room);

        List<ViolationReportResponse> violations = students.stream()
                .filter(Objects::nonNull)
                .flatMap(student -> {
                    List<ExamParticipation> examParticipations = student.getExamParticipations();
                    if (examParticipations == null) {
                        return Stream.empty();
                    }
                    return examParticipations.stream()
                            .filter(exam -> exam.getExamDate().equals(examDateStr) &&
                                    exam.getArea().equals(area) &&
                                    exam.getShift().equals(shift) &&
                                    exam.getRoom().equals(room))
                            .filter(exam -> exam.getViolation() != null && exam.getViolation().isHasViolation())
                            .map(exam -> convertToViolationResponse(student, exam));
                })
                .collect(Collectors.toList());

        ExamRoomViolationResponse response = new ExamRoomViolationResponse();
        response.setExamDate(examDate);
        response.setArea(area);
        response.setRoom(room);
        response.setShift(shift);
        response.setTotalStudents(students.size());
        response.setViolationCount(violations.size());
        response.setViolations(violations);

        return response;
    }

    // Lấy tất cả vi phạm trong khoảng thời gian
    public List<ViolationReportResponse> getViolationsByDateRange(LocalDate startDate, LocalDate endDate) {
        List<Student> allStudents = studentRepository.findAll();

        return allStudents.stream()
                .filter(Objects::nonNull) // Lọc null students
                .flatMap(student -> {
                    // Null check cho examParticipations
                    List<ExamParticipation> examParticipations = student.getExamParticipations();
                    if (examParticipations == null) {
                        return Stream.empty(); // Trả về empty stream thay vì null
                    }

                    return examParticipations.stream()
                            .filter(exam -> {
                                try {
                                    LocalDate examDate = LocalDate.parse(exam.getExamDate());
                                    return examDate.isAfter(startDate.minusDays(1)) &&
                                            examDate.isBefore(endDate.plusDays(1));
                                } catch (DateTimeParseException e) {
                                    return false;
                                }
                            })
                            .filter(exam -> exam.getViolation() != null && exam.getViolation().isHasViolation())
                            .map(exam -> convertToViolationResponse(student, exam));
                })
                .sorted(Comparator.comparing(response -> {
                    try {
                        return response.getExamDate();
                    } catch (DateTimeParseException e) {
                        return LocalDate.MIN;
                    }
                }, Comparator.reverseOrder()))
                .collect(Collectors.toList());
    }

    // Lấy thống kê vi phạm
    public ViolationStatisticsResponse getViolationStatistics() {
        List<Student> allStudents = studentRepository.findAll();

        List<ViolationReportResponse> allViolations = allStudents.stream()
                .filter(Objects::nonNull) // Lọc null students
                .flatMap(student -> {
                    // Null check cho examParticipations
                    List<ExamParticipation> examParticipations = student.getExamParticipations();
                    if (examParticipations == null) {
                        return Stream.empty(); // Trả về empty stream thay vì null
                    }

                    return examParticipations.stream()
                            .filter(exam -> exam != null &&
                                    exam.getViolation() != null &&
                                    exam.getViolation().isHasViolation())
                            .map(exam -> convertToViolationResponse(student, exam));
                })
                .collect(Collectors.toList());

        ViolationStatisticsResponse stats = new ViolationStatisticsResponse();
        stats.setTotalViolations(allViolations.size());

        // Thống kê theo mức độ vi phạm
        Map<String, Integer> violationsByLevel = allViolations.stream()
                .filter(violation -> violation.getViolationLevel() != null) // Null check
                .collect(Collectors.groupingBy(
                        ViolationReportResponse::getViolationLevel,
                        Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
                ));
        stats.setViolationsByLevel(violationsByLevel);

        // Thống kê theo khu vực
        Map<String, Integer> violationsByArea = allViolations.stream()
                .filter(violation -> violation.getArea() != null) // Null check
                .collect(Collectors.groupingBy(
                        ViolationReportResponse::getArea,
                        Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
                ));
        stats.setViolationsByArea(violationsByArea);

        // Thống kê theo phòng thi
        Map<String, Integer> violationsByRoom = allViolations.stream()
                .filter(violation -> violation.getRoom() != null) // Null check
                .collect(Collectors.groupingBy(
                        ViolationReportResponse::getRoom,
                        Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
                ));
        stats.setViolationsByRoom(violationsByRoom);

        // Thống kê trạng thái sinh viên với null check tốt hơn
        Map<String, Integer> studentsByStatus = allStudents.stream()
                .filter(Objects::nonNull) // Lọc null students
                .collect(Collectors.groupingBy(
                        student -> {
                            if (student.getStatus() != null && student.getStatus().getExamEligibility() != null) {
                                return student.getStatus().getExamEligibility();
                            }
                            return "active"; // Default value
                        },
                        Collectors.collectingAndThen(Collectors.counting(), Math::toIntExact)
                ));
        stats.setStudentsByStatus(studentsByStatus);

        stats.setActiveStudents(studentsByStatus.getOrDefault("active", 0));
        stats.setSuspendedStudents(studentsByStatus.getOrDefault("suspended", 0));
        stats.setExpelledStudents(studentsByStatus.getOrDefault("expelled", 0));

        return stats;
    }

    // Lấy danh sách sinh viên bị cấm thi
    public List<ViolationSummaryResponse> getSuspendedAndExpelledStudents() {
        List<Student> students = studentRepository.findAll().stream()
                .filter(student -> student.getStatus() != null &&
                        ("suspended".equals(student.getStatus().getExamEligibility()) ||
                                "expelled".equals(student.getStatus().getExamEligibility())))
                .collect(Collectors.toList());

        return students.stream()
                .map(student -> getStudentViolationSummary(student.getStudentId()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    // Convert ExamParticipation to ViolationReportResponse
    private ViolationReportResponse convertToViolationResponse(Student student, ExamParticipation exam) {
        ViolationReportResponse response = new ViolationReportResponse();
        response.setStudentId(student.getStudentId());
        response.setStudentName(student.getCurrentInfo().getFullName());
        response.setStudentClass(student.getCurrentInfo().getStudentClass());
        response.setExamDate(LocalDate.parse(exam.getExamDate()));
        response.setArea(exam.getArea());
        response.setRoom(exam.getRoom());
        response.setShift(exam.getShift());

        if (exam.getViolation() != null) {
            response.setHasViolation(exam.getViolation().isHasViolation());
            response.setViolationLevel(exam.getViolation().getLevel());
            response.setDescription(exam.getViolation().getDescription());
            response.setInvigilators(exam.getViolation().getInvigilators());
            response.setNotes(exam.getViolation().getNotes());
        }

        if (student.getStatus() != null) {
            response.setCurrentExamStatus(student.getStatus().getExamEligibility());
            response.setStatusReason(student.getStatus().getReason());
        } else {
            response.setCurrentExamStatus("active");
        }

        return response;
    }
}