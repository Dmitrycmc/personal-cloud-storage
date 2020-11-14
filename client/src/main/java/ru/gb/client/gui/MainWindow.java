package ru.gb.client.gui;

import ru.gb.client.Network;

import javax.swing.*;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.*;
import java.io.File;
import java.nio.file.Paths;

class MainWindow extends JFrame {
    private final AuthWindow authWindow;
    private final Network network;
    private JList<String> filesListBox;
    private JPanel buttonsPanel = new JPanel();
    private JButton downloadButton = new JButton("Download");
    private JButton renameButton = new JButton("Rename");
    private JButton deleteButton = new JButton("Delete");
    private JButton uploadButton = new JButton("Upload");

    private void notifyServerError(String message) {
        JOptionPane.showMessageDialog(null, "Server error", message, JOptionPane.ERROR_MESSAGE);
    }

    MainWindow(Network network, AuthWindow authWindow) throws HeadlessException {
        this.network = network;
        this.authWindow = authWindow;

        setBounds(300, 300, 400, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setLayout(new BorderLayout(3, 1));

        addFilesListBox();
        addButtonsPanel();

        addMenu();

        refreshListData();

        setVisible(true);
    }

    private void addMenu() {
        JMenuBar menuBar = new JMenuBar();
        JMenu menuLogout = new JMenu("Logout");
        MainWindow mainWindow = this;
        menuLogout.addMenuListener(new MenuListener() {
            @Override
            public void menuSelected(MenuEvent e) {
                int input = JOptionPane.showConfirmDialog(mainWindow,
                        "Do you really want to logout?", "Logout?",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (input == JOptionPane.YES_OPTION) {
                    try {
                        network.logout();
                        authWindow.setVisible(true);
                        mainWindow.setVisible(false);
                    } catch (Exception e1) {
                        notifyServerError("Unable to logout");
                    }
                }
            }
            @Override
            public void menuDeselected(MenuEvent e) {}
            @Override
            public void menuCanceled(MenuEvent e) {}
        });
        menuBar.add(menuLogout);
        add(menuBar, BorderLayout.NORTH);
    }

    private void addButtonsPanel() {
        buttonsPanel.setLayout(new GridLayout(2, 1));
        addControlsPanel();
        addUploadButton();
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    private void addFilesListBox() {
        filesListBox = new JList<>();
        filesListBox.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        filesListBox.setLayoutOrientation(JList.VERTICAL);
        filesListBox.setVisibleRowCount(-1);
        filesListBox.addListSelectionListener(e -> {
            setControlsEnabled(filesListBox.getSelectedIndex() != -1);
        });
        JScrollPane listScroller = new JScrollPane(filesListBox);
        listScroller.setPreferredSize(new Dimension(250, 80));
        add(listScroller, BorderLayout.CENTER);
    }

    private void addControlsPanel() {
        JPanel controllerPanel = new JPanel();
        controllerPanel.setLayout(new GridLayout(1, 3));

        setControlsEnabled(false);
        downloadButton.addActionListener(e -> {
            try {
                String serverPath = filesListBox.getSelectedValue();
                String fileName = Paths.get(serverPath).getFileName().toString();

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new File(fileName));
                fileChooser.setDialogTitle("Specify a file to save");

                if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    String clientPath = fileToSave.getAbsolutePath();

                    network.getFile(serverPath, clientPath);
                }
            } catch (Exception e1) {
                notifyServerError("Unable to download file");
                e1.printStackTrace();
            }
        });
        renameButton.addActionListener(e -> {
            try {
                String oldName = filesListBox.getSelectedValue();
                String newName = JOptionPane.showInputDialog(this, "Enter new name for file " + oldName, oldName);
                network.patchFile(oldName, newName);
                refreshListData();
            } catch (Exception e1) {
                notifyServerError("Unable to rename file");
                e1.printStackTrace();
            }
        });
        deleteButton.addActionListener(e -> {
            try {
                String serverPath = filesListBox.getSelectedValue();
                int input = JOptionPane.showConfirmDialog(this,
                        "Do you really want to delete " + serverPath + "?", "Delete file?",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (input == JOptionPane.YES_OPTION) {
                    network.deleteFile(serverPath);
                    refreshListData();
                }
            } catch (Exception e1) {
                notifyServerError("Unable to delete file");
                e1.printStackTrace();
            }
        });

        controllerPanel.add(downloadButton);
        controllerPanel.add(renameButton);
        controllerPanel.add(deleteButton);

        buttonsPanel.add(controllerPanel);
    }

    private void addUploadButton() {
        uploadButton = new JButton("Upload");
        uploadButton.addActionListener(e -> {
            final JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select a file to upload");
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                String path = selectedFile.getAbsolutePath();

                try {
                    network.postFile(path);
                    refreshListData();
                } catch (Exception e1) {
                    notifyServerError("Unable to upload file");
                    e1.printStackTrace();
                }
            }
        });
        buttonsPanel.add(uploadButton);
    }

    private void refreshListData() {
        String[] filesList = new String[0];
        try {
            filesList = network.getList();
        } catch (Exception e) {
            notifyServerError("Unable to get files list");
            e.printStackTrace();
        }
        filesListBox.setListData(filesList);
    }

    private void setControlsEnabled(boolean flag) {
        downloadButton.setEnabled(flag);
        renameButton.setEnabled(flag);
        deleteButton.setEnabled(flag);
    }
}
