"use client"
import { useState } from "react"
import Image from "next/image"

export default function StudentImage({ src, alt, width = 180, height = 220, className = "" }) {
  const [imgSrc, setImgSrc] = useState(src || "/whiteimage.png")
  const [isLoading, setIsLoading] = useState(true)

  const handleImageLoad = () => {
    setIsLoading(false)
  }

  const handleImageError = () => {
    console.log("Image failed to load:", src)
    setImgSrc("/whiteimage.png")
    setIsLoading(false)
  }

  // Kiểm tra nếu src là URL hợp lệ
  const isValidUrl = (string) => {
    try {
      new URL(string)
      return true
    } catch (_) {
      return false
    }
  }

  // Xử lý src để đảm bảo có URL hợp lệ
  const getImageSrc = () => {
    if (!src) return "/whiteimage.png"

    // Nếu là URL đầy đủ (http/https)
    if (isValidUrl(src)) {
      return src
    }

    // Nếu là đường dẫn local
    if (src.startsWith("/")) {
      return src
    }

    // Fallback
    return "/whiteimage.png"
  }

  return (
    <div className={`student-image-container ${className}`} style={{ position: "relative", width, height }}>
      {isLoading && (
        <div
          style={{
            position: "absolute",
            top: 0,
            left: 0,
            width: "100%",
            height: "100%",
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            backgroundColor: "#f0f0f0",
            border: "1px solid #ddd",
          }}
        >
          <span style={{ fontSize: "12px", color: "#666" }}>Đang tải...</span>
        </div>
      )}
      <Image
        src={getImageSrc() || "/placeholder.svg"}
        alt={alt || "Ảnh sinh viên"}
        width={width}
        height={height}
        onLoad={handleImageLoad}
        onError={handleImageError}
        style={{
          objectFit: "cover",
          border: "1px solid #ddd",
          borderRadius: "4px",
        }}
        // Thêm unoptimized cho external URLs
        unoptimized={isValidUrl(src)}
      />
    </div>
  )
}
