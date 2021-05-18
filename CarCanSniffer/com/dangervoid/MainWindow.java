package com.dangervoid;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Scanner;

public class MainWindow extends JFrame{
    MainWindow me = this;
    private boolean isConnected = false;
    private boolean isLogging = false;
    private boolean isAutoscroll = true;
    private boolean isGroupPackets = false;
    private boolean isFiltered = false;

    private String filteredStr = "";
    private SerialContr serialContr;

    JCheckBox chbAutoscroll;
    JCheckBox chGroupPackets;

    JTextField tfFilterId;
    JButton bFilter;
    JButton bClearFilter;


    private JTextField tfDevice;
    private JButton connectButton;
    private JPanel rootPanel;
    private JTable dataTable;
    private JButton startLogButton;
    private JButton bClearTable;
    private JButton stopLogButton;
    private JTextField tfSpeed;
    private JLabel lStatus;
    private JPanel mainPanel;
    private JLabel statusLog;
    private JPanel pOption;
    private JTabbedPane tabbedPane1;
    private JButton bOpenIdle;
    private JButton bOpenNewData;
    private JTable tableResult;
    private JPanel buttonPanel;
    private JButton bCompare;
    private JButton bClear;
    private JLabel lStatusComp;
    private JButton bSaveLog;
    private JButton bSaveIdle;

    CanWriterWindow canWriterWindow;

    ArrayList<String> logArray;
    ArrayList<String> idleArray;
    ArrayList<String> newDataArray;



    MyCanPacket selectedPacket = null;

    public MainWindow(){
        add(rootPanel);
        setTitle("CarCanSniffer");
        setSize(1000,560);
        ImageIcon img = new ImageIcon("./icon.png");
        setIconImage(img.getImage());

        setupTable();
        addOptionsPanel();
        regEvt();
    }

    private void addOptionsPanel(){
        pOption.setLayout(new BoxLayout(pOption,1));
        chbAutoscroll = new JCheckBox("Autoscroll");
        chbAutoscroll.setSelected(true);
        pOption.add(chbAutoscroll);

        chGroupPackets = new JCheckBox("Group packets");
        pOption.add(chGroupPackets);

        JPanel panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.X_AXIS));
        JLabel lFilterId = new JLabel("Filter ID:");
        tfFilterId = new JTextField();
        panel1.add(lFilterId);
        panel1.add(tfFilterId);
        panel1.setMinimumSize(new Dimension(200,40));
        panel1.setPreferredSize(new Dimension(200,40));
        panel1.setMaximumSize(new Dimension(200,40));
        pOption.add(panel1);
        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));
        bFilter = new JButton("Filter");
        bFilter.setPreferredSize(new Dimension(120,30));
        bFilter.setMinimumSize(new Dimension(120,30));
        bFilter.setMaximumSize(new Dimension(120,30));
        bClearFilter = new JButton("Clear Filter");
        bClearFilter.setPreferredSize(new Dimension(120,30));
        bClearFilter.setMinimumSize(new Dimension(120,30));
        bClearFilter.setMaximumSize(new Dimension(120,30));
        panel2.add(bFilter);
        panel2.add(bClearFilter);
        pOption.add(panel2);

        statusLog.setText("");
    }

    private void setupTable(){
        dataTable.setAutoscrolls(true);
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Time");
        model.addColumn("ID");
        model.addColumn("RET");
        model.addColumn("SIZE");
        model.addColumn("D0");
        model.addColumn("D1");
        model.addColumn("D2");
        model.addColumn("D3");
        model.addColumn("D4");
        model.addColumn("D5");
        model.addColumn("D6");
        model.addColumn("D7");
        dataTable.setModel(model);
        int dataColumnWidth = 10;
        DefaultTableColumnModel model1 = (DefaultTableColumnModel) dataTable.getColumnModel();
        model1.getColumn(0).setPreferredWidth(80);
        model1.getColumn(2).setPreferredWidth(50);
        model1.getColumn(3).setPreferredWidth(30);
        int i=4;
        while (i < model1.getColumnCount()){
            model1.getColumn(i).setPreferredWidth(dataColumnWidth);
            i++;
        }

        ListSelectionModel selModel = dataTable.getSelectionModel();
        selModel.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent evt) {
                selectedPacket = MyCanPacket.returnPacketFromComplexData(logArray.get(selModel.getAnchorSelectionIndex()));
            }
        });

    }

    private String getCurTime(int i){
        String curTime = "";
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
        if(i == 1){
            formatter = new SimpleDateFormat("YYMMddHHmmss");
        }
        Date date = new Date();
        curTime = formatter.format(date);
        return curTime;
    }

    public void addNewRow(MyCanPacket newPacket){

        DefaultTableModel curModel = (DefaultTableModel) dataTable.getModel();
        if (isGroupPackets) {
            int i = 0;
            String tmpid = "";
            while (i < curModel.getRowCount()) {
                tmpid = (String) curModel.getValueAt(i, 1);
                if (tmpid.equals(newPacket.getId())) {
                    curModel.removeRow(i);

                }
                i++;
            }
        }
        if (isFiltered){
            if(newPacket.getId().equals(filteredStr)){
                curModel.addRow(new Object[]{getCurTime(0), newPacket.getId(), newPacket.isRet(), newPacket.getSize(), newPacket.getHex(0), newPacket.getHex(1),
                        newPacket.getHex(2), newPacket.getHex(3), newPacket.getHex(4), newPacket.getHex(5), newPacket.getHex(6), newPacket.getHex(7)});

                dataTable.setModel(curModel);
            }
        }else {
            curModel.addRow(new Object[]{getCurTime(0), newPacket.getId(), newPacket.isRet(), newPacket.getSize(), newPacket.getHex(0), newPacket.getHex(1),
                    newPacket.getHex(2), newPacket.getHex(3), newPacket.getHex(4), newPacket.getHex(5), newPacket.getHex(6), newPacket.getHex(7)});

            dataTable.setModel(curModel);
        }

        if (isAutoscroll) {
            Rectangle r = dataTable.getCellRect(dataTable.getRowCount(), 0, true);
            if (r != null)
                dataTable.scrollRectToVisible(r);
        }
        if (isLogging) {
            System.out.println("aad" + newPacket.getLogData());
            logArray.add(getCurTime(0) + "," + newPacket.getLogData());
        }
    }

    private void startLog(){
        isLogging = true;
        logArray = new ArrayList<String>();
    }

    private void stopLog(){
        isLogging = false;
        statusLog.setText("We have " + logArray.size() + " records");
    }

    private String getIdFromComplexData(String comData){
        String[] splittedd = comData.split(",");
        return splittedd[1];
    }

    private ArrayList<String> compareTwoArrays(){
        ArrayList<String> res = new ArrayList<>();
        int i = 0;
        while (i < newDataArray.size()){
            String curId = getIdFromComplexData(newDataArray.get(i));
            int j = 0;
            int marker = 0;
            while (j < idleArray.size()){
                if(curId.equals(idleArray.get(j))){
                    marker++;
                }
                j++;
            }
            if(marker == 0){
                //checking is it already in our resArr
                j = 0;
                while (j < res.size()){
                    if(curId.equals(getIdFromComplexData(res.get(j)))){
                        marker++;
                    }
                    j++;
                }
                if(marker == 0) {
                    res.add(newDataArray.get(i));
                }
            }
            i++;
        }

        return res;
    }

    private void showResultTable(ArrayList<String> resArr){
        tableResult.setAutoscrolls(true);
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("Time");
        model.addColumn("ID");
        model.addColumn("RET");
        model.addColumn("SIZE");
        model.addColumn("D0");
        model.addColumn("D1");
        model.addColumn("D2");
        model.addColumn("D3");
        model.addColumn("D4");
        model.addColumn("D5");
        model.addColumn("D6");
        model.addColumn("D7");
        tableResult.setModel(model);
        int dataColumnWidth = 10;
        DefaultTableColumnModel model1 = (DefaultTableColumnModel) tableResult.getColumnModel();
        model1.getColumn(0).setPreferredWidth(80);
        model1.getColumn(2).setPreferredWidth(50);
        model1.getColumn(3).setPreferredWidth(30);
        int i=4;
        while (i < model1.getColumnCount()){
            model1.getColumn(i).setPreferredWidth(dataColumnWidth);
            i++;
        }
        i = 0;
        while (i < resArr.size()){
            System.out.println("az:" + resArr.get(i));
            String[] curItem = resArr.get(i).split(",");
            ArrayList<String> tempArr = MyCanPacket.decodeComplexData(curItem[curItem.length-1]);
            String[] newArr = new String[curItem.length + tempArr.size()];
            int j = 0;
            while (j < curItem.length-1){
                newArr[j] = curItem[j];
                j++;
            }
            j = 0;
            while (j < tempArr.size()){
                newArr[j + 4] = tempArr.get(j);
                j++;
            }
            model.addRow(newArr);
            i++;
        }
        tableResult.setModel(model);
    }

    private void saveLog() {
        String filename = "Log" + getCurTime(1) + ".txt";
        String filePath = "";
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save");
        fileChooser.setSelectedFile(new File(filename));
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            filePath = fileToSave.getAbsolutePath();
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(filePath, "UTF-8");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            int i = 0;
            while (i < logArray.size()) {
                writer.println(logArray.get(i));
                i++;
            }
            writer.close();
            System.out.println("Log saved: " + filePath);
        }
    }

    private void saveIdleList() {
        ArrayList<String> tmpArr = new ArrayList<>();
        int i = 0;
        while (i < logArray.size()) {
            tmpArr.add(MyCanPacket.getIdFromLogData(logArray.get(i)));
            i++;
        }

        String filename = "IdleList" + getCurTime(1) + ".txt";
        String filePath = "";
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Specify a file to save");
        fileChooser.setSelectedFile(new File(filename));
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            filePath = fileToSave.getAbsolutePath();
            PrintWriter writer = null;
            try {
                writer = new PrintWriter(filePath, "UTF-8");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            i = 0;
            while (i < logArray.size()) {
                writer.println(tmpArr.get(i) + ",");
                i++;
            }
            writer.close();
            System.out.println("Log saved: " + filePath);
        }



    }

    private void showCanWriterWindow(){
        if(canWriterWindow == null) {
            canWriterWindow = new CanWriterWindow(serialContr);
            canWriterWindow.setLocation(me.getLocation().x, me.getLocation().y + me.getHeight() + 5);
        }else {
            canWriterWindow.setVisible(true);
        }
    }

    private void hideCanWriterWindow(){
        canWriterWindow.setVisible(false);
    }

    private void regEvt(){
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String ac = actionEvent.getActionCommand();
                if(ac.equals("Connect")){

                    System.out.println("Action: Connect");
                    serialContr = new SerialContr(me, tfDevice.getText());
                    String tmpStatus = serialContr.initialize();
                    lStatus.setText(tmpStatus);
                    setupTable();
                    if(tmpStatus.equals("Connected")){
                        showCanWriterWindow();
                        connectButton.setText("Disconnect");
                        isConnected = true;
                        showCanWriterWindow();
                    }
                }else if(ac.equals("Disconnect")){
                    connectButton.setText("Connect");
                    lStatus.setText("Disconnected");
                    serialContr.close();
                    isConnected = false;
                    hideCanWriterWindow();
                }else if(ac.equals("Start Log")){
                    if(isConnected) {
                        System.out.println("Action: Start Log");
                        statusLog.setText("Log started: " + getCurTime(0));
                        startLog();
                    }else {
                        lStatus.setText("NOT CONNECTED");
                    }
                }else if(ac.equals("Stop Log")){
                    if(isConnected) {
                        System.out.println("Action: Stop Log");
                        statusLog.setText("Log saved");
                        stopLog();
                    }else {
                        lStatus.setText("NOT CONNECTED");
                    }
                }else if(ac.equals("Save Log")){
                    if(logArray != null) {
                        saveLog();
                    }else {
                        lStatus.setText("No records found yet");
                    }
                }
                else if(ac.equals("Save idle list")){
                    if(logArray != null) {
                        saveIdleList();
                    }else {
                        lStatus.setText("No records found yet");
                    }
                }else if(ac.equals("Open idle file")){
                    JFileChooser fc = new JFileChooser();
                    int returnVal = fc.showOpenDialog(me);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        try{
                            Scanner myReader = new Scanner(file);
                            ArrayList<String> loadedArr = new ArrayList<>();
                            while (myReader.hasNextLine()) {
                                String data = myReader.nextLine();
                                loadedArr.add(data);
                            }
                            idleArray = loadedArr;
                            loadedArr = null;
                            myReader.close();
                            lStatusComp.setText("Idle file loaded:" + idleArray.size()+ " records.");
                        } catch (FileNotFoundException e) {
                            lStatusComp.setText("Error when loading idle file.");
                            System.out.println("An error occurred.");
                            e.printStackTrace();
                        }
                    } else {
                        //canceled by user
                    }
                }else if(ac.equals("Open data file")){
                    JFileChooser fc = new JFileChooser();
                    int returnVal = fc.showOpenDialog( me);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File file = fc.getSelectedFile();
                        try{
                            Scanner myReader = new Scanner(file);
                            ArrayList<String> loadedArr = new ArrayList<>();
                            while (myReader.hasNextLine()) {
                                String data = myReader.nextLine();
                                loadedArr.add(data);
                            }
                            newDataArray = loadedArr;
                            loadedArr = null;
                            myReader.close();
                            lStatusComp.setText("New data file loaded. " + newDataArray.size());
                        } catch (FileNotFoundException e) {
                            lStatusComp.setText("Error when loading new data file.");
                            System.out.println("An error occurred.");
                            e.printStackTrace();
                        }
                    } else {
                        //canceled by user
                    }
                }else if(ac.equals("Compare")){
                    showResultTable(compareTwoArrays());
                }else if(ac.equals("Clear")){
                    tableResult.setModel(new DefaultTableModel());
                }else if(ac.equals("Filter")){
                    isFiltered = true;
                    filteredStr = tfFilterId.getText().toString();
                }else if(ac.equals("Clear Filter")){
                    isFiltered = false;
                }else if(ac.equals("Clear Table")){
                    //dataTable.setModel(new DefaultTableModel());
                    setupTable();
                }
            }
        };
        connectButton.addActionListener(al);
        startLogButton.addActionListener(al);
        stopLogButton.addActionListener(al);
        bSaveLog.addActionListener(al);
        bSaveIdle.addActionListener(al);
        bOpenIdle.addActionListener(al);
        bOpenNewData.addActionListener(al);
        bCompare.addActionListener(al);
        bClear.addActionListener(al);
        bFilter.addActionListener(al);
        bClearFilter.addActionListener(al);
        bClearTable.addActionListener(al);

        chbAutoscroll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(chbAutoscroll.isSelected()){
                    isAutoscroll = true;
                }else {
                    isAutoscroll = false;
                }
            }
        });

        chGroupPackets.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if(chGroupPackets.isSelected()){
                    isGroupPackets = true;
                }else {
                    isGroupPackets = false;
                }
            }
        });
    }


}
