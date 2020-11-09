package ru.gb.client.gui;

import ru.gb.client.Network;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

class MainWindow extends JFrame {
    private Network network;
    JList<String> filesListBox;

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

    MainWindow(Network network) throws HeadlessException {
        this.network = network;
        setBounds(300, 300, 400, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setLayout(new GridLayout(2, 1));

        filesListBox = new JList<>();
        filesListBox.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        filesListBox.setLayoutOrientation(JList.VERTICAL);
        filesListBox.setVisibleRowCount(-1);

        JScrollPane listScroller = new JScrollPane(filesListBox);
        listScroller.setPreferredSize(new Dimension(250, 80));

        refreshListData();
        add(listScroller);


        final JFileChooser fileChooser = new JFileChooser();

        JButton uploadButton = new JButton("Upload");
        uploadButton.addActionListener(e -> {
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


        setVisible(true);
    }
}
