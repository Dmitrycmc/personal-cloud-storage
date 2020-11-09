package ru.gb.client.gui;

import ru.gb.client.Network;

import javax.swing.*;
import java.awt.*;

class MainWindow extends JFrame {
    private Network network;

    private JScrollPane renderFilesList() {
        String[] filesList = new String[0];
        try {
            filesList = network.getList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        JList<String> list = new JList<>(filesList);
        list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(-1);

        JScrollPane listScroller = new JScrollPane(list);
        listScroller.setPreferredSize(new Dimension(250, 80));

        return listScroller;
    }

    MainWindow(Network network) throws HeadlessException {
        this.network = network;
        setBounds(300, 300, 400, 400);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);


        JScrollPane filesList = renderFilesList();
        add(filesList);

        setVisible(true);
    }
}
