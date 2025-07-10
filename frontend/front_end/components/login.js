"use client";

import { useState, useEffect } from "react";
import styles from "./login.module.css"; // CSS module cho trang Login
import Image from "next/image";

const Login = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState(""); // State để lưu lỗi
  const [time, setTime] = useState(new Date().toLocaleTimeString()); // State để lưu thời gian

  const handleLogin = (e) => {
    e.preventDefault();
    // Xử lý logic đăng nhập (ví dụ gọi API ở đây)
    console.log("Đăng nhập với", username, password);
  };

  useEffect(() => {
    const interval = setInterval(() => {
      setTime(new Date().toLocaleTimeString()); // Cập nhật thời gian hiện tại
    }, 1000);

    // Dọn dẹp interval khi component bị unmount
    return () => clearInterval(interval);
  }, []);

  useEffect(() => {
    if (!username || !password) {
      setError("Mã sinh viên và mật khẩu không được để trống!");
    } else {
      setError(""); // Xóa lỗi khi thông tin hợp lệ
    }
  }, [username, password]); // Chạy lại mỗi khi `username` hoặc `password` thay đổi

  return (
    <>
      <div className={styles.container}>
        <div className={styles.loginBox}>
          <div className={styles.logoContainer}>
            <Image
              src="/logohcmute.png" // Đường dẫn tới logo trong thư mục public
              alt="Logo"
              width={100} // Kích thước tùy chỉnh cho logo
              height={150} // Kích thước tùy chỉnh cho logo
            />
          </div>
          <h1 className={styles.header}>
            Trường Đại học Sư phạm Kỹ thuật TP.HCM
          </h1>
          <form onSubmit={handleLogin}>
            <div className={styles.inputGroup}>
              <label htmlFor="username">Tài khoản đăng nhập</label>
              <input
                type="text"
                id="username"
                name="username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="Nhập tài khoản"
                required
                className={styles.input}
              />
            </div>
            <div className={styles.inputGroup}>
              <label htmlFor="password">Mật khẩu</label>
              <input
                type="password"
                id="password"
                name="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Nhập mật khẩu"
                required
                className={styles.input}
              />
            </div>
            <button type="submit" className={styles.submitBtn}>
              Đăng nhập
            </button>
          </form>
          <div className={styles.clock}>
            <h3>{time}</h3>
          </div>
        </div>
      </div>
      <footer className={styles.footer}>
        <h1>Copyright © 2017 Trường Đại học Sư phạm Kỹ thuật TP.HCM </h1>
      </footer>
    </>
  );
};

export default Login;
