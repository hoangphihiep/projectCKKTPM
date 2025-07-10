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

  const handleSearch = () => {
    let result = [];

    if (searchType === "Mã số sinh viên") {
      result = mockStudents.filter(
        (student) => student.mssv === searchInput.trim()
      );
    } else if (searchType === "Họ tên") {
      result = mockStudents.filter((student) =>
        student.name.toLowerCase().includes(searchInput.trim().toLowerCase())
      );
    }
    setSearchResult(result.length > 0 ? result : null);

    console.log("Kết quả tìm:", result);
    console.log("Tìm theo:", searchType);
    console.log("Từ khóa:", searchInput.trim());

    setSearchResult(result || null);
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

        {activeTab === "sinhvien" && (
          <input
            type="text"
            className={styles.searchInput}
            placeholder="Tìm sinh viên..."
            value={searchInput}
            onChange={(e) => setSearchInput(e.target.value)}
          />
        )}
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
            className={styles.inputArea}
          />
          <button onClick={handleSearch}>Tìm kiếm</button>
        </div>
      )}

      {/* Header hiển thị ket qua tra cuu */}

      {activeTab === "sinhvien" && (
        <div className={styles.result}>
          <h3>Kết quả tìm kiếm</h3>
          {searchResult ? (
            <div className={styles.resultGrid}>
              {searchResult.map((student) => (
                <div
                  key={student.mssv}
                  className={styles.studentCard}
                  onClick={() => setSelectedStudent(student)}
                >
                  <StudentImage src={student.image} alt={student.name} />

                  <p>
                    <strong>{student.name}</strong>
                  </p>
                  <p>{student.mssv}</p>
                </div>
              ))}
            </div>
          ) : (
            <div className={styles.noResult}>
              Không tìm thấy kết quả phù hợp
            </div>
          )}
        </div>
      )}
      {activeTab === "phongthi" && !selectedZone && (
        <div className={styles.examInfo}>
          {/* Chọn ngày thi */}
          <div className={styles.examDate}>
            <label htmlFor="examDate">Chọn ngày thi:</label>
            <input type="date" id="examDate" defaultValue="2025-05-18" />
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
              {["A2", "A3", "A4", "A5"].map((zone) => (
                <div
                  key={zone}
                  className={styles.zoneCard}
                  onClick={() => setSelectedZone(zone)} // 👈 chọn khu
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

          <select className={styles.sessionSelect}>
            <option>07g00</option>
            <option>09g00</option>
            <option>13g00</option>
          </select>

          {!selectedFloor && (
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
          )}

          {selectedFloor && (
            <div className={styles.roomGrid}>
              {Object.keys(roomStudents)
                .filter((room) => {
                  const [zone, code] = room.split("-");
                  const floorNumber = code.charAt(0); // lấy số tầng từ ví dụ: "201" → "2"
                  return (
                    zone === selectedZone &&
                    `Tầng ${floorNumber}` === selectedFloor
                  );
                })
                .map((room) => (
                  <button
                    key={room}
                    className={styles.roomCard}
                    onClick={() => setSelectedRoom(room)}
                  >
                    {room}
                  </button>
                ))}
            </div>
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
            Môn thi: Anh văn đầu ra, Ngày thi: 18/05/2025, Phòng: {selectedRoom}
          </h3>
          <div className={styles.resultGrid}>
            {(roomStudents[selectedRoom] || []).map((student) => (
              <div
                key={student.mssv}
                className={styles.studentCard}
                onClick={() => setSelectedStudent(student)}
              >
                <Image
                  src={student.image}
                  alt={student.name}
                  width={180}
                  height={220}
                />
                <p>
                  <strong>{student.name}</strong>
                </p>
                <p>{student.mssv}</p>
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
              src={selectedStudent.image}
              alt="avatar"
              width={120}
              height={140}
            />
            <div className={styles.studentDetail}>
              <p>
                <strong>Họ Tên:</strong> {selectedStudent.name}
              </p>
              <p>
                <strong>MSSV:</strong> {selectedStudent.mssv}
              </p>
              <p>
                <strong>Lớp:</strong> {selectedStudent.lop}
              </p>
              <p>
                <strong>Ngày sinh:</strong> {selectedStudent.ngaysinh}
              </p>
              <p>
                <strong>Quê quán:</strong> {selectedStudent.quequan}
              </p>
              <p>
                <strong>Giới tính:</strong> {selectedStudent.gioitinh}
              </p>
              <p>
                <strong>CMND:</strong> {selectedStudent.cmnd}
              </p>
              <p>
                <strong>Ngành:</strong> {selectedStudent.nganh}
              </p>
              <p>
                <strong>Khoa:</strong> {selectedStudent.khoa}
              </p>
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
