package ipamapp;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.IOException;

public class Main {

    //buat IPEntry, atau bahasa lainnya tabel data
    static class IPEntry {
        private int no;
        private String ip;
        private int vlan;
        private String description;
        private String location;

        public IPEntry(int no, String ip, int vlan, String description, String location) {
            this.no = no;
            this.ip = ip;
            this.vlan = vlan;
            this.description = description;
            this.location = location;
        }

        public int getNo() { return no; }
        public String getIp() { return ip; }
        public int getVlan() { return vlan; }
        public String getDescription() { return description; }
        public String getLocation() { return location; }

        public void setNo(int no) { this.no = no; }
        public void setIp(String ip) { this.ip = ip; }
        public void setVlan(int vlan) { this.vlan = vlan; }
        public void setDescription(String description) { this.description = description; }
        public void setLocation(String location) { this.location = location; }
    }

    // buat ip managernya, atau sebut aja buat jalanin fungsi CRUD
    static class IPManager {
        private IPEntry[] entries;
        private int count;

        public IPManager(int capacity) {
            entries = new IPEntry[capacity];
            count = 0;
        }

        public boolean addEntry(IPEntry entry) {
            if (count >= entries.length) return false;
            entries[count++] = entry;
            return true;
        }

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

        public IPEntry get(int index) {
            if (index < 0 || index >= count) return null;
            return entries[index];
        }

        public int size() { return count; }

        public IPEntry[] getAll() {
            IPEntry[] out = new IPEntry[count];
            for (int i = 0; i < count; i++) out[i] = entries[i];
            return out;
        }

        public int findIndexByNo(int no) {
            for (int i = 0; i < count; i++) {
                if (entries[i].getNo() == no) return i;
            }
            return -1;
        }

        public int findIndexByIp(String ipQuery) {
            for (int i = 0; i < count; i++) {
                String storedIp = entries[i].getIp();

            if (storedIp.equals(ipQuery)) {
                return i;
            }

            if (storedIp.startsWith(ipQuery + "/")) {
                return i;
            }
    }
    return -1;
}

        public void clear() {
            for (int i = 0; i < count; i++) entries[i] = null;
            count = 0;
        }

        public void reindex() {
            for (int i = 0; i < count; i++) {
                if (entries[i] != null) entries[i].setNo(i + 1);
            }
        }
    }

    // main app nya 
    private static final Scanner in = new Scanner(System.in);
    private static final IPManager manager = new IPManager(200);
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
                    //buat auto save pas exit
                    System.out.println("Menyimpan data sebelum keluar...");
                    menuSimpan();
                    System.out.println("Keluar aplikasi.");
                    return;
                default: System.out.println("Pilihan tidak valid.\n");
            }
        }
    }

    private static void printMenu() {
        System.out.println("======================================");
        System.out.println("   IP ADDRESS MANAGEMENT   ");
        System.out.println("======================================");
        System.out.println("1. Tambah data IP");
        System.out.println("2. Lihat semua data");
        System.out.println("3. Update data IP (by No)");
        System.out.println("4. Hapus data IP (by No)");
        System.out.println("5. Cari data (by IP)");
        System.out.println("6. Simpan data ke file");
        System.out.println("7. Muat data dari file");
        System.out.println("0. Keluar");
        System.out.println("--------------------------------------");
    }

    //create
    private static void menuTambah() {
        System.out.println("[TAMBAH DATA IP]");

        int no = manager.size() + 1;
        String ip = readLine("IP Address: ");
        int vlan = readInt("VLAN: ");
        String desc = readLine("Deskripsi: ");
        String location = readLine("Lokasi: ");

        IPEntry entry = new IPEntry(no, ip, vlan, desc, location);

        if (manager.addEntry(entry)) {
            manager.reindex();
            System.out.println(">> Data berhasil ditambahkan. Assigned No: " + no + "\n");
        } else {
            System.out.println("!! Gagal menambah (kapasitas penuh)\n");
        }
    }

    //read
    private static void menuLihat() {
        System.out.println("[DAFTAR IP ADDRESS]");

        if (manager.size() == 0) {
            System.out.println("Belum ada data.\n");
            return;
        }

        System.out.printf("%-5s %-15s %-6s %-25s %-15s%n",
                "No", "IP Address", "VLAN", "Description", "Location");
        System.out.println("-----------------------------------------------------------------");

        for (IPEntry e : manager.getAll()) {
            System.out.printf("%-5d %-15s %-6d %-25s %-15s%n",
                    e.getNo(), e.getIp(), e.getVlan(),
                    e.getDescription(), e.getLocation());
        }
        System.out.println();
    }

    //update
    private static void menuUpdate() {
        System.out.println("[UPDATE DATA IP]");

        int no = readInt("No yang mau diupdate: ");
        int idx = manager.findIndexByNo(no);

        if (idx == -1) {
            System.out.println("!! Data tidak ditemukan.\n");
            return;
        }

        IPEntry e = manager.get(idx);

        System.out.println("Data lama:");
        System.out.printf("IP: %s, VLAN: %d, Desc: %s, Loc: %s%n",
                e.getIp(), e.getVlan(), e.getDescription(), e.getLocation());

        String ipBaru   = readLineAllowEmpty("IP baru (kosong = tetap): ");
        String vlanBaru = readLineAllowEmpty("VLAN baru (kosong = tetap): ");
        String descBaru = readLineAllowEmpty("Deskripsi baru (kosong = tetap): ");
        String locBaru  = readLineAllowEmpty("Lokasi baru (kosong = tetap): ");

        if (!ipBaru.isEmpty()) e.setIp(ipBaru);
        if (!vlanBaru.isEmpty()) {
            try { e.setVlan(Integer.parseInt(vlanBaru.trim())); }
            catch (Exception ignored) {}
        }
        if (!descBaru.isEmpty()) e.setDescription(descBaru);
        if (!locBaru.isEmpty()) e.setLocation(locBaru);

        System.out.println(">> Data diupdate.\n");
    }
    // delete 
    private static void menuHapus() {
        System.out.println("[HAPUS DATA IP]");

        int no = readInt("Masukkan No yang mau dihapus: ");
        int idx = manager.findIndexByNo(no);

        if (idx == -1) {
            System.out.println("!! Data tidak ditemukan.\n");
            return;
        }

        if (!confirmYesNo("Hapus entry No " + no + " ? (y/n): ")) {
            System.out.println(">> Batal dihapus.\n");
            return;
        }

        manager.removeAt(idx);
        System.out.println(">> Data dihapus.\n");
    }

    //buat search data
    private static void menuCari() {
        System.out.println("[CARI DATA IP]");
        String ip = readLine("Masukkan IP: ");

        int idx = manager.findIndexByIp(ip);
        if (idx == -1) {
            System.out.println("!! Tidak ditemukan.\n");
            return;
        }

        IPEntry e = manager.get(idx);
        System.out.println("Data ditemukan:");
        System.out.printf("No  : %d%n", e.getNo());
        System.out.printf("IP  : %s%n", e.getIp());
        System.out.printf("VLAN: %d%n", e.getVlan());
        System.out.printf("Desc: %s%n", e.getDescription());
        System.out.printf("Loc : %s%n%n", e.getLocation());
    }

    //save manual
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
            System.out.println(">> Data disimpan ke file: " + DATA_FILE + "\n");
        } catch (IOException ex) {
            System.out.println("!! Gagal menyimpan data: " + ex.getMessage() + "\n");
        }
    }

    //load data dari file excel
    private static void menuMuat() {
        try (BufferedReader br = new BufferedReader(new FileReader(DATA_FILE))) {
            manager.clear();
            String line;
            while ((line = br.readLine()) != null) {
                String[] d = line.split(";", -1);
                if (d.length < 5) continue;
                String ip = d[1];
                int vlan = Integer.parseInt(d[2]);
                String desc = unescape(d[3]);
                String loc = unescape(d[4]);

                manager.addEntry(new IPEntry(0, ip, vlan, desc, loc));
            }
            manager.reindex();
            System.out.println(">> Data berhasil dimuat. Total record: " + manager.size() + "\n");
        } catch (IOException ex) {
            System.out.println("!! Gagal memuat data (file belum ada atau rusak).\n");
        } catch (NumberFormatException ex) {
            System.out.println("!! Format data di file tidak valid.\n");
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
            String line = in.nextLine().trim();
            try {
                return Integer.parseInt(line);
            } catch (NumberFormatException e) {
                System.out.println("Input harus angka.");
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
        return s.replace("\\", "\\\\").replace(";", "\\;").replace("\n", "\\n");
    }

    private static String unescape(String s) {
        if (s == null) return "";
        return s.replace("\\n", "\n").replace("\\;", ";").replace("\\\\", "\\");
    }
}
