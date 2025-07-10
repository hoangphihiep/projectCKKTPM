import Image from "next/image";
import styles from "./page.module.css";
import Login from "../../components/login";
export default function Home() {
  return (
    <div>
      <main>
        <Login />
      </main>
    </div>
  );
}
