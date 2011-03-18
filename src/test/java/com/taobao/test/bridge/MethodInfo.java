package com.taobao.test.bridge;

public class MethodInfo {
    Class classInfo;
    String name;
    Class[] paraTypes;
    Class returnType;
    boolean bridge;
    
    public String toString() {  
        StringBuilder info = new StringBuilder();   
        info.append(returnType.getSimpleName() + " " + classInfo.getName() + "." + name + "(");   
        for(Class para : paraTypes){  
            info.append(para.getSimpleName() + ",");  
        }  
        if(paraTypes.length > 0){  
            info.setCharAt(info.length()-1, ')');  
        }else{  
            info.append(')');  
        }  
          
        info.append("  bridge[" + bridge + "]");  
          
        return info.toString();  
    }
}
