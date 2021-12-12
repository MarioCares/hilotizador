package dev.mariocares.hilotizador.models;

import java.io.Serializable;
import java.util.List;

public class Tuit implements Serializable {
    public String texto;
    public List<String> medias;
    public List<Url> links;
    
    public Tuit(String texto, List<String> medias, List<Url> links){
        this.texto = texto;
        this.medias = medias;
        this.links = links;
    }
}
