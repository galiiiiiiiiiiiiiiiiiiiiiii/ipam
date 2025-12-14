package ipamapp;
// ini cuma nunjukkin file ini ada di folder "ipamapp" biar rapi aja

import java.util.Scanner;
// scanner = alat buat baca input yang user ketik

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.IOException;
// ini semua buat baca file, nulis file, dan kalau ada error pas baca-nulis

public class Main {
    // Main = pusat program. Java mulai jalan dari sini.

    // IPEntry = bentuk satu data IP Address. Kayak satu baris di tabel.
    static class IPEntry {

        // ini kolom-kolom datanya
        private int no;              // nomor ID (urutan)
        private String ip;           // alamat IP
        private int vlan;            // VLAN
        private String description;  // keterangan IP
        private String location;     // lokasi IP dipakai di mana

        // bikin data baru, ini dipanggil pas user tambah data
        public IPEntry(int no, String ip, int vlan, String description, String location) {
            this.no = no;
            this.ip = ip;
            this.vlan = vlan;
            this.description = description;
            this.location = location;
        }

        // ambil isi data (getter)
        public int getNo() { return no; }
        public String getIp() { return ip; }
        public int getVlan() { return vlan; }
        public String getDescription() { return description; }
        public String getLocation() { return location; }

        // ganti isi data (setter)
        public void setNo(int no) { this.no = no; }
        public void setIp(String ip) { this.ip = ip; }
        public void setVlan(int vlan) { this.vlan = vlan; }
        public void setDescription(String description) { this.description = description; }
        public void setLocation(String location) { this.location = location; }
    }

    // IPManager = tempat nyimpen banyak IPEntry sekaligus.
    // Ini bagian yang ngatur tambah data, hapus data, cari data, dsb.
    static class IPManager {

        private IPEntry[] entries; // array buat nyimpen banyak data
        private int count;         // berapa data yang sudah terisi

        // pas IPManager dibuat, kita siapin kapasitas array
        public IPManager(int capacity) {
            entries = new IPEntry[capacity];
            count = 0; // belum ada data di awal
        }

        // nambah data ke array
        public boolean addEntry(IPEntry entry) {
            if (count >= entries.length) return false;
            entries[count] = entry;
            count++;
            return true;
        }

        // hapus data berdasarkan nomor index array
        public boolean removeAt(int index) {
            if (index < 0 || index >= count) return false;

            for (int i = index; i < count - 1; i++) {
                entries[i] = entries[i + 1];
            }

            entries[count - 1] = null;
            count--;
            reindex();
            return true;
        }

        // ngambil data sesuai index
        public IPEntry get(int index) {
            if (index < 0 || index >= count) return null;
            return entries[index];
        }

        // jumlah data di database mini kita
        public int size() { return count; }

        // balikin semua data dalam bentuk array baru
        public IPEntry[] getAll() {
            IPEntry[] out = new IPEntry[count];
            for (int i = 0; i < count; i++) out[i] = entries[i];
            return out;
        }

        // cari data berdasarkan nomor ID
        public int findIndexByNo(int no) {
            for (int i = 0; i < count; i++) {
                if (entries[i].getNo() == no) return i;
            }
            return -1;
        }

        // cari berdasarkan IP
        public int findIndexByIp(String ipQuery) {
            for (int i = 0; i < count; i++) {
                String storedIp = entries[i].getIp();
                if (storedIp.equals(ipQuery)) return i;
                if (storedIp.startsWith(ipQuery + "/")) return i;
            }
            return -1;
        }

        // ngecek apakah IP sudah dipakai atau belum
        // ignoreIndex dipakai pas update supaya ga bentrok sama dirinya sendiri
        public boolean ipExists(String ip, int ignoreIndex) {
            for (int i = 0; i < count; i++) {
                if (i == ignoreIndex) continue;
                if (entries[i].getIp().equals(ip)) return true;
            }
            return false;
        }

        // hapus semua data
        public void clear() {
            for (int i = 0; i < count; i++) entries[i] = null;
            count = 0;
        }

        // update ulang nomor ID setelah ada yang dihapus
        public void reindex() {
            for (int i = 0; i < count; i++) {
                if (entries[i] != null) entries[i].setNo(i + 1);
            }
        }
    }

    private static final Scanner in = new Scanner(System.in);

    // kapasitas database dinaikkan jadi 1000
    private static final IPManager manager = new IPManager(1000);

    private static final String DATA_FILE = "ipdata_console.csv";

    public static void main(String[] args) {

        while (true) {

            printMenu();

            int pilihan = readInt("Pilih menu: ");
            System.out.println();

            switch (pilihan) {
                case 1: menuTambah(); break;
                case 2: menuLihat(); break;
                case 3: menuUpdate(); break;
                case 4: menuHapus(); break;
                case 5: menuCari(); break;
                case 6: menuSimpan(); break;
                case 7: menuMuat(); break;
                case 0:
                    System.out.println("Menyimpan data sebelum keluar...");
                    menuSimpan();
                    System.out.println("Keluar aplikasi.");
                    return;
                default:
                    System.out.println("Pilihan tidak valid.\n");
            }
        }
    }

    private static void printMenu() {
        System.out.println("===================================");
        System.out.println("        IP ADDRESS MANAGER");
        System.out.println("===================================");
        System.out.println("1. Tambah data IP");
        System.out.println("2. Lihat semua data");
        System.out.println("3. Update data (berdasarkan No)");
        System.out.println("4. Hapus data (berdasarkan No)");
        System.out.println("5. Cari data (berdasarkan IP)");
        System.out.println("6. Simpan data ke file");
        System.out.println("7. Muat data dari file");
        System.out.println("0. Keluar");
    }

    // menu tambah data baru (DITAMBAH CEK IP UNIK)
    private static void menuTambah() {
        System.out.println("[TAMBAH DATA]");

        int no = manager.size() + 1;
        String ip;

        while (true) {
            ip = readLine("IP Address: ");
            if (manager.ipExists(ip, -1)) {
                System.out.println("IP sudah dipakai, masukkan IP lain.\n");
            } else {
                break;
            }
        }

        int vlan = readInt("VLAN: ");
        String desc = readLine("Deskripsi: ");
        String location = readLine("Lokasi: ");

        IPEntry entry = new IPEntry(no, ip, vlan, desc, location);

        if (manager.addEntry(entry)) {
            manager.reindex();
            System.out.println(">> Data berhasil ditambah. No: " + no + "\n");
        } else {
            System.out.println("!! Gagal menambah. Kapasitas penuh.\n");
        }
    }

    private static void menuLihat() {
        System.out.println("[LIHAT DATA]");

        if (manager.size() == 0) {
            System.out.println("Belum ada data.\n");
            return;
        }

        System.out.printf("%-5s %-15s %-6s %-25s %-15s%n",
                "No", "IP", "VLAN", "Deskripsi", "Lokasi");

        for (IPEntry e : manager.getAll()) {
            System.out.printf("%-5d %-15s %-6d %-25s %-15s%n",
                    e.getNo(), e.getIp(), e.getVlan(),
                    e.getDescription(), e.getLocation());
        }

        System.out.println();
    }

    // update data lama (DITAMBAH CEK IP UNIK)
    private static void menuUpdate() {
        System.out.println("[UPDATE DATA]");

        int no = readInt("Masukkan No yang mau diupdate: ");
        int idx = manager.findIndexByNo(no);

        if (idx == -1) {
            System.out.println("Data tidak ditemukan.\n");
            return;
        }

        IPEntry e = manager.get(idx);

        String ipBaru = readLineAllowEmpty("IP baru (biarin kosong kalau ga mau ubah): ");
        if (!ipBaru.isEmpty()) {
            if (manager.ipExists(ipBaru, idx)) {
                System.out.println("IP sudah dipakai data lain.\n");
                return;
            }
            e.setIp(ipBaru);
        }

        String vlanBaru = readLineAllowEmpty("VLAN baru (kosong = tidak ubah): ");
        if (!vlanBaru.isEmpty()) {
            try { e.setVlan(Integer.parseInt(vlanBaru.trim())); }
            catch (Exception ignored) {}
        }

        String descBaru = readLineAllowEmpty("Deskripsi baru: ");
        if (!descBaru.isEmpty()) e.setDescription(descBaru);

        String locBaru = readLineAllowEmpty("Lokasi baru: ");
        if (!locBaru.isEmpty()) e.setLocation(locBaru);

        System.out.println(">> Data berhasil diupdate.\n");
    }

    private static void menuHapus() {
        System.out.println("[HAPUS DATA]");

        int no = readInt("Masukkan No yang mau dihapus: ");
        int idx = manager.findIndexByNo(no);

        if (idx == -1) {
            System.out.println("Data tidak ditemukan.\n");
            return;
        }

        if (!confirmYesNo("Yakin mau hapus? (y/n): ")) {
            System.out.println("Dibatalkan.\n");
            return;
        }

        manager.removeAt(idx);
        System.out.println(">> Data berhasil dihapus.\n");
    }

    private static void menuCari() {
        System.out.println("[CARI DATA]");
        String ip = readLine("Masukkan IP: ");

        int idx = manager.findIndexByIp(ip);

        if (idx == -1) {
            System.out.println("Tidak ketemu.\n");
            return;
        }

        IPEntry e = manager.get(idx);

        System.out.println("Data ditemukan:");
        System.out.println("No: " + e.getNo());
        System.out.println("IP: " + e.getIp());
        System.out.println("VLAN: " + e.getVlan());
        System.out.println("Deskripsi: " + e.getDescription());
        System.out.println("Lokasi: " + e.getLocation());
        System.out.println();
    }

    private static void menuSimpan() {
        try (PrintWriter pw = new PrintWriter(DATA_FILE)) {

            manager.reindex();

            for (IPEntry e : manager.getAll()) {
                pw.println(e.getNo() + ";" +
                        e.getIp() + ";" +
                        e.getVlan() + ";" +
                        escape(e.getDescription()) + ";" +
                        escape(e.getLocation()));
            }

            System.out.println("Data tersimpan ke file.\n");

        } catch (IOException ex) {
            System.out.println("Gagal menyimpan.\n");
        }
    }

    private static void menuMuat() {
        try (BufferedReader br = new BufferedReader(new FileReader(DATA_FILE))) {

            manager.clear();

            String line;

            while ((line = br.readLine()) != null) {

                String[] d = line.split(";", -1);
                if (d.length < 5) continue;

                manager.addEntry(new IPEntry(
                        0,
                        d[1],
                        Integer.parseInt(d[2]),
                        unescape(d[3]),
                        unescape(d[4])
                ));
            }

            manager.reindex();
            System.out.println("Data berhasil dimuat.\n");

        } catch (IOException ex) {
            System.out.println("File tidak ditemukan atau rusak.\n");
        }
    }

    private static boolean confirmYesNo(String prompt) {
        System.out.print(prompt);
        String ans = in.nextLine().trim().toLowerCase();
        return ans.equals("y") || ans.equals("yes");
    }

    private static int readInt(String msg) {
        while (true) {
            System.out.print(msg);
            try {
                return Integer.parseInt(in.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Harus angka.");
            }
        }
    }

    private static String readLine(String msg) {
        System.out.print(msg);
        return in.nextLine().trim();
    }

    private static String readLineAllowEmpty(String msg) {
        System.out.print(msg);
        return in.nextLine();
    }

    private static String escape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace(";", "\\;")
                .replace("\n", "\\n");
    }

    private static String unescape(String s) {
        if (s == null) return "";
        return s.replace("\\n", "\n")
                .replace("\\;", ";")
                .replace("\\\\", "\\");
    }
}
