package hcmute.edu.vn.baiTapCuoiKyKTPM.repository;

import hcmute.edu.vn.baiTapCuoiKyKTPM.model.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends MongoRepository<Student, String> {

    // Tìm kiếm theo mã sinh viên (sử dụng student_id thay vì studentId)
    @Query("{'student_id': ?0}")
    Optional<Student> findByStudentId(String studentId);

    // Tìm kiếm theo họ tên (sử dụng current_info.full_name)
    @Query("{'current_info.full_name': {$regex: ?0, $options: 'i'}}")
    List<Student> findByFullNameContainingIgnoreCase(String fullName);

    // Tìm kiếm theo mã sinh viên hoặc họ tên
    @Query("{ $or: [ " +
            "{'student_id': {$regex: ?0, $options: 'i'}}, " +
            "{'current_info.full_name': {$regex: ?0, $options: 'i'}} " +
            "]}")
    List<Student> findByStudentIdOrFullNameContaining(String searchTerm);

    // Tìm sinh viên theo ngày thi (sử dụng exam_participations.exam_date)
    @Query("{'exam_participations.exam_date': {'$regex': ?0}}")
    List<Student> findByExamDate(String examDate);

    // Tìm sinh viên theo ngày thi và khu vực
    @Query("{'exam_participations': {$elemMatch: {'exam_date': ?0, 'area': ?1}}}")
    List<Student> findByExamDateAndArea(String examDate, String area);

    // Tìm sinh viên theo ngày thi, khu vực và ca thi
    @Query("{'exam_participations': {$elemMatch: {'exam_date': ?0, 'area': ?1, 'shift': ?2}}}")
    List<Student> findByExamDateAndAreaAndShift(String examDate, String area, String shift);

    // Tìm sinh viên theo ngày thi, khu vực, ca thi và phòng thi
    @Query("{'exam_participations': {$elemMatch: {'exam_date': ?0, 'area': ?1, 'shift': ?2, 'room': ?3}}}")
    List<Student> findByExamDateAndAreaAndShiftAndRoom(String examDate, String area, String shift, String room);

    // Tìm sinh viên có vi phạm
    @Query("{'exam_participations.violation.has_violation': true}")
    List<Student> findStudentsWithViolations();

    // Tìm sinh viên theo trạng thái thi cử
    @Query("{'status.exam_eligibility': ?0}")
    List<Student> findByExamEligibility(String examEligibility);

    // Tìm sinh viên bị cấm thi (suspended hoặc expelled)
    @Query("{'status.exam_eligibility': {$in: ['suspended', 'expelled']}}")
    List<Student> findSuspendedAndExpelledStudents();
}
