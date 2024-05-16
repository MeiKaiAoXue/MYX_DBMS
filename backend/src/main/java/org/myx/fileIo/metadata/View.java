package org.myx.fileIo.metadata;

import java.io.Serializable;
import java.util.List;

public class View implements Serializable {
    private static final long serialVersionUID = 1L;
    private   String viewName=null;
    private String fromTable=null;
    private List<String> Columns;

    public View(String viewName,String fromTable,List<String> Columns){
        this.viewName=viewName;
        this.fromTable=fromTable;
        this.Columns=Columns;
    };

    public String getViewName(){
        return  viewName;
    };
    public String getFromTable(){
        return fromTable;
    };
    public List<String>  getColumns(){
        return Columns;
    }

}
