# 📦 SIMVENT (Sistem Inventarisasi Manajemen Aset)

![Android](https://img.shields.io/badge/Android-Kotlin-green?style=flat&logo=android)
![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=flat&logo=jetpackcompose)
![Backend](https://img.shields.io/badge/Backend-PHP%20Native-777BB4?style=flat&logo=php)
![Database](https://img.shields.io/badge/Database-MySQL-4479A1?style=flat&logo=mysql)

**SIMVENT** adalah aplikasi Android berbasis *native* yang dirancang untuk memudahkan proses pencatatan, pemantauan, dan pengelolaan aset inventaris di sebuah sekolah. Aplikasi ini dibangun menggunakan **Kotlin** dengan antarmuka modern **Jetpack Compose** dan arsitektur **MVVM (Model-View-ViewModel)**.

---

## 📖 Penjelasan Aplikasi

Aplikasi ini berfungsi sebagai klien (Front-end) yang terhubung ke server backend (PHP & MySQL). SIMVENT memungkinkan admin untuk mengelola data barang (aset) dan lokasi (ruangan) secara *real-time*. Dengan fitur validasi token, aplikasi ini menjamin keamanan sesi pengguna.

### Arsitektur & Teknologi
* **Bahasa:** Kotlin
* **UI Toolkit:** Jetpack Compose
* **Architecture:** MVVM + Repository Pattern (Clean Architecture)
* **Networking:** Retrofit + OkHttp
* **Local Storage:** DataStore Preferences (untuk menyimpan Session Token)
* **Backend:** PHP Native (REST API)
* **Database:** MySQL

---

## ✨ Fitur Utama

Berikut adalah fitur-fitur unggulan yang telah diimplementasikan:

### 1. 🔐 Otentikasi & Keamanan
* **Login Admin:** Menggunakan JWT/Token-based authentication.
* **Auto Logout:** Sistem otomatis keluar jika token kedaluwarsa atau sesi tidak valid.
* **Splash Screen:** Tampilan pembuka dengan logo aplikasi.

### 2. 📊 Dashboard Interaktif
* Menampilkan ringkasan total aset dan total ruangan.
* Statistik kondisi barang (Jumlah "Baik" vs "Rusak").
* Navigasi cepat ke menu utama.

### 3. 📦 Manajemen Aset (CRUD)
* **Lihat Daftar:** Menampilkan semua aset dengan indikator warna kondisi.
* **Tambah Aset:** Input data barang lengkap dengan pilihan Ruangan (Dropdown).
* **Edit Aset:** Memperbarui informasi barang.
* **Hapus Aset:** Menghapus data barang dengan konfirmasi keamanan.
* **Pencarian & Filter:** Cari barang berdasarkan nama dan filter berdasarkan lokasi ruangan.

### 4. 🏢 Manajemen Ruangan (CRUD)
* **Kelola Lokasi:** Tambah, Edit, dan Hapus data ruangan.
* **Validasi Hapus:** Mencegah penghapusan ruangan jika masih terdapat aset di dalamnya.

---

## 🚀 Panduan Instalasi & Penggunaan

Ikuti langkah-langkah berikut untuk menjalankan proyek ini di komputer lokal Anda.

### Prasyarat
1.  **Android Studio** (Versi terbaru, support Jetpack Compose).
2.  **XAMPP** (atau web server lain untuk PHP & MySQL).
3.  **Kabel Data / Emulator** Android.

### Langkah 1: Setup Backend (Server)
1.  Aktifkan **Apache** dan **MySQL** pada XAMPP.
2.  Buka `phpMyAdmin` dan buat database baru bernama `simvent_db`.
3.  Import file SQL database (biasanya `simvent_db.sql`) ke dalam database tersebut.
4.  Pastikan folder API PHP Anda (`simvent-api`) tersimpan di folder `htdocs`.
5.  **PENTING:** Cari tahu IP Address laptop Anda.
    * *Windows:* Buka CMD -> ketik `ipconfig` -> salin IPv4 Address (misal: `192.168.1.10`).

### Langkah 2: Setup Android Project
1.  Clone repositori ini atau download ZIP.
2.  Buka proyek di **Android Studio**.
3.  Buka file konfigurasi API (biasanya di `data/network/ApiService.kt` atau `RetrofitClient.kt`).
4.  Ubah **BASE_URL** sesuai IP Address laptop Anda:
    ```kotlin
    // Jangan gunakan "localhost", gunakan IP Address LAN
    const val BASE_URL = "[http://192.168.1.10/simvent-api/](http://192.168.1.10/simvent-api/)" 
    ```
5.  Tunggu proses *Gradle Sync* selesai.

### Langkah 3: Jalankan Aplikasi
1.  Sambungkan HP Android atau nyalakan Emulator.
2.  Klik tombol **Run (▶)** di Android Studio.
3.  Aplikasi akan terinstall. Login menggunakan akun admin yang sudah ada di database.

---

## 👤 Author

**Chesta Yurcel Zebada**

Dikembangkan sebagai proyek akhir mata kuliah Pengembangan Aplikasi Mobile (PAM) Jurusan Teknologi Informasi Universitas Muhammadiyah Yogyakarta.

---

*Dibuat dengan ❤️ menggunakan Kotlin & Jetpack Compose.*
