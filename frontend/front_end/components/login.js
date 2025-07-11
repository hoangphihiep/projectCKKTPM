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

  const handleLogin = async (e) => {
    e.preventDefault()

    if (!username.trim() || !password.trim()) {
      setError("Tài khoản và mật khẩu không được để trống!")
      return
    }
    setError("")

    try {
      const response = await fetch("http://localhost:8080/api/auth/login", {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        credentials: "include", // Important for session cookies
        body: JSON.stringify({
          username: username.trim(),
          password: password.trim(),
        }),
      })

      const data = await response.json()

      if (data.success) {
        console.log("Đăng nhập thành công:", data)

        // Store user info in localStorage if needed
        localStorage.setItem(
          "user",
          JSON.stringify({
            username: data.username,
            authorities: data.authorities,
          }),
        )

        // Redirect to main page
        router.push("/mainpage")
      } else {
        setError(data.message || "Đăng nhập thất bại")
      }
    } catch (error) {
      console.error("Lỗi khi đăng nhập:", error)
      setError("Không thể kết nối đến server. Vui lòng thử lại sau.")
    }
  }

  // Check if user is already authenticated
  useEffect(() => {
    const checkAuth = async () => {
      try {
        const response = await fetch("http://localhost:8080/api/auth/check", {
          credentials: "include",
        })
        const data = await response.json()

        if (data.authenticated) {
          router.push("/mainpage")
        }
      } catch (error) {
        console.log("Not authenticated or server error")
      }
    }

    checkAuth()
  }, [router])

  return (
    <>
      <div className={styles.container}>
        <div className={styles.loginBox}>
          <div className={styles.logoContainer}>
            <Image src="/logohcmute.png" alt="Logo" width={120} height={150} />
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
