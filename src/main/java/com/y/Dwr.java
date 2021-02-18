package com.y;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Dwr {

    private List<String> strings;
    private String string;
    private JSONObject datas;
    private String firstKey;
    private String refeArrayKey;

    public Dwr(String string) {
        this.string = string;
        this.strings = Arrays.asList(string.split(";"));
        this.datas = parseDatas(this.string);
    }

    private JSONObject parseDatas(String line){
        String newLine = line.replace(";",";\n");
        String objKey = "var s[0-9]+=\\{\\};";
        String arrayKey = "var s[0-9]+=\\[\\];";

        Function<String,String> collectFunc = s->StringUtils.substringBetween(s,"var","=");

        List<String> objKeys = collect(newLine,objKey,collectFunc);

        if(!objKeys.isEmpty()){
            this.firstKey = objKeys.get(0);
        }
        List<String> arrayKeys = collect(newLine,arrayKey,collectFunc);

        if(!arrayKey.isEmpty()){
            this.refeArrayKey = arrayKeys.get(0);
        }

        JSONObject jsonObject = new JSONObject();

        objKeys.forEach(key->{
            String patternKek = String.format("%s\\.[^0-9]+=.+;",key);
            List<String> collect = collect(newLine, patternKek, Function.identity());
            Map<String, String> map = collect.stream().collect(Collectors.toMap(s -> StringUtils.substringBetween(s, key+".", "="),
                    s -> StringUtils.substringBetween(s, "=", ";"),(v1,v2)->v2));
            jsonObject.put(key,map);
        });

        arrayKeys.forEach(key->{
            String patternKek = String.format("%s\\[[0-9]+\\]=s[0-9]+;",key);
            List<String> collect1 = collect(newLine, patternKek, s -> StringUtils.substringBetween(s, "=", ";"));

            String staticKek = String.format("%s\\[[0-9]+\\]=\"[\\w]+\";",key);

            List<String> collect2 = collect(newLine, staticKek, s -> StringUtils.substringBetween(s, "=", ";"));

            if(!collect1.isEmpty()){
                jsonObject.put(key,collect1);
            }else {
                jsonObject.put(key,collect2);
            }
        });

        return jsonObject;
    }

    private List<String> collect(String line,String patternKey, Function<String,String> collectFunc){

        // 创建 Pattern 对象
        Pattern r = Pattern.compile(patternKey);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(line);

        List<String> keys = new ArrayList<>();
        while (m.find()){
            String key = collectFunc.apply(m.group());
            if(key!=null){
                keys.add(key.trim().replaceAll("\"",""));
            }
        }
        return keys;
    }

    public Dwr() {
    }

    public List<String> getStrings() {
        return strings;
    }

    public void setStrings(List<String> strings) {
        this.strings = strings;
    }

    public String getString() {
        return string;
    }

    public void setString(String string) {
        this.string = string;
    }


    public JSONObject getDatas() {
        return datas;
    }

    public void setDatas(JSONObject datas) {
        this.datas = datas;
    }

    public String getFirstKey() {
        return firstKey;
    }

    public void setFirstKey(String firstKey) {
        this.firstKey = firstKey;
    }

    public String getRefeArrayKey() {
        return refeArrayKey;
    }

    public void setRefeArrayKey(String refeArrayKey) {
        this.refeArrayKey = refeArrayKey;
    }
}
