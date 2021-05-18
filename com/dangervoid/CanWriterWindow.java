package com.dangervoid;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CanWriterWindow extends JFrame{
    private JTable writerTable;
    private JPanel writerPanel;
    private JButton bSend;
    private JButton bclear;
    private JScrollPane tableScrollPane;
    private JTextField tfRepeat;
    private JCheckBox chbRet;

    boolean clearMarker = false;
    SerialContr serialContr;

    public CanWriterWindow(SerialContr serialContr){
        this.serialContr = serialContr;
        add(writerPanel);
        setTitle("CarCanSniffer");
        setSize(500,180);
        this.setVisible(true);
        setupTable();
        regEvt();
    }

    private void regEvt(){
        ActionListener al = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                String ac = actionEvent.getActionCommand();
                if(ac.equals("Send")){
                    String newId = (String) writerTable.getModel().getValueAt(0,0);
                    int newRetInt = -1;
                    if(chbRet.isSelected()){
                        newRetInt = 1;
                    }else {
                        newRetInt = 0;
                    }
                            String newData = (String)  writerTable.getModel().getValueAt(0,1) +
                            (String)  writerTable.getModel().getValueAt(0,2) +
                            (String)  writerTable.getModel().getValueAt(0,3) +
                            (String)  writerTable.getModel().getValueAt(0,4) +
                            (String)  writerTable.getModel().getValueAt(0,5) +
                            (String)  writerTable.getModel().getValueAt(0,6) +
                            (String)  writerTable.getModel().getValueAt(0,7) +
                            (String)  writerTable.getModel().getValueAt(0,8);
                    String newCompData = newId + "," +  newRetInt + "," + "8," + newData;
                    MyCanPacket tmpPacket = new MyCanPacket(newCompData);
                    System.out.println("ID:" + newId + " DATA:" + newData + " COMPDATA:" + newCompData);
                    int repeat = Integer.parseInt(tfRepeat.getText());
                    if(repeat > 0) {
                        newCompData = repeat + "," + newCompData;
                    }else {
                        newCompData = "1," + newCompData;
                        tfRepeat.setText("1");
                    }
                    serialContr.sendData(newCompData);
                }else if(ac.equals("Clear")){
                    clearMarker = true;
                    DefaultTableModel curModel = (DefaultTableModel) writerTable.getModel();
                    curModel.addRow(new Object[]{"0x00", "0", "0","0","0","0","0","0","0"});
                    curModel.removeRow(0);

                    writerTable.setModel(curModel);
                    clearMarker = false;

                }
            }
        };

        bSend.addActionListener(al);
        bclear.addActionListener(al);
    }

    private void setupTable(){
        writerTable.setAutoscrolls(true);
        Dimension dim = new Dimension(300, 20);
        tableScrollPane.setMinimumSize(dim);
        tableScrollPane.setSize(dim);
        tableScrollPane.setMaximumSize(dim);
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("D0");
        model.addColumn("D1");
        model.addColumn("D2");
        model.addColumn("D3");
        model.addColumn("D4");
        model.addColumn("D5");
        model.addColumn("D6");
        model.addColumn("D7");
        writerTable.setModel(model);
        DefaultTableModel curModel = (DefaultTableModel) writerTable.getModel();
        curModel.addRow(new Object[]{"0x232","3F","FD","20","03","12","0","0","0"});
        writerTable.setModel(curModel);
        curModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent tableModelEvent) {
                System.out.println("sss");
                int curCol = tableModelEvent.getColumn();
                int curRow = tableModelEvent.getLastRow();
                if(!clearMarker) {
                    writerTable.getColumnModel().getColumn(curCol).setCellRenderer(new ChangedCellRenderer());
                }else {
                    //clearMarker = false;
                }

            }
        });
    }
}
