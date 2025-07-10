"use client";
import StudentImage from "../../../components/studentImage";
const mockStudents = [
  {
    mssv: "23110114",
    name: "Nguyễn Quốc Vĩ",
    image: "/23110114.png", // Đặt ảnh này vào thư mục /public
  },
  {
    mssv: "23110115",
    name: "Trần Văn Vĩ",
    image: "/23110115.png", // Đặt ảnh này vào thư mục /public
  },
  {
    mssv: "23110116",
    name: "Võ Văn Vĩ",
    image: "/23110116.png", // Đặt ảnh này vào thư mục /public
  },
  // Thêm sinh viên khác nếu cần
];
const roomStudents = {
  "A2-101": [
    {
      name: "Lê Việt Hoàng",
      mssv: "17119077",
      image: "/student1.png",
      lop: "171190B",
      ngaysinh: "30/04/1999",
      quequan: "Hà Tĩnh",
      gioitinh: "Nam",
      cmnd: "184378266",
      nganh: "Công nghệ kỹ thuật máy tính",
      khoa: "Điện - Điện tử",
    },
    {
      name: "Phùng Huy Hoàng",
      mssv: "20110370",
      image: "/student2.png",
    },
  ],
  "A2-102": [
    {
      name: "Lê Thị Mỹ Hồng",
      mssv: "19155017",
      image: "/student3.png",
    },
    {
      name: "Trần Khánh Hời",
      mssv: "19127021",
      image: "/student4.png",
    },
  ],
  "A3-201": [],
  "A3-202": [],
  "A4-101": [],
  "A4-201": [],
};

import Image from "next/image";
import { useState } from "react";
import styles from "./mainpage.module.css"; // Tạo file CSS này

export default function MainPage() {
  const [searchType, setSearchType] = useState("Họ tên");
  const [searchInput, setSearchInput] = useState("");
  const [searchResult, setSearchResult] = useState(null);
  const [activeTab, setActiveTab] = useState("sinhvien"); // "sinhvien" | "phongthi"
  const [selectedZone, setSelectedZone] = useState(null); // VD: "A2", "A3"...
  const [selectedFloor, setSelectedFloor] = useState(null);
  const [selectedRoom, setSelectedRoom] = useState(null);
  const [selectedStudent, setSelectedStudent] = useState(null);
  const [hasSearched, setHasSearched] = useState(false);
  const [selectedDate, setSelectedDate] = useState("");
  const [examAreas, setExamAreas] = useState([]);
  const [examShifts, setExamShifts] = useState([]);
  const [selectedShift, setSelectedShift] = useState("");
  const [examRooms, setExamRooms] = useState([]);
  const [roomStudentList, setRoomStudentList] = useState([]);
  const [violationStudentId, setViolationStudentId] = useState("");
  const [violations, setViolations] = useState([]);
  const [violationLoading, setViolationLoading] = useState(false);
  const [violationError, setViolationError] = useState(null);
  const [violationSummary, setViolationSummary] = useState(null);
  const [roomViolationData, setRoomViolationData] = useState(null);

  //search all student by id or name
  const handleSearch = async () => {
    if (!searchInput.trim()) return;

    try {
      const res = await fetch(
        `http://localhost:8080/api/students/search?query=${encodeURIComponent(
          searchInput.trim()
        )}`
      );

      if (!res.ok) throw new Error("Không thể tìm kiếm sinh viên");

      const data = await res.json();
      setSearchResult(data);
      setHasSearched(true);
    } catch (err) {
      console.error("Lỗi khi tìm kiếm:", err.message);
      setSearchResult([]);
      setHasSearched(true);
    }
  };
  // Lay danh sach khu vuc thi theo ngay
  const fetchExamAreas = async (date) => {
    setSelectedZone(null); // reset lựa chọn cũ
    setSelectedFloor(null);
    setSelectedRoom(null);

    if (!date) return;
    try {
      const res = await fetch(
        `http://localhost:8080/api/students/exam-areas?examDate=${date}`
      );
      if (!res.ok) throw new Error("Lỗi khi lấy khu vực thi");
      const data = await res.json();
      setExamAreas(data);
    } catch (err) {
      console.error("Lỗi API khu vực thi:", err);
      setExamAreas([]);
    }
  };
  // API lấy danh sách ca thi theo ngày và khu
  const fetchExamShifts = async (date, zone) => {
    if (!date || !zone) return;
    try {
      const res = await fetch(
        `http://localhost:8080/api/students/exam-shifts?examDate=${date}&area=${zone}`
      );
      if (!res.ok) throw new Error("Lỗi khi lấy ca thi");
      const data = await res.json();
      setExamShifts(data);
      setSelectedShift(""); // reset lại nếu chọn khu khác
    } catch (err) {
      console.error("Lỗi API ca thi:", err);
      setExamShifts([]);
    }
  };
  // API lấy danh sách phòng thi theo ngày, khu vực và ca thi
  const fetchExamRooms = async (date, zone, shift) => {
    if (!date || !zone || !shift) return;

    try {
      const res = await fetch(
        `http://localhost:8080/api/students/exam-rooms?examDate=${date}&area=${zone}&shift=${shift}`
      );
      if (!res.ok) throw new Error("Lỗi khi lấy danh sách phòng");

      const data = await res.json(); // danh sách chuỗi phòng, ví dụ: ["P.201", "P.202"]
      setExamRooms(data);
    } catch (err) {
      console.error("Lỗi API phòng thi:", err);
      setExamRooms([]);
    }
  };
  // API lấy danh sách sinh viên trong phòng thi
  const fetchStudentsInRoom = async (date, zone, shift, room) => {
    if (!date || !zone || !shift || !room) return;
    try {
      const res = await fetch(
        `http://localhost:8080/api/students/exam-room-students?examDate=${date}&area=${zone}&shift=${encodeURIComponent(
          shift
        )}&room=${encodeURIComponent(room)}`
      );
      if (!res.ok) throw new Error("Lỗi khi lấy danh sách sinh viên");

      const data = await res.json();
      setRoomStudentList(data);
    } catch (err) {
      console.error("Lỗi khi lấy dữ liệu sinh viên trong phòng:", err);
      setRoomStudentList([]);
    }
  };
  // API lấy thông tin chi tiết sinh viên theo mã sinh viên
  const fetchStudentDetail = async (studentId) => {
    try {
      const res = await fetch(
        `http://localhost:8080/api/students/detail/${studentId}`
      );
      if (!res.ok) throw new Error("Không tìm thấy sinh viên");

      const data = await res.json();
      setSelectedStudent(data);
    } catch (err) {
      console.error("Lỗi khi lấy chi tiết sinh viên:", err);
      setSelectedStudent(null);
    }
  };

  // API lấy tất cả vi phạm của một sinh viên
  const fetchViolations = async (studentId) => {
    if (!studentId.trim()) return;
    setViolationLoading(true);
    setViolationError(null);
    setViolations([]);
    setViolationSummary(null); // reset

    try {
      const [violationsRes, summaryRes] = await Promise.all([
        fetch(`http://localhost:8080/api/violations/student/${studentId}`),
        fetch(
          `http://localhost:8080/api/violations/student/${studentId}/summary`
        ),
      ]);

      if (!violationsRes.ok) throw new Error("Lỗi khi lấy danh sách vi phạm");

      const violationsData = await violationsRes.json();
      setViolations(violationsData);

      if (summaryRes.ok) {
        const summaryData = await summaryRes.json();
        setViolationSummary(summaryData);
      }
    } catch (err) {
      setViolationError("Không thể lấy dữ liệu");
      setViolations([]);
      setViolationSummary(null);
    } finally {
      setViolationLoading(false);
    }
  };

  // API lấy tóm tắt vi phạm của một sinh viên
  const fetchViolationSummary = async (studentId) => {
    try {
      const res = await fetch(
        `http://localhost:8080/api/violations/student/${studentId}/summary`
      );
      if (!res.ok) throw new Error("Không tìm thấy tóm tắt vi phạm");

      const data = await res.json();
      setViolationSummary(data);
    } catch (err) {
      console.error("Lỗi lấy tóm tắt vi phạm:", err);
      setViolationSummary(null);
    }
  };
  // API lấy vi phạm theo phòng thi
  const fetchRoomViolations = async (date, area, shift, room) => {
    if (!date || !area || !shift || !room) return;

    try {
      const res = await fetch(
        `http://localhost:8080/api/violations/exam-room?examDate=${date}&area=${area}&shift=${encodeURIComponent(
          shift
        )}&room=${encodeURIComponent(room)}`
      );
      if (!res.ok) throw new Error("Không lấy được dữ liệu vi phạm phòng thi");

      const data = await res.json();
      setRoomViolationData(data);
    } catch (err) {
      console.error("Lỗi khi lấy dữ liệu vi phạm phòng:", err);
      setRoomViolationData(null);
    }
  };

  return (
    <div className={styles.container}>
      {/* Header hiển thị tên trường */}
      <header className={styles.header}>
        <div className={styles.headerLeft}>
          <Image src="/logohcmute.png" alt="Logo" width={60} height={70} />
        </div>
        <div className={styles.headerRight}>
          <h2>TRƯỜNG ĐẠI HỌC SƯ PHẠM KỸ THUẬT TP. HỒ CHÍ MINH</h2>
          <p>PHÒNG THANH TRA - PHÁP CHẾ</p>
        </div>
      </header>
      {/* Header hiển thị tra cuu */}
      <nav className={styles.nav}>
        <button
          className={styles.navBtn}
          onClick={() => setActiveTab("sinhvien")}
        >
          Tra cứu sinh viên
        </button>
        <button
          className={styles.navBtn}
          onClick={() => setActiveTab("phongthi")}
        >
          Tra cứu phòng thi
        </button>
        <button
          className={styles.navBtn}
          onClick={() => setActiveTab("vipham")}
        >
          Danh sách vi phạm
        </button>
      </nav>

      {activeTab === "sinhvien" && (
        <div className={styles.searchArea}>
          <label className={styles.find}>Tìm theo:</label>
          <select
            value={searchType}
            onChange={(e) => setSearchType(e.target.value)}
            className={styles.searchArea}
          >
            <option>Họ tên</option>
            <option>Mã số sinh viên</option>
          </select>
          <input
            type="text"
            placeholder="Nhập từ khóa tìm kiếm"
            value={searchInput}
            onChange={(e) => setSearchInput(e.target.value)}
            onKeyDown={(e) => e.key === "Enter" && handleSearch()}
            className={styles.inputArea}
          />
          <button onClick={handleSearch}>Tìm kiếm</button>
        </div>
      )}

      {/* Header hiển thị ket qua tra cuu */}

      {activeTab === "sinhvien" && (
        <div className={styles.result}>
          <h3>Kết quả tìm kiếm</h3>
          {searchResult && searchResult.length > 0 ? (
            <div className={styles.resultGrid}>
              {searchResult.map((student) => (
                <div
                  key={student.studentId}
                  className={styles.studentCard}
                  onClick={() => fetchStudentDetail(student.studentId)}
                >
                  <StudentImage
                    src={student.photoUrl || "/default.png"}
                    alt={student.fullName}
                  />
                  <p>
                    <strong>{student.fullName}</strong>
                  </p>
                  <p>{student.studentId}</p>
                </div>
              ))}
            </div>
          ) : hasSearched ? (
            <div className={styles.noResult}>
              Không tìm thấy kết quả phù hợp
            </div>
          ) : null}
        </div>
      )}

      {activeTab === "phongthi" && !selectedZone && (
        <div className={styles.examInfo}>
          {/* Chọn ngày thi */}
          <div className={styles.examDate}>
            <label htmlFor="examDate">Chọn ngày thi:</label>
            <select
              id="examDate"
              value={selectedDate}
              onChange={(e) => {
                setSelectedDate(e.target.value);
                fetchExamAreas(e.target.value);
              }}
            >
              <option value="">-- Chọn ngày --</option>
              <option value="2025-06-25">2025-06-25</option>
              <option value="2025-06-26">2025-06-26</option>
              <option value="2025-07-01">2025-07-01</option>
            </select>
          </div>

          {/* Cảnh báo thí sinh thi nhiều ca */}
          <div className={styles.warningArea}>
            <h4>Danh sách thí sinh thi nhiều hơn 2 ca thi trong một ngày</h4>
            <div className={styles.noDanger}>
              Không tìm thấy thí sinh nguy hiểm
            </div>
          </div>

          {/* Danh sách khu vực thi */}
          <div className={styles.examZones}>
            <h4>Khu vực thi</h4>
            <div className={styles.zoneGrid}>
              {examAreas.map((zone) => (
                <div
                  key={zone}
                  className={styles.zoneCard}
                  onClick={() => {
                    setSelectedZone(zone);
                    fetchExamShifts(selectedDate, zone);
                  }}
                >
                  Khu {zone}
                </div>
              ))}
            </div>
          </div>
        </div>
      )}

      {activeTab === "phongthi" && selectedZone && !selectedRoom && (
        <div className={styles.zoneDetail}>
          {/* Nút quay lại khu */}
          <button
            className={styles.backBtn}
            onClick={() => {
              setSelectedZone(null);
              setSelectedFloor(null);
              setSelectedRoom(null);
            }}
          >
            ⬅ Quay lại danh sách khu
          </button>
          <h3>Khu {selectedZone}</h3>

          {examShifts.length > 0 && (
            <select
              className={styles.sessionSelect}
              value={selectedShift}
              onChange={(e) => {
                const newShift = e.target.value;
                setSelectedShift(newShift);
                fetchExamRooms(selectedDate, selectedZone, newShift); // gọi API mới
              }}
            >
              <option value="">-- Chọn ca thi --</option>
              {examShifts.map((shift, idx) => (
                <option key={idx} value={shift}>
                  {shift}
                </option>
              ))}
            </select>
          )}

          {/* {!selectedFloor && (
            <div className={styles.floorSelect}>
              {["Tầng 1", "Tầng 2", "Tầng 3"].map((floor) => (
                <button
                  key={floor}
                  className={styles.floorBtn}
                  onClick={() => setSelectedFloor(floor)}
                >
                  🧭 {floor}
                </button>
              ))}
            </div>
          )} */}

          {selectedShift ? (
            examRooms.length > 0 ? (
              <div className={styles.roomGrid}>
                {examRooms.map((room) => (
                  <button
                    key={room}
                    className={styles.roomCard}
                    onClick={() => {
                      setSelectedRoom(room);
                      fetchStudentsInRoom(
                        selectedDate,
                        selectedZone,
                        selectedShift,
                        room
                      );
                      fetchRoomViolations(
                        selectedDate,
                        selectedZone,
                        selectedShift,
                        room
                      );
                    }}
                  >
                    {room}
                  </button>
                ))}
              </div>
            ) : (
              <p className={styles.alertText}>
                Không có phòng thi nào trong ca này.
              </p>
            )
          ) : (
            <p className={styles.alertText}>⚠️ Vui lòng chọn ca thi trước.</p>
          )}

          {!selectedRoom && (
            <p className={styles.alertText}>
              Vui lòng chọn phòng để thấy danh sách sinh viên
            </p>
          )}
        </div>
      )}

      {activeTab === "phongthi" && selectedRoom && (
        <div className={styles.roomStudentList}>
          {/* Nút quay lại phòng */}
          <button
            className={styles.backBtn}
            onClick={() => setSelectedRoom(null)}
          >
            ⬅ Quay lại chọn phòng
          </button>
          <h3>
            Môn thi: Anh văn đầu ra, Ngày thi: {selectedDate}, Phòng:{" "}
            {selectedRoom}
          </h3>
          {roomViolationData && (
            <div className={styles.violationInRoom}>
              <h4>📋 Thống kê vi phạm trong phòng</h4>
              <p>
                <strong>Tổng số sinh viên:</strong>{" "}
                {roomViolationData.totalStudents}
              </p>
              <p>
                <strong>Số sinh viên vi phạm:</strong>{" "}
                {roomViolationData.violations.length}
              </p>

              {roomViolationData.violations.length > 0 && (
                <table className={styles.violationTable}>
                  <thead>
                    <tr>
                      <th>MSSV</th>
                      <th>Họ tên</th>
                      <th>Lớp</th>
                      <th>Mức độ</th>
                      <th>Mô tả</th>
                      <th>Trạng thái</th>
                    </tr>
                  </thead>
                  <tbody>
                    {roomViolationData.violations.map((v, idx) => (
                      <tr key={idx}>
                        <td>{v.studentId}</td>
                        <td>{v.studentName}</td>
                        <td>{v.studentClass}</td>
                        <td>{v.violationLevel}</td>
                        <td>{v.description}</td>
                        <td>{v.currentExamStatus}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </div>
          )}

          <div className={styles.resultGrid}>
            {roomStudentList.map((student) => (
              <div
                key={student.mssv}
                className={styles.studentCard}
                onClick={() => fetchStudentDetail(student.studentId)}
              >
                <Image
                  src={student.photoUrl || "/default.png"}
                  alt={student.fullName}
                  width={180}
                  height={220}
                />
                <p>
                  <strong>{student.fullName}</strong>
                </p>
                <p>{student.studentId}</p>
              </div>
            ))}
          </div>
        </div>
      )}

      {selectedStudent && (
        <div className={styles.popupOverlay}>
          <div className={styles.popupCard}>
            <h3>Thông tin sinh viên</h3>
            <Image
              src={selectedStudent.photoUrl || "/default.png"}
              alt="avatar"
              width={120}
              height={140}
            />
            <div className={styles.studentDetail}>
              <p>
                <strong>Họ tên:</strong> {selectedStudent.currentInfo.fullName}
              </p>
              <p>
                <strong>MSSV:</strong> {selectedStudent.studentId}
              </p>
              <p>
                <strong>Lớp:</strong> {selectedStudent.currentInfo.studentClass}
              </p>
              <p>
                <strong>Ngày sinh:</strong> {selectedStudent.currentInfo.dob}
              </p>
              <p>
                <strong>Giới tính:</strong> {selectedStudent.currentInfo.gender}
              </p>
              <p>
                <strong>Ngành:</strong> {selectedStudent.currentInfo.major}
              </p>
              <p>
                <strong>Khoa:</strong> {selectedStudent.currentInfo.faculty}
              </p>
              <p>
                <strong>Trạng thái:</strong>{" "}
                {selectedStudent.status.examEligibility}
              </p>
              {selectedStudent.status.reason && (
                <p>
                  <strong>Lý do:</strong> {selectedStudent.status.reason}
                </p>
              )}
            </div>
            <button
              className={styles.closeBtn}
              onClick={() => setSelectedStudent(null)}
            >
              Đóng
            </button>
          </div>
        </div>
      )}

      {activeTab === "vipham" && (
        <div className={styles.violationSection}>
          <h3>Tra cứu vi phạm của sinh viên</h3>
          <div className={styles.searchArea}>
            <input
              type="text"
              placeholder="Nhập MSSV (studentId)..."
              value={violationStudentId}
              onChange={(e) => setViolationStudentId(e.target.value)}
              className={styles.inputArea}
            />
            <button onClick={() => fetchViolations(violationStudentId)}>
              Tìm kiếm
            </button>
          </div>

          {violationLoading && <p>Đang tải dữ liệu...</p>}
          {violationError && <p style={{ color: "red" }}>{violationError}</p>}

          {violations.length > 0 ? (
            <div className={styles.violationTable}>
              <table>
                <thead>
                  <tr>
                    <th>MSSV</th>
                    <th>Họ tên</th>
                    <th>Lớp</th>
                    <th>Ngày thi</th>
                    <th>Khu vực</th>
                    <th>Phòng</th>
                    <th>Ca</th>
                    <th>Mức độ</th>
                    <th>Ghi chú</th>
                    <th>Trạng thái</th>
                  </tr>
                </thead>
                <tbody>
                  {violations.map((v, idx) => (
                    <tr key={idx}>
                      <td>{v.studentId}</td>
                      <td>{v.studentName}</td>
                      <td>{v.studentClass}</td>
                      <td>{v.examDate}</td>
                      <td>{v.area}</td>
                      <td>{v.room}</td>
                      <td>{v.shift}</td>
                      <td>{v.violationLevel}</td>
                      <td>{v.description}</td>
                      <td>{v.currentExamStatus}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          ) : (
            !violationLoading && <p>Không có vi phạm nào được tìm thấy</p>
          )}
        </div>
      )}

      {/* Footer */}
      <footer className={styles.footer}>
        <div className={styles.footerContainer}>
          {/* Cột 1: Logo + Liên hệ */}
          <div className={styles.column}>
            <Image src="/logohcmute.png" alt="Logo" width={60} height={70} />
            <p>
              <strong>Trường Đại Học Sư Phạm Kỹ Thuật TP. HCM</strong>
            </p>
            <p>
              <strong>Phòng Thanh Tra - Pháp Chế </strong>
            </p>
            <p>📍 01 Võ Văn Ngân, Q. Thủ Đức, TP. HCM</p>
            <p>📞 (08) 37221223 (nhánh 48180)</p>
            <p>✉️ pttpc@hcmute.edu.vn</p>
          </div>

          {/* Cột 2: Kết nối */}
          <div className={styles.column}>
            <p>
              <strong>Kết nối với HCMUTE</strong>
            </p>
            <p>➡️ ĐH Sư Phạm Kỹ Thuật TP. Hồ Chí Minh</p>
            <p>➡️ Phòng Thanh Tra Pháp Chế</p>
            <div className={styles.socialIcons}>
              <a
                href="https://facebook.com"
                target="_blank"
                rel="noopener noreferrer"
              >
                <Image
                  // src="/facebook.png"
                  alt="Facebook"
                  width={24}
                  height={24}
                />
              </a>
              <a
                href="https://youtube.com"
                target="_blank"
                rel="noopener noreferrer"
              >
                <Image
                  // src="/youtube.png"
                  alt="YouTube"
                  width={24}
                  height={24}
                />
              </a>
            </div>
          </div>
        </div>

        {/* Thanh cuối */}
        <div className={styles.footerBottom}>
          <p className={styles.left}>
            Copyright © 2017 HCMUTE. All rights reserved.
          </p>
          <p className={styles.right}>
            HOTLINE - PHÒNG THANH TRA PHÁP CHẾ: (+84.28) 3722 1223 (nhánh 48180)
          </p>
        </div>
      </footer>
    </div>
  );
}
