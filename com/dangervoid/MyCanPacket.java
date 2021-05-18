package com.dangervoid;

import java.util.ArrayList;

public class MyCanPacket {
    private String id;
    private boolean ret;
    private int size;
    private String data;

    public MyCanPacket(){

    };
    public MyCanPacket(String id, boolean ret, int size, String data){
        this.id = id;
        this.ret = ret;
        this.size = size;
        this.data = data;
    }
    public MyCanPacket(String complexData){
        String[] tmpArr = complexData.split(",");
        this.id = tmpArr[0];
        this.ret = Boolean.valueOf(tmpArr[1]);
        this.size = Integer.valueOf(tmpArr[2]);
        this.data = tmpArr[3];
    }

    public String getHex(int i){
        String res = "";
        try {
            if (i == 0) {
                res = data.substring(i, 2);
            } else if (i == 1) {
                res = data.substring(2, 4);
            } else if (i == 2) {
                res = data.substring(4, 6);
            } else if (i == 3) {
                res = data.substring(6, 8);
            } else if (i == 4) {
                res = data.substring(8, 10);
            } else if (i == 5) {
                res = data.substring(10, 12);
            } else if (i == 6) {
                res = data.substring(12, 14);
            } else if (i == 7) {
                res = data.substring(14, 16);
            }
        }catch (StringIndexOutOfBoundsException ex){
            //ex.printStackTrace();
        }

        return res;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isRet() {
        return ret;
    }

    public void setRet(boolean ret) {
        this.ret = ret;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getLogData(){
        String res = "";
        res = id + "," + ret + "," + size + "," + data;
        return res;
    }

    public static String getIdFromLogData(String data){
        String res = "";
        String[] tmpArr = data.split(",");
        res = tmpArr[1];
        tmpArr = null;
        return res;
    }

    public static ArrayList<String> decodeComplexData(String compData){
        ArrayList<String> res = new ArrayList<>();
        int i = 0;
        while (i < compData.length()){
            String tmpdata = compData.substring(i,i+2);
            res.add(tmpdata);
            i = i+2;
            System.out.println("A" + i + ":" + compData + ":" + tmpdata);
        }
        return res;
    }

    public static MyCanPacket returnPacketFromComplexData(String compData){

        String[] tmpArr = compData.split(",");
        MyCanPacket newPacket = new MyCanPacket(tmpArr[0],Boolean.valueOf(tmpArr[1]),Integer.valueOf(tmpArr[2]),tmpArr[3]);
        return newPacket;
    }
}
