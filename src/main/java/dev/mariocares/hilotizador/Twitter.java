package dev.mariocares.hilotizador;

import io.github.redouane59.twitter.TwitterClient;
import io.github.redouane59.twitter.dto.tweet.Attachments;
import io.github.redouane59.twitter.dto.tweet.Tweet;
import io.github.redouane59.twitter.dto.tweet.entities.MediaEntity;
import io.github.redouane59.twitter.dto.user.User;
import io.github.redouane59.twitter.signature.TwitterCredentials;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Twitter {
    private static final String api_key = "";
    private static final String api_key_secret = "";
    private static final String token_bearer = "";
    private static final String token = "";
    private static final String token_secret = "";

    private final String id;

    private final TwitterClient client;

    public Twitter(String id){
        this.id = id;
        client = new TwitterClient(TwitterCredentials.builder()
                .accessToken(token)
                .accessTokenSecret(token_secret)
                .apiKey(api_key)
                .apiSecretKey(api_key_secret)
                .build());
    }

    public Usuario usuario(){
        User u = client.getTweet(this.id).getUser();
        return new Usuario(
                u.getDisplayedName(),
                u.getProfileImageUrl().replace("normal", "bigger"),
                u.getName(),
                u.getFollowersCount(),
                u.getTweetCount(),
                buscarLinks(buscarUsuarios(buscarHash(u.getDescription())))
        );
    }

    public List<Tuit> hilo(){
        List<Tuit> tuits = new ArrayList<>();
        armarHilo(this.id).forEach(tweet -> {
            tuits.add(
                    new Tuit(
                            buscarLinks(
                                    buscarHash(
                                            buscarUsuarios(
                                                    limpiarTuit(tweet.getText(), (tweet.getMedia()) != null)
                                            )
                                    )
                            ),
                            media(tweet.getMedia())
                    )
            );
        });
        return tuits;
    }

    public List<Tweet> armarHilo(String id){
        List<Tweet> tuits = new ArrayList<>();
        // ENTONCES, BUSCO EL ÚLTIMO TUIT DEL HILO
        do {
            Tweet tuit = client.getTweet(id);
            tuits.add(tuit);
            id = tuit.getInReplyToStatusId();
        } while (id != null);
        Collections.reverse(tuits);
        return tuits;
    }

    private String limpiarTuit(String texto, boolean media){
        /* Esto es medio rancio... jaja. Twitter siempre añade links de media o páginas al final del tuit.
        Si existe un link y una imagen, la imagen siempre siempre queda al final.
        Si existe un link en "medio" de un tuit, hay que revisarlo distinto
        Además, al menos hasta ahora... siempre son 23 caractéres... así que se puede "cortar" o contar desde atrás.
        */
        if(media){
            // ENTONCES ELIMINO EL ULTIMO LINK (23 caracteres)
            return texto.substring(0, (texto.length() - 23));
        }
        return texto;
    }

    private List<String> media(List<? extends MediaEntity> medias){
        List<String> media = new ArrayList<>();
        if(medias != null){
            medias.forEach(tmp -> {
                if(tmp.getType().equals("photo")){
                    media.add(tmp.getMediaUrl());
                }
            });
        } else {
            //System.out.println("No tengo imagen");
        }
        return media;
    }

    private void attachments(Attachments attachments){
        if(attachments != null){
            for (String at: attachments.getMediaKeys()) {
                System.out.println(at);
            }
        }
    }

    private String buscarUsuarios(String texto){
        List<String> salida = new ArrayList<>();
        if(texto.contains("@")){
            String[] split = texto.split(" ");
            for (String parte : split) {
                if(parte.startsWith("@")){
                    salida.add("<a href=\"https://twitter.com/" +
                            parte.replace("@", "") +
                            "\" target=\"_blank\">" + parte + "</a>");
                } else {
                    salida.add(parte);
                }
            }
            return String.join(" ", salida);
        } else {
            return texto;
        }
    }

    private String buscarHash(String texto){
        List<String> salida = new ArrayList<>();
        if(texto.contains("#")){
            String[] split = texto.split(" ");
            for (String parte : split) {
                if(parte.startsWith("#")){
                    salida.add("<a href=\"https://twitter.com/search?q=%23" +
                            parte.replace("#", "") +
                            "\" target=\"_blank\">" + parte + "</a>");
                } else {
                    salida.add(parte);
                }
            }
            return String.join(" ", salida);
        } else {
            return texto;
        }
    }

    private String buscarLinks(String texto){
        List<String> salida = new ArrayList<>();
        if(texto.contains("https://t.co/")){
            String[] split = texto.split(" ");
            for (String parte : split) {
                if(parte.startsWith("https://t.co/")){
                    salida.add("<a href=\"https://t.co/" +
                            parte.replace("https://t.co/", "") +
                            "\" target=\"_blank\">" + parte + "</a>");
                } else {
                    salida.add(parte);
                }
            }
            return String.join(" ", salida);
        } else {
            return texto;
        }
    }
}
