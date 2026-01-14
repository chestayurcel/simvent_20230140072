# 📦 SIMVENT (Sistem Inventarisasi Aset Laboratorium dan Sarana Olahraga Sekolah)

![Android](https://img.shields.io/badge/Android-Kotlin-green?style=flat&logo=android)
![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose-4285F4?style=flat&logo=jetpackcompose)
![Backend](https://img.shields.io/badge/Backend-PHP%20Native-777BB4?style=flat&logo=php)
![Database](https://img.shields.io/badge/Database-MySQL-4479A1?style=flat&logo=mysql)

**SIMVENT** adalah aplikasi Android berbasis *native* yang dirancang untuk memudahkan proses pencatatan, pemantauan, dan pengelolaan aset inventaris di sebuah sekolah. Aplikasi ini dibangun menggunakan **Kotlin** dengan antarmuka modern **Jetpack Compose** dan arsitektur **MVVM (Model-View-ViewModel)**.

---

## 📖 Penjelasan Aplikasi

Aplikasi ini berfungsi sebagai klien (Front-end) yang terhubung ke server backend (PHP & MySQL). SIMVENT memungkinkan admin untuk mengelola data barang (aset) dan lokasi (ruangan) secara *real-time*. Dengan fitur validasi token, aplikasi ini menjamin keamanan sesi pengguna.

### Struktur Repositori
Repositori ini terdiri dari 2 *branch* utama:
1.  **`master`**: Berisi *source code* aplikasi Android (Kotlin).
2.  **`backend`**: Berisi *source code* API (PHP Native) dan file Database (.sql).

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

### Prasyarat
1.  **Android Studio** (Versi terbaru, support Jetpack Compose).
2.  **Web Server** (XAMPP atau Laragon).

### Langkah 1: Download Source Code
1.  Clone repositori ini ke komputer Anda.
2.  **Untuk Android:** Gunakan kode yang ada di branch `master`.
3.  **Untuk Backend:** Checkout ke branch `backend` untuk mendapatkan folder API dan file SQL.

    ```bash
    git checkout backend
    ```

### Langkah 2: Setup Backend (Server)

Pilih salah satu sesuai aplikasi web server yang Anda gunakan:

#### ➤ Opsi A: Pengguna XAMPP
1.  Salin folder backend (API) ke dalam folder instalasi XAMPP:
    * Path: `C:\xampp\htdocs\`
    * Contoh hasil: `C:\xampp\htdocs\simvent-api\`
2.  Nyalakan **Apache** dan **MySQL** pada Control Panel XAMPP.

#### ➤ Opsi B: Pengguna Laragon
1.  Salin folder backend (API) ke dalam folder instalasi Laragon:
    * Path: `C:\laragon\www\`
    * Contoh hasil: `C:\laragon\www\simvent-api\`
2.  Nyalakan server (Start All) pada Laragon.

#### Setup Database (Sama untuk XAMPP & Laragon)
1.  Buka browser dan akses `phpMyAdmin` (`http://localhost/phpmyadmin`).
2.  Buat database baru dengan nama: **`simvent`**.
3.  Import file SQL yang ada di dalam folder backend tadi ke database `simvent`.
4.  Cek koneksi database di file PHP dan pastikan username/password sesuai.

### Langkah 3: Setup Android Project
1.  Buka Android Studio.
2.  Pilih **Open** dan arahkan ke folder proyek yang sudah diclone sebelumnya (dari branch `master`).
3.  Cari tahu **IP Address** komputer Anda:
    * *Windows:* Buka CMD -> ketik `ipconfig` -> salin IPv4 Address.
4.  Buka file konfigurasi API (`data/network/ApiConfig.kt`).
5.  Ubah **BASE_URL** sesuai IP Address Anda:
    ```kotlin
    // Ganti 192.168.x.x dengan IP Laptop Anda
    const val BASE_URL = "[http://192.168.1.10/simvent-api/](http://192.168.1.10/simvent-api/)"
    ```
6.  Klik **Sync Gradle**.

### Langkah 4: Jalankan Aplikasi
1.  Sambungkan HP Android atau nyalakan Emulator.
2.  Klik tombol **Run (▶)** di Android Studio.
3.  Login menggunakan akun admin yang tersedia di database.

---

## 👤 Author

**Chesta Yurcel Zebada**

Dikembangkan sebagai proyek akhir mata kuliah Pengembangan Aplikasi Mobile (PAM) Jurusan Teknologi Informasi Universitas Muhammadiyah Yogyakarta.
