package ipamapp.ui;

import ipamapp.model.IPEntry;
import ipamapp.model.IPManager;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.AbstractAction;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;

public class MainFrame extends JFrame {

    private final IPManager manager = new IPManager(1000);

    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"No", "IP Address", "VLAN", "Name", "Desc", "Subnet", "Device", "Location"}, 0
    );

    private JTable table;
    private JTextField tfNo, tfIP, tfVLAN, tfName, tfDesc, tfSubnet, tfLocation;
    private JComboBox<String> cbDevice;

    private JButton btnAdd, btnRemove, btnSave, btnLoad, btnUpdate;

    public MainFrame() {
        super("IP Address Management - ARRAY Version");
        initUI();
        setSize(1000, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void initUI() {
        setLayout(new BorderLayout());

        // ---------- TOP INPUT ----------
        JPanel pTop = new JPanel(new GridLayout(4, 4, 7, 7));
        tfNo = new JTextField();
        tfIP = new JTextField();
        tfVLAN = new JTextField();
        tfName = new JTextField();
        tfDesc = new JTextField();
        tfSubnet = new JTextField();
        tfLocation = new JTextField();

        cbDevice = new JComboBox<>(new String[]{
                "Switch", "Router", "Access Point", "Server", "PC", "Printer", "Other"
        });

        pTop.add(new JLabel("No"));
        pTop.add(tfNo);
        pTop.add(new JLabel("IP Address"));
        pTop.add(tfIP);

        pTop.add(new JLabel("VLAN"));
        pTop.add(tfVLAN);
        pTop.add(new JLabel("Name"));
        pTop.add(tfName);

        pTop.add(new JLabel("Description"));
        pTop.add(tfDesc);
        pTop.add(new JLabel("Subnet Mask"));
        pTop.add(tfSubnet);

        pTop.add(new JLabel("Device Type"));
        pTop.add(cbDevice);
        pTop.add(new JLabel("Location"));
        pTop.add(tfLocation);

        add(pTop, BorderLayout.NORTH);

        // ---------- CENTER: TABLE ----------
        table = new JTable(tableModel);
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        JScrollPane sp = new JScrollPane(table);
        add(sp, BorderLayout.CENTER);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) populateFormFromSelectedRow();
            }
        });

        // ---------- BOTTOM BUTTONS ----------
        JPanel panelActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        btnUpdate = new JButton("Update Entry");
        btnRemove = new JButton("Delete Entry");
        btnSave = new JButton("Save Data");
        btnLoad = new JButton("Load Data");
        btnAdd = new JButton("Tambah Entry");

        panelActions.add(btnUpdate);
        panelActions.add(btnRemove);
        panelActions.add(btnSave);
        panelActions.add(btnLoad);
        panelActions.add(btnAdd);

        add(panelActions, BorderLayout.SOUTH);

        // ---------- LISTENERS ----------
        btnAdd.addActionListener(e -> onAdd());
        btnRemove.addActionListener(e -> onRemove());
        btnUpdate.addActionListener(e -> onUpdate());
        btnSave.addActionListener(e -> saveToCSV());
        btnLoad.addActionListener(e -> loadFromCSV());

        // DELETE key
        table.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
                .put(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0), "deleteRow");
        table.getActionMap().put("deleteRow", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                btnRemove.doClick();
            }
        });

        // ---------- MENU BAR ----------
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem miLoad = new JMenuItem("Load Data");
        JMenuItem miSave = new JMenuItem("Save Data");
        JMenuItem miExit = new JMenuItem("Exit");

        miLoad.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.CTRL_DOWN_MASK));
        miSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK));
        miExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, KeyEvent.CTRL_DOWN_MASK));

        miLoad.addActionListener(e -> loadFromCSV());
        miSave.addActionListener(e -> saveToCSV());
        miExit.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(this, "Keluar aplikasi?",
                    "Exit", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
                System.exit(0);
        });

        fileMenu.add(miLoad);
        fileMenu.add(miSave);
        fileMenu.addSeparator();
        fileMenu.add(miExit);
        menuBar.add(fileMenu);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem miDelete = new JMenuItem("Delete Selected");
        miDelete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        miDelete.addActionListener(e -> btnRemove.doClick());
        editMenu.add(miDelete);
        menuBar.add(editMenu);

        JMenu viewMenu = new JMenu("View");
        JMenuItem miRefresh = new JMenuItem("Refresh Table");
        miRefresh.addActionListener(e -> refreshTable());
        viewMenu.add(miRefresh);
        menuBar.add(viewMenu);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem miAbout = new JMenuItem("About");
        miAbout.addActionListener(e ->
                JOptionPane.showMessageDialog(this,
                        "IP Address Management - ARRAY Version\nCreated for Tugas Kuliah",
                        "About", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(miAbout);
        menuBar.add(helpMenu);

        setJMenuBar(menuBar);
    }

    private void populateFormFromSelectedRow() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) return;

        int modelRow = table.convertRowIndexToModel(viewRow);
        IPEntry e = manager.get(modelRow);
        if (e == null) return;

        tfNo.setText(String.valueOf(e.getNo()));
        tfIP.setText(e.getIp());
        tfVLAN.setText(String.valueOf(e.getVlan()));
        tfName.setText(e.getName());
        tfDesc.setText(e.getDescription());
        tfSubnet.setText(e.getSubnetMask());
        cbDevice.setSelectedItem(e.getDeviceType());
        tfLocation.setText(e.getLocation());
    }

    private void onAdd() {
        try {
            int no = Integer.parseInt(tfNo.getText().trim());
            String ip = tfIP.getText().trim();
            int vlan = Integer.parseInt(tfVLAN.getText().trim());
            String name = tfName.getText().trim();
            String desc = tfDesc.getText().trim();
            String subnet = tfSubnet.getText().trim();
            String device = cbDevice.getSelectedItem().toString();
            String location = tfLocation.getText().trim();

            IPEntry entry = new IPEntry(no, ip, vlan, name, desc, subnet, device, location);

            if (!manager.addEntry(entry)) {
                JOptionPane.showMessageDialog(this, "Database penuh!", "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            tableModel.addRow(new Object[]{
                    no, ip, vlan, name, desc, subnet, device, location
            });

            JOptionPane.showMessageDialog(this, "Entry ditambahkan.", "Sukses",
                    JOptionPane.INFORMATION_MESSAGE);
            clearInputs();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "No & VLAN harus angka!",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void onRemove() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Pilih entry dulu!", "Hapus",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (JOptionPane.showConfirmDialog(this,
                "Hapus entry ini?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) return;

        int modelRow = table.convertRowIndexToModel(viewRow);

        manager.removeAt(modelRow);
        tableModel.removeRow(modelRow);

        JOptionPane.showMessageDialog(this, "Entry dihapus.");
    }

    private void onUpdate() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Pilih entry untuk update!",
                    "Update", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);

        try {
            int no = Integer.parseInt(tfNo.getText().trim());
            String ip = tfIP.getText().trim();
            int vlan = Integer.parseInt(tfVLAN.getText().trim());
            String name = tfName.getText().trim();
            String desc = tfDesc.getText().trim();
            String subnet = tfSubnet.getText().trim();
            String device = cbDevice.getSelectedItem().toString();
            String location = tfLocation.getText().trim();

            IPEntry entry = manager.get(modelRow);
            entry.setNo(no);
            entry.setIp(ip);
            entry.setVlan(vlan);
            entry.setName(name);
            entry.setDescription(desc);
            entry.setSubnetMask(subnet);
            entry.setDeviceType(device);
            entry.setLocation(location);

            tableModel.setValueAt(no, modelRow, 0);
            tableModel.setValueAt(ip, modelRow, 1);
            tableModel.setValueAt(vlan, modelRow, 2);
            tableModel.setValueAt(name, modelRow, 3);
            tableModel.setValueAt(desc, modelRow, 4);
            tableModel.setValueAt(subnet, modelRow, 5);
            tableModel.setValueAt(device, modelRow, 6);
            tableModel.setValueAt(location, modelRow, 7);

            JOptionPane.showMessageDialog(this, "Entry diupdate.");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "No/VLAN harus angka!",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        for (IPEntry e : manager.getAll()) {
            tableModel.addRow(new Object[]{
                    e.getNo(), e.getIp(), e.getVlan(), e.getName(),
                    e.getDescription(), e.getSubnetMask(), e.getDeviceType(),
                    e.getLocation()
            });
        }
    }

    private void saveToCSV() {
        try (PrintWriter pw = new PrintWriter("ipdata.csv")) {
            IPEntry[] all = manager.getAll();
            for (IPEntry e : all) {
                pw.println(e.getNo() + ";" +
                        e.getIp() + ";" +
                        e.getVlan() + ";" +
                        escape(e.getName()) + ";" +
                        escape(e.getDescription()) + ";" +
                        escape(e.getSubnetMask()) + ";" +
                        escape(e.getDeviceType()) + ";" +
                        escape(e.getLocation()));
            }
            JOptionPane.showMessageDialog(this, "Data saved to ipdata.csv");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal save!", "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadFromCSV() {
        try (BufferedReader br = new BufferedReader(new FileReader("ipdata.csv"))) {
            tableModel.setRowCount(0);
            manager.clear();

            String line;
            while ((line = br.readLine()) != null) {
                String[] d = line.split(";", -1);
                if (d.length < 8) continue;

                IPEntry e = new IPEntry(
                        Integer.parseInt(d[0]),
                        d[1],
                        Integer.parseInt(d[2]),
                        unescape(d[3]),
                        unescape(d[4]),
                        unescape(d[5]),
                        unescape(d[6]),
                        unescape(d[7])
                );

                manager.addEntry(e);
                tableModel.addRow(new Object[]{
                        e.getNo(), e.getIp(), e.getVlan(), e.getName(),
                        e.getDescription(), e.getSubnetMask(),
                        e.getDeviceType(), e.getLocation()
                });
            }
            JOptionPane.showMessageDialog(this, "Data loaded.");
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Tidak bisa load file!",
                    "Load Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearInputs() {
        tfNo.setText("");
        tfIP.setText("");
        tfVLAN.setText("");
        tfName.setText("");
        tfDesc.setText("");
        tfSubnet.setText("");
        tfLocation.setText("");
        tfNo.requestFocus();
    }

    private String escape(String s) {
        if (s == null) return "";
        return s.replace("\n", "\\n").replace(";", "\\;");
    }

    private String unescape(String s) {
        if (s == null) return "";
        return s.replace("\\n", "\n").replace("\\;", ";");
    }
}
