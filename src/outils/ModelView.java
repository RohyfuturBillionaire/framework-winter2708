package outils;

import java.util.HashMap;

public class ModelView {
    String url;
    HashMap <String , Object> data;
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public HashMap<String, Object> getData() {
        return data;
    }
    public void setData(HashMap<String, Object> data) {
        this.data = data;
    }

   
    public void add(String nomData,Object data){

        
        HashMap map = this.getData();
        map.put(nomData,data);
        
    } 
}
