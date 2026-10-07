<img width="490" height="490" alt="1000342720" src="https://github.com/user-attachments/assets/4ae35594-85b8-4995-8ad5-608f04a36de3" />



# 🚀 Boost-Fps-App (v1.0-beta)

[![Download v1.0-beta](https://img.shields.io/badge/Download_APK-v1.0--beta-2ba640?style=for-the-badge&logo=github&logoColor=white)](https://github.com/musgamerkeren23-afk/Boost-Fps-App/releases/tag/IDKBUTTHISMAKEMEWANNADIED)
[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![Shizuku](https://img.shields.io/badge/Requires-Shizuku-blue?style=for-the-badge)](https://shizuku.rikka.app)

Aplikasi Android untuk optimasi performa dan FPS game dengan memanfaatkan akses API **Shizuku** tanpa perlu melakukan *root* penuh pada perangkat.

> ⚠️ **Status:** **v1.0-beta**. Beberapa fitur masih dalam tahap penyempurnaan dan pengembangan aktif.

---

## ✨ Fitur Utama

- **Integrasi Shizuku Native:** Menjalankan perintah tingkat sistem dengan izin terbatas tanpa butuh *root*.
- **FPS & Performance Optimization:** Mengoptimalkan alokasi resource perangkat untuk pengalaman main game yang lebih lancar.
- **Automated CI/CD Build:** Kompilasi APK otomatis berbasis GitHub Actions di setiap *update* repositori.

---

## 📥 Cara Unduh & Instalasi Application

1. **Unduh APK:**
   - Klik tombol **Download APK** di atas atau kunjungi [Halaman Release v1.0-beta](https://github.com/musgamerkeren23-afk/Boost-Fps-App/releases/tag/IDKBUTTHISMAKEMEWANNADIED).
   - Unduh file `app-debug.apk` dari bagian *Assets*.

2. **Prasyarat (Shizuku):**
   - Pastikan aplikasi **Shizuku** sudah terinstal di HP kamu dan service-nya sudah berjalan (bisa via Wireless Debugging atau ADB PC).

3. **Install & Jalankan:**
   - Install file `app-debug.apk` pada perangkat Android.
   - Buka aplikasi **Boost-Fps-App** dan berikan izin (*grant permission*) Shizuku saat diminta.

---

## 🛠️ Panduan Pembuatan / Build dari Source Code

Jika kamu ingin mengompilasi atau mengoperasikan proyek ini sendiri dari *source code*, ikuti panduan berikut:

### Prasyarat System
- **Android Studio:** Hedgehog | 2023.1.1 atau yang lebih baru.
- **JDK / Java Version:** Java 17.
- **Gradle Version:** Gradle 8.4 (AGP 8.1.4).

### Langkah-Langkah Build (Lokal)

1. **Clone Repositori:**
   ```bash
   git clone [https://github.com/musgamerkeren23-afk/Boost-Fps-App.git](https://github.com/musgamerkeren23-afk/Boost-Fps-App.git)
   cd Boost-Fps-App
