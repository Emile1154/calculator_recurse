package com.javarush.task.task34.task3404;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* 
Рекурсия для мат. выражения
*/

public class Solution {
    public static void main(String[] args) {
        Solution solution = new Solution();
        solution.recurse("2+8*(9/4-1.5)^(1+1)", 0);
    }

    public void recurse(final String expression, int countOperation) {
        String string = expression.replace(" ","");
        char[] chars = string.trim().toCharArray();
        int lvl = 0;
        double result = 0;
        int count = string.length() - string.replace("(", "").length();
        String[] tmp = new String[count+1];
        for (int i = 0; i < count+1; i++) {
            tmp[i]="";
        }
        int oldCross = 0;
        if(count != 0 && !string.contains("!")){
            for (int i = 0; i < chars.length; i++) {
                if(i != 0 && chars[i-1] == '('){
                    int cross = tmp[0].length() - tmp[0].replace("()", "&").length();
                    if(oldCross != cross){
                        lvl += 2;
                        oldCross = cross;
                    }else{
                        lvl++;
                    }
                }
                if(chars[i] == ')'){
                    if(oldCross != 0){
                        lvl -= 2;
                    }else{
                        lvl--;
                    }
                }
                tmp[lvl] += chars[i];
            }

            String str = "";
            for (int i = 0; i < tmp.length; i++) {
                if(i == tmp.length-1){
                    str += tmp[i] ;
                }else{
                    str += tmp[i] + "!";
                }
            }
            recurse(str,countOperation);
        }
        if(string.contains("!")){
            ArrayList<String> temp = new ArrayList<>(Arrays.asList(string.split("!")));

            double[] doubles = calculate(temp.get(temp.size()-1));
            result = doubles[0];
            countOperation += (int)doubles[1];
            if(!temp.get(temp.size()-2).contains("()")){
                StringBuilder b = new StringBuilder(temp.get(0));
                b.replace(temp.get(0).lastIndexOf('('),temp.get(0).lastIndexOf('(')+2, String.valueOf(result));
                temp.set(0,b.toString());
            }else{
                temp.set(temp.size()-2, temp.get(temp.size()-2).replace("()",String.valueOf(result)));
            }

            temp.remove(temp.size()-1);
            String str = "";
            for (int i = 0; i < temp.size(); i++) {
                if(i == temp.size() -1){
                    str += temp.get(i);
                }else{
                    str += temp.get(i) + "!";
                }
            }
            recurse(str, countOperation);
        }
        if(!string.contains("!") && count == 0){

            double[] doubles = calculate(string);
            result = doubles[0];

            countOperation += (int)doubles[1];
            if(result % 1 == 0){
                System.out.println((int)result + " " + countOperation);
                return;
            }
            if(String.valueOf(result).substring(String.valueOf(result).indexOf(".")+1).length() > 2){
                System.out.println(String.format("%.2f",result).replace(",", ".") + " " + countOperation);
                return;
            }
            System.out.println(result + " " + countOperation);
        }
    }


    public double calculate_logic(ArrayList<String> lines){ //-2^-2
        int index = 0;
        double result = 0;
        for (int i = 0; i < lines.size(); i++) {
            if(lines.get(0).equals("-")){
                lines.remove(0);
                lines.set(0, "-"+lines.get(0));
                if(lines.size() == 1){
                    return Double.parseDouble(lines.get(0));
                }
            }
            if(i != 0 && lines.get(i-1).matches("\\D") && lines.get(i).matches("\\D")){
                lines.remove(i);
                lines.set(i, "-" + lines.get(i));
                if(lines.size() == 1){
                    return Double.parseDouble(lines.get(0));
                }
            }
        }


        if(lines.contains("^")){
            index = lines.indexOf("^");
            result = Math.pow(Double.parseDouble(lines.get(index-1)), Double.parseDouble(lines.get(index+1)));
        }
        else if(lines.contains("*")){
            index = lines.indexOf("*");
            result = Double.parseDouble(lines.get(index-1)) * Double.parseDouble(lines.get(index+1));
        }
        else if(lines.contains("/")){
            index = lines.indexOf("/");
            result = Double.parseDouble(lines.get(index-1)) / Double.parseDouble(lines.get(index+1));
        }
        else if(lines.contains("-")){
            index = lines.indexOf("-");
            result = Double.parseDouble(lines.get(index-1)) - Double.parseDouble(lines.get(index+1));
        }
        else if(lines.contains("+")){
            index = lines.indexOf("+");
            result = Double.parseDouble(lines.get(index-1)) + Double.parseDouble(lines.get(index+1));
        }

        lines.set(index+1,String.valueOf(result));
        for (int i = 0; i < 2; i++) {
            lines.remove(index-1);
        }

        if(lines.size() > 1){
            calculate_logic(lines);
        }
        return Double.parseDouble(lines.get(0));
    }
    public double function(String buff){
        double num = 0;
        num = Double.parseDouble(buff.replaceAll("[a-z]", ""));
        if(buff.startsWith("s")){ //sin
            return Math.sin(Math.toRadians(num));
        }
        if(buff.startsWith("c")){ //cos
            return Math.cos(Math.toRadians(num));
        }
        if(buff.startsWith("t")){ //tg
            return Math.tan(Math.toRadians(num));
        }
        return 0;
    }

    public double[] calculate(String buff){ //2*sin11+28
        try{
            double[] r = {Double.parseDouble(buff), 0};
            return r;
        }catch (Exception e){

        }
        double[] result = new double[2];
        String tmp = "";
        int counter = 0;
        char[] chars = buff.toCharArray();
        ArrayList<String> lines = new ArrayList<String>();
        if(buff.matches("(.*)[a-z](.*)")){
            boolean cut= false;
            String temp = "";
            ArrayList<String> function_list = new ArrayList<>();
            for (int i = 0; i < chars.length; i++) {
                if(chars[i] == 's' || chars[i] == 'c' || chars[i] == 't'){
                    cut = true;
                }
                if(cut){
                    temp += chars[i];
                }
                if(i <= chars.length - 2 && cut && (chars[i+1] == '+' || chars[i+1] == '-' || chars[i+1] == '/' || chars[i+1] == '*' || chars[i+1] == '^')){
                    cut = false;
                    function_list.add(temp);
                    temp = "";
                }
                if(cut && i == chars.length-1){
                    cut = false;
                    function_list.add(temp);
                    temp = "";
                }
            }
            for(String s : function_list){
                buff = buff.replace(s, String.format("%.3f",function(s)));
                counter++;
            }
            buff = buff.replaceAll(",",".");
            if(buff.contains("--")){
                buff = buff.replaceAll("--","");
                counter++;
            }
            //System.out.println(buff);
            try{
                result[0] = Double.parseDouble(buff);
                result[1] = counter;
                return result;
            }catch (Exception e){

            }
        }
        chars = buff.toCharArray();
        for (int i = 1; i < chars.length; i+=2) {
            if (Character.isDigit(chars[i - 1]) && !Character.isDigit(chars[i])) { // 2+
                if (chars[i] == '.') {
                    tmp += chars[i - 1];
                    tmp += chars[i];
                } else {
                    tmp += chars[i - 1];
                    lines.add(tmp);
                    tmp = "";
                    lines.add(Character.toString(chars[i]));
                }
            }
            if (Character.isDigit(chars[i - 1]) && Character.isDigit(chars[i])) { // 22
                tmp += chars[i - 1];
                tmp += chars[i];
            }
            if(!Character.isDigit(chars[i - 1]) && !Character.isDigit(chars[i])){ //++
                lines.add(tmp);
                tmp = "";
                lines.add(String.valueOf(chars[i - 1]));
                lines.add(String.valueOf(chars[i]));
            }

            if (!Character.isDigit(chars[i - 1]) && Character.isDigit(chars[i])) { //+2
                if (!tmp.isEmpty() && chars[i - 1] != '.') {
                    lines.add(tmp);
                    tmp = "";
                }

                if (chars[i - 1] == '.') {
                    tmp += chars[i - 1];
                    tmp += chars[i];
                } else {
                    lines.add(Character.toString(chars[i - 1]));
                    tmp = Character.toString(chars[i]);
                }
            }
            if (chars.length % 2 == 0) {
                if (!tmp.isEmpty() && i == chars.length - 1) {
                    lines.add(tmp);
                }
            } else {
                if (i == chars.length - 2) {
                    lines.add(tmp + chars[chars.length - 1]);
                }
            }
        }
        if(lines.size() > 2){
            for(String s:lines){
                if(s.matches("\\D")){
                    counter++;
                }
            }
        }

        result[0] = calculate_logic(lines);
        result[1] = counter;
        return result;
    }

    public Solution() {

    }
}
