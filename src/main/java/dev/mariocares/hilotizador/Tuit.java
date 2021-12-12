package dev.mariocares.hilotizador;

import java.io.Serializable;
import java.util.List;

public class Tuit implements Serializable {
    public String texto;
    public List<String> medias;
    
    public Tuit(String texto, List<String> medias){
        this.texto = texto;
        this.medias = medias;
    }
}
