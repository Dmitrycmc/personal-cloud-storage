package ru.gb.client.gui;

import ru.gb.client.Network;

import javax.swing.*;
import java.awt.*;
import java.io.File;

class MainWindow extends JFrame {
    private Network network;
    private JList<String> filesListBox;
    private JButton downloadButton = new JButton("Download");
    private JButton renameButton = new JButton("Rename");
    private JButton deleteButton = new JButton("Delete");
    private JButton uploadButton = new JButton("Upload");

    MainWindow(Network network) throws HeadlessException {
        this.network = network;
        setBounds(300, 300, 400, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setLayout(new GridLayout(3, 1));

        addFilesListBox();
        addControlsPanel();
        addUploadButton();

        refreshListData();

        setVisible(true);
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
        add(listScroller);
    }

    private void addControlsPanel() {
        JPanel controllerPanel = new JPanel();
        controllerPanel.setLayout(new GridLayout(1, 3));

        setControlsEnabled(false);
        downloadButton.addActionListener(e -> {
            try {
                String serverPath = filesListBox.getSelectedValue();
                String[] splittedPath = serverPath.split("/");
                String fileName = splittedPath[splittedPath.length - 1];

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setSelectedFile(new File(fileName));
                fileChooser.setDialogTitle("Specify a file to save");

                if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File fileToSave = fileChooser.getSelectedFile();
                    String clientPath = fileToSave.getAbsolutePath();

                    network.getFile(serverPath, clientPath);
                }
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(null, "Server error", "Unable to download file", JOptionPane.ERROR_MESSAGE);
                e1.printStackTrace();
            }
        });
        /*renameButton.addActionListener(e -> {
            try {
                network.patchFile("", "");
                refreshListData();
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(null, "Server error", "Unable to rename file", JOptionPane.ERROR_MESSAGE);
                e1.printStackTrace();
            }
        });*/
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
                JOptionPane.showMessageDialog(null, "Server error", "Unable to delete file", JOptionPane.ERROR_MESSAGE);
                e1.printStackTrace();
            }
        });

        controllerPanel.add(downloadButton);
        controllerPanel.add(renameButton);
        controllerPanel.add(deleteButton);

        add(controllerPanel);
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
                    JOptionPane.showMessageDialog(null, "Server error", "Unable to upload file", JOptionPane.ERROR_MESSAGE);
                    e1.printStackTrace();
                }
            }
        });
        add(uploadButton);
    }

    private void refreshListData() {
        String[] filesList = new String[0];
        try {
            filesList = network.getList();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Server error", "Unable to get files list", JOptionPane.ERROR_MESSAGE);
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
