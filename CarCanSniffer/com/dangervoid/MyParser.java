package com.dangervoid;

import java.util.ArrayList;

public class MyParser {

    public MyParser(){

    }

    public MyCanPacket parseToPacket(String complexData){
        String[] tmpArr = complexData.split(",");
        MyCanPacket newPacket = new MyCanPacket(tmpArr[0],Boolean.valueOf(tmpArr[1]),Integer.valueOf(tmpArr[2]),tmpArr[3]);
        return newPacket;
    }

    public ArrayList<String> parseSt(String complexData){
        ArrayList<String> res = new ArrayList<String>();
        String[] tmpArr = complexData.split(",");
        res.add(tmpArr[0]);
        res.add(tmpArr[1]);
        res.add(tmpArr[2]);
        res.add(tmpArr[3]);

        return res;
    }
}
