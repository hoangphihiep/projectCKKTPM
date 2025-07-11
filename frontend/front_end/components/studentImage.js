"use client";
import { useState } from "react";
import Image from "next/image";

export default function StudentImage({ src, alt, width = 180, height = 220 }) {
  const [imgSrc, setImgSrc] = useState(src || "/whiteimage.png");

  return (
    <Image
      src={imgSrc}
      alt={alt}
      width={width}
      height={height}
      onError={() => setImgSrc("/whiteimage.png")}
    />
  );
}
