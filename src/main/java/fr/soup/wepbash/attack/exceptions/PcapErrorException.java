package fr.soup.wepbash.attack.exceptions;

/**
 * Created by cyprien on 15/07/16.
 */
public class PcapErrorException extends Exception {
    int errcode;
    String error;

    public int getErrcode() {
        return errcode;
    }

    public String getError() {
        return error;
    }

    public PcapErrorException(){
        errcode=0;
        error="";
    }

    public PcapErrorException(int errcode, String error){
        this.errcode=errcode;
        this.error=error;
    }
}
