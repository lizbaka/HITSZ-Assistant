package com.stupidtree.hita.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.stupidtree.hita.hita.TextTools;
import com.stupidtree.hita.timetable.Curriculum;
import com.stupidtree.hita.timetable.CurriculumCreator;
import com.stupidtree.hita.timetable.Note;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

import static com.stupidtree.hita.HITAApplication.HContext;
import static com.stupidtree.hita.HITAApplication.ToastHander;

public class FileOperator {



    public static boolean verifyStoragePermissions(Activity activity) {
        int REQUEST_EXTERNAL_STORAGE = 1;
        String[] PERMISSIONS_STORAGE = {
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE"};
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static boolean saveCurriculumsToFile(ArrayList<Curriculum> ils, File path) {

        File file = new File(path.toString() + "/Curriculum.dat");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(ils);
            objOut.flush();
            objOut.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }


    public static ArrayList<Curriculum> loadCurriculumFromFile(File path) {

        ArrayList<Curriculum> il = null;
        File file = new File(path.toString() + "/Curriculum.dat");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            return null;
        }
        FileInputStream in;
        try {
            in = new FileInputStream(file);
            ObjectInputStream objIn = new ObjectInputStream(in);
            il = (ArrayList<Curriculum>) objIn.readObject();
            objIn.close();
            System.out.println("read object success!");
        } catch (IOException e) {
            System.out.println("read object failed");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return il;

    }


    public static boolean saveUserInfosToFile(HashMap<String, String> ils, File path) {

        File file = new File(path.toString() + "/UserInfo.dat");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(ils);
            objOut.flush();
            objOut.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }

    public static boolean saveByteImageToFile(String path, Bitmap bitmap) {
        BufferedOutputStream os;
        try {
            File file = new File(path);  //新建图片
            if (!file.getParentFile().exists()) {  //如果文件夹不存在，创建文件夹
                file.getParentFile().mkdirs();
            }
            if (file.exists()) file.delete();
            file.createNewFile();  //创建图片文件
            os = new BufferedOutputStream(new FileOutputStream(file, false));

            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);  //图片存成png格式。
            os.close();  //关闭流
        } catch (Exception e) {
            return false;
        }
        return true;
    }




//    public static CurriculumCreator loadCurriculumHelper(String curriculumCode, String name, int sY, int sM, int sD, List<Map<String,String>> data){
//        CurriculumCreator cl = new CurriculumCreator(curriculumCode, sY, sM, sD, name);
//        cl.setCurriculumText( new Gson().toJson(data));
//
//        return cl;
//    }
//
//    public static CurriculumCreator loadCurriculumHelperFromCurriculumText(Curriculum ci_bmob) {
//        CurriculumCreator cl = new CurriculumCreator(ci_bmob.getCurriculumCode(), ci_bmob.getStart_year(), ci_bmob.getStart_month(), ci_bmob.getStart_day(), ci_bmob.getName());
//        try {
//               for (Map<String, String> map : data) {
//                cl.CoursesGeneraor(Integer.parseInt(map.get("dow")), map.get("name"), map.get("teacher"), map.get("classroom"),Integer.parseInt(map.get("begin")), Integer.parseInt(map.get("last")), map.get("weeks").split(","));
//            }
//        } catch (JsonSyntaxException e) {
//            e.printStackTrace();
//        } catch (NumberFormatException e) {
//            e.printStackTrace();
//        }
//        return cl;
//    }


    public static boolean copyNotePhotosToFile(List<String> oldFile, File location, String curriculumName, String dateName, String number) {
        if (oldFile == null || oldFile.size() == 0) return false;

        File file = new File(location.toString() + "/notes/" + curriculumName + "/" + dateName + "/" + number + "/");
        if (!file.exists()) {
            if (file.getParentFile().exists()) {
                file.mkdir();
            } else {
                file.mkdirs();
            }
        }
        for (String x : oldFile) {
            // System.out.println("copy:"+x+"--->"+file.getPath()+x.substring(x.lastIndexOf("/")));
            if (!copyFile(x, file.getPath() + x.substring(x.lastIndexOf("/")))) return false;
        }
        return true;
    }

    public static boolean saveNoteToFile(ArrayList<Note> ltt, File path, String curriculumName, String dateName, String number) {
        if (ltt == null) return false;
        File file = new File(path.toString() + "/notes/" + curriculumName + "/" + dateName + "/" + number + "/notes.dat");
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (Exception e) {
                return false;
            }
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(ltt);
            objOut.flush();
            objOut.close();
        } catch (Exception e) {
            return false;
        }
        return true;

    }

    public static ArrayList<Note> loadNoteFromFile(File path, String curriculumName, String dateName, String number) {
        ArrayList<Note> ltt = new ArrayList<>();
        File file = new File(path.toString() + "/notes/" + curriculumName + "/" + dateName + "/" + number + "/notes.dat");
        if (!file.exists()) {
            return null;
        }
        FileInputStream in;
        try {
            in = new FileInputStream(file);
            ObjectInputStream objIn = new ObjectInputStream(in);
            ltt = (ArrayList<Note>) objIn.readObject();
            objIn.close();
        } catch (IOException e) {
            System.out.println("read object failed");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return ltt;
    }

    ///////////////////////////////以下为内部辅助函数////////////////////////////////////////////////
//    /*函数功能:把Excel文件转化成java通用类的形式
//     * 参数1：Excel文件名*/
//    public static Map<String, List<List<String>>> analyzeXls(String fileName) {
//        Map<String, List<List<String>>> map = new HashMap<>();
//        List<List<String>> rows;
//        List<String> columns = null;
//        try {
//            Workbook workbook = Workbook.getWorkbook(new File(fileName));
//            Sheet[] sheets = workbook.getSheets();
//            for (Sheet sheet : sheets) {
//                rows = new ArrayList<>();
//                String sheetName = sheet.getName();
//                for (int i = 0; i < sheet.getRows(); i++) {
//                    Cell[] sheetRow = sheet.getRow(i);
//                    int columnNum = sheet.getColumns();
//                    for (int j = 0; j < sheetRow.length; j++) {
//                        if (j % columnNum == 0) {  //按行存数据
//                            columns = new ArrayList<>();
//                        }
//                        columns.add(sheetRow[j].getContents());
//                    }
//                    rows.add(columns);
//                }
//                map.put(sheetName, rows);
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return map;
//    }

//    /*函数功能:从教务系统课表文字中分析出课程信息（写这个真滴累）
//     * 参数1：一格中的文字*/
//    public static List<Map<String, String>> analyseTableText(final String rawText) {
//        List<String> textSplit = new ArrayList<>();
//        final List<Map<String, String>> result = new ArrayList<>();
//        if (TextUtils.isEmpty(rawText)) return result;
//        int counting = 0;
//        counting++;
//        result.clear();
//        try {
//            String text = rawText.replaceAll("<br>", "</br>")
//                    .replaceAll("\n","")
//                    .replaceAll("◇","</br>")
//                    .replaceAll("<br>","</br>")
//                    .replaceAll("周]","]周")
//                    .replaceAll("双]", "]双")
//                    .replaceAll("单]", "]单")
//                    .replaceAll(",","，")
//                    ;
//            // System.out.println("block:\n"+text);
//            if (text.contains("&nbsp")) return result;
//            if (text.startsWith("[考试]")) {
//                String ExamsText;
//                if (text.contains("考试时间")) {
//                    int temp0 = text.indexOf("</br>", text.lastIndexOf("考试时间"));
//                    if (temp0 > 0) {
//                        ExamsText = text.substring(0, temp0);
//                        String CoursesText = text.substring(temp0 + 5);
//                        result.addAll(analyseTableText(CoursesText));
//                    } else {
//                        ExamsText = text;
//                    }
//                } else {
//                    int temp0 = text.indexOf("</br>", text.lastIndexOf("周 ，"));
//                    if (temp0 > 0) {
//                        ExamsText = text.substring(0, temp0);
//                        String CoursesText = text.substring(temp0 + 5);
//                        result.addAll(analyseTableText(CoursesText));
//                    } else {
//                        ExamsText = text;
//                    }
//                }
//
//
//                List<String> ExamTexts = new ArrayList<>();
//                int lastIndex = 1;
//                while (true) {
//                    int temp = ExamsText.indexOf("[考试]", lastIndex);
//                    if (temp > 0) {
//                        ExamTexts.add(ExamsText.substring(lastIndex - 1, temp));
//                        lastIndex = temp + 1;
//                    } else {
//                        ExamTexts.add(ExamsText.substring(lastIndex - 1));
//                        break;
//                    }
//
//                }
//                for (String x : ExamTexts) {
//                    //System.out.println("text:"+x);
//                    Map m = new HashMap();
//                    String name = x.substring(x.indexOf("[考试]") + 4, x.indexOf("</br>"));
//                    String week = x.substring(x.indexOf("[", 5) + 1, x.indexOf("]", 5));
//                    String time;
//                    String classroom;
//                    if (x.indexOf("，") > 0)
//                        time = x.substring(x.indexOf("考试时间") + 5, x.indexOf("，", x.indexOf("考试时间")));
//                    else time = x.substring(x.indexOf("考试时间") + 5);
//                    if (x.indexOf("<br/>", x.indexOf("考试时间")) > 0)
//                        classroom = x.substring(x.indexOf("]周", x.indexOf("考试时间")) + 2, x.indexOf("<br/>", x.indexOf("考试时间")));
//                    else classroom = "";
//
//                    //System.out.println("classroom:"+classroom);
//                    m.put("name", name);
//                    m.put("week", week);
//                    m.put("time", time);
//                    m.put("teacher", "null");
//                    m.put("classroom", classroom);
//                    result.add(m);
//
//                }
//
//
//            } else {
//                List<Integer> splitIndexes = new ArrayList<>();
//                List<Integer> splitIndexes_plus = new ArrayList<>();
//                int lastIndex = 0;
//                while (true) {
//                    int temp = text.indexOf("]周", lastIndex);
//                    if (temp > 0) {
//                        splitIndexes.add(temp);
//                        lastIndex = temp + 1;
//                    } else {
//                        lastIndex = 0;
//                        break;
//                    }
//                }
//                while (true) {
//                    int temp = text.indexOf("]单周", lastIndex);
//                    if (temp > 0) {
//                        splitIndexes.add(temp);
//                        lastIndex = temp + 1;
//                    } else {
//                        lastIndex = 0;
//                        break;
//                    }
//                }
//                while (true) {
//                    int temp = text.indexOf("]双周", lastIndex);
//                    if (temp > 0) {
//                        splitIndexes.add(temp);
//                        lastIndex = temp + 1;
//                    } else {
//                        break;
//                    }
//                }
//
//                List<Integer> integersToDelete = new ArrayList<>();
//                for (Integer x : splitIndexes) {
//                    if (x + 4 >= text.length()) {
//                    } else {
//                        String temp = text.substring(x, x + 4);
//                        if (temp.contains("，") || temp.contains(",")) {
//                            integersToDelete.add(x);
//                        }
//                    }
//                }
//                splitIndexes.removeAll(integersToDelete);
//                Collections.sort(splitIndexes);
//                System.out.println("text:" + text + ",splitIndexes:" + splitIndexes.toString());
//                for (int i = 0; i < splitIndexes.size() - 1; i++) {
//                    if (i == splitIndexes.size() - 1) break;
//                    if (text.substring(splitIndexes.get(i), splitIndexes.get(i) + 7 >= text.length() ? text.length() - 1 : splitIndexes.get(i) + 7).contains("</br>")) {
//                        boolean hasMultiWeek = false;//判断是否为在教室前加</br>这种情况（前面有多个周就加，没有就不加）
//                        if (integersToDelete.size() > 0 && integersToDelete.get(0) < splitIndexes.get(i))
//                            hasMultiWeek = true;
//                        List<Integer> fuckoff = new ArrayList<>();
//                        for (int x : integersToDelete) {
//                            if (x < splitIndexes.get(i)) fuckoff.add(x);
//                        }
//                        integersToDelete.removeAll(fuckoff);
//                        int offset = hasMultiWeek ? 3 : 0;//如果有</br>则跳过这个br，否则不调过
//                        int toAddIndexPlus = text.indexOf("</br>", splitIndexes.get(i) + offset);
//                        if (!splitIndexes_plus.contains(toAddIndexPlus))
//                            splitIndexes_plus.add(toAddIndexPlus);
//                    } else if (text.indexOf("</br>", splitIndexes.get(i)) > 0) {
//                        int toAddIndexPlus = text.indexOf("</br>", splitIndexes.get(i));
//                        if (!splitIndexes_plus.contains(toAddIndexPlus))
//                            splitIndexes_plus.add(toAddIndexPlus);
//                    }
//                }
//                System.out.println("text:" + text + ",splitIndexes_plus:" + splitIndexes_plus.toString());
//                int from = 0;
//                for (int i = 0; i < splitIndexes_plus.size() + 1; i++) {
//                    if (i > splitIndexes_plus.size() - 1) {
//                        textSplit.add(text.substring(from));
//                    } else {
//                        textSplit.add(text.substring(from, splitIndexes_plus.get(i)));
//                        from = splitIndexes_plus.get(i) + 5;
//                    }
//                }
//                for (String x : textSplit) {
//                    Map m = new HashMap();
//                    System.out.println("x:\n" + x);
//                    String name = x.substring(0, x.indexOf("</br>"));
//                    String teacher = x.substring(x.indexOf("</br>") + 5, x.indexOf("["));
//                    String classroom, timeText;
//                    int num1 = countStrNum(x, "]");
//                    int num2 = countStrNum(x, "周，");
//                    //  if(x.contains("，[")&&(!x.contains("周，"))){//教室变动的情况
//                    Log.e("教室变动处理：", "text=" + text + ",num1=" + num1 + ",num2=" + num2);
//                    if (num1 != num2 + 1) { //教室变动的情况
//                        int f = x.indexOf("[");
//                        classroom = x.substring(f);
//                        StringBuilder sb = new StringBuilder();
//                        for (String df : classroom.split("，\\[")) {
//                            String timeT = df.substring(0, df.lastIndexOf("]"));
//                            sb.append(timeT);
//                            sb.append("，");
//                        }
//                        timeText = sb.toString().substring(0, sb.length() - 1) + "]";
//                        //Log.e("!!!",timeText);
//                    } else {
//                        if (x.substring(x.lastIndexOf("周")).contains("</br>")) {
//                            classroom = x.substring(x.lastIndexOf("</br>") + 5);
//                        } else {
//                            classroom = x.substring(x.lastIndexOf("周") + 1);
//                        }
//                        if (x.lastIndexOf("]") + 3 < x.length()) {
//                            timeText = x.substring(x.indexOf("["), x.lastIndexOf("]") + 3);
//                        } else {
//                            timeText = x.substring(x.indexOf("["), x.lastIndexOf("]") + 1);
//                        }
//                    }
//
//
//                    String timeMap = "";
//
//                    List<Integer> weeks = analyseTimeText(timeText);
//                    for (int i = 0; i < weeks.size(); i++) {
//                        if (i != weeks.size() - 1) {
//                            timeMap = timeMap + weeks.get(i) + "-";
//                        } else {
//                            timeMap = timeMap + weeks.get(i);
//                        }
//
//                    }
//                    m.put("name", name);
//                    m.put("teacher", teacher);
//                    m.put("classroom", classroom);
//                    m.put("weeks", timeMap);
//
//                    result.add(m);
//                }
//            }
//        } catch (Exception e) {
//            Log.e("解析出错，等待服务器更正", "," );
//            e.printStackTrace();
//            BmobQuery<errorTableText> bq = new BmobQuery<>();
//            bq.addWhereEqualTo("tableText", rawText);
//            List<errorTableText> ls = null;
//            try {
//                ls = bq.findObjectsSync(errorTableText.class);
//            } catch (Exception el) {
//                el.printStackTrace();
//                Message msg = ToastHander.obtainMessage();
//                msg.what = 0;
//                Bundle b = new Bundle();
//                b.putString("msg","解析课表出现问题，请开启网络尝试使用云端修复！");
//                msg.setData(b);
//                ToastHander.sendMessage(msg);
//                return result;
//            }
//            if(ls!=null&&ls.size()>0){
//
//                Log.e("success", "已找到更正方案");
//                return analyseTableText(ls.get(0).correction);
//            }else{
//                new errorTableText(rawText, e).save(new SaveListener<String>() {
//                    @Override
//                    public void done(String s, BmobException e) {
//                        Toast.makeText(HContext, "解析课表出现问题，请等待云端修复方案", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                return result;
//            }
//
//        }
//        return result;
//
//    }




    private static boolean copyFile(String oldPath$Name, String newPath$Name) {
        try {
            File oldFile = new File(oldPath$Name);
            if (!oldFile.exists() || !oldFile.isFile() || !oldFile.canRead()) {
                return false;
            }
            FileInputStream fileInputStream = new FileInputStream(oldPath$Name);
            FileOutputStream fileOutputStream = new FileOutputStream(newPath$Name);
            byte[] buffer = new byte[1024];
            int byteRead;
            while (-1 != (byteRead = fileInputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            fileInputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }



}
