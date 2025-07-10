"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation"; // ✅ Import router
import styles from "./login.module.css";
import Image from "next/image";

const Login = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const router = useRouter(); // ✅ Khởi tạo router

  const handleLogin = (e) => {
    e.preventDefault();

    // ✅ Giả sử tài khoản thanh tra là: thanhtra / 123456
    if (username === "thanhtra" && password === "123456") {
      console.log("Đăng nhập thành công");

      // ✅ Điều hướng sang trang chính
      router.push("/mainpage");
    } else {
      setError("Tài khoản hoặc mật khẩu không chính xác!");
    }
  };

  useEffect(() => {
    if (!username || !password) {
      setError("Mã sinh viên và mật khẩu không được để trống!");
    } else {
      setError(""); // Xoá lỗi nếu có dữ liệu
    }
  }, [username, password]);

  return (
    <>
      <div className={styles.container}>
        <div className={styles.loginBox}>
          <div className={styles.logoContainer}>
            <Image src="/logohcmute.png" alt="Logo" width={100} height={150} />
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
                className={styles.input}
              />
            </div>
            {error && <p className={styles.error}>{error}</p>}
            <button type="submit" className={styles.submitBtn}>
              Đăng nhập
            </button>
          </form>
        </div>
      </div>
      <footer className={styles.footer}>
        <h1>Copyright © 2017 Trường Đại học Sư phạm Kỹ thuật TP.HCM </h1>
      </footer>
    </>
  );
};

export default Login;
