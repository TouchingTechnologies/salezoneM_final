package dbstuff;

import com.orm.SugarRecord;

/**
 * Created by MOBICASH on 02-Jul-15.
 */
public class Codesdb extends SugarRecord<Codesdb> {
    public Codesdb(){}

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getId_post() {
        return id_post;
    }

    public void setId_post(String id_post) {
        this.id_post = id_post;
    }

    String code;
    String enddate;
    String id_post;

    public Codesdb(String code,String enddate,String id_post){
        this.code = code;
        this.enddate = enddate;
        this.id_post =id_post;
    }
}
