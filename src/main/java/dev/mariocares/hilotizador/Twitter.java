package dev.mariocares.hilotizador;

import dev.mariocares.hilotizador.models.Tuit;
import dev.mariocares.hilotizador.models.Url;
import dev.mariocares.hilotizador.models.Usuario;
import io.github.redouane59.twitter.TwitterClient;
import io.github.redouane59.twitter.dto.tweet.Attachments;
import io.github.redouane59.twitter.dto.tweet.Tweet;
import io.github.redouane59.twitter.dto.tweet.entities.*;
import io.github.redouane59.twitter.dto.user.User;
import io.github.redouane59.twitter.signature.TwitterCredentials;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Twitter {
    private static final String api_key = "";
    private static final String api_key_secret = "";
    private static final String token_bearer = "%2B5RRGaPTYmdYV4Q%3Dgjkzp2Md67F1hj4qildwST3K95oGj2kTYgmxT0FKN48oVR5OBm";
    private static final String token = "-";
    private static final String token_secret = "";
    private static final String url = "https://twitter.com/";

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
                u.getDescription()
        );
    }

    public List<Tuit> hilo(){
        List<Tuit> tuits = new ArrayList<>();
        armarHilo(this.id).forEach(tweet -> {
            //attachments(tweet.getAttachments());
            //entities(tweet.getEntities());
            String texto = tweet.getText();
            texto = generarHash(texto, tweet.getEntities().getHashtags());
            texto = generarMencion(texto, tweet.getEntities().getUserMentions());
            texto = limpiarLinks(texto, tweet.getEntities().getUrls());
            tuits.add( new Tuit( texto, media(tweet.getMedia()), url(tweet.getEntities().getUrls()) ) );
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

    private List<String> media(List<? extends MediaEntity> medias){
        List<String> media = new ArrayList<>();
        if(medias != null){
            medias.forEach(tmp -> {
                if(tmp.getType().equals("photo")){
                    media.add(tmp.getMediaUrl());
                } else {
                    System.out.println(tmp.getType());
                    System.out.println(tmp.getMediaUrl());
                    System.out.println(tmp.getDisplayUrl());
                    System.out.println(tmp.getExpandedUrl());
                    System.out.println(tmp.getUrl());
                }
            });
        } else {
            //System.out.println("No tengo imagen");
        }
        return media;
    }

    private void entities(Entities entities){
        System.out.println("!------Entities---------");
        if (entities.getSymbols() != null) {
            System.out.println("-- symbols --");
            entities.getUrls().forEach(symbol -> {
                System.out.println(symbol.getDescription());
                System.out.println(symbol.getUnwoundedUrl());
                System.out.println(symbol.getTitle());
                System.out.println(symbol.getUrl());
                System.out.println(symbol.getExpandedUrl());
                System.out.println(symbol.getDisplayUrl());
            });
            System.out.println("-- symbols --");
        }
        System.out.println("----Entities----!");
    }

    private void attachments(Attachments attachments){
        if(attachments != null){
            for (String at: attachments.getMediaKeys()) {
                System.out.println(at);
            }
        }
    }

    private String generarHash(String texto, List<? extends HashtagEntity> hashs){
        if(hashs != null){
            for (HashtagEntity hash : hashs) {
                texto = texto.replace(
                    ("#" + hash.getText()),
                    "<a href=\"" + url + "search?q=%23" + hash.getText() + "\" target=\"_blank\">" +
                        "#" + hash.getText() + "</a>"
                );
            }
        }
        return texto;
    }

    private String generarMencion(String texto, List<? extends UserMentionEntity> menciones){
        if(menciones != null){
            for (UserMentionEntity mencion : menciones) {
                texto = texto.replace(
                    ("@" + mencion.getText()),
                    "<a href=\"" + url + mencion.getText() + "\" target=\"_blank\">" +
                        "@" + mencion.getText() + "</a>"
                );
            }
        }
        return texto;
    }

    private List<Url> url(List<? extends UrlEntity> links){
        List<Url> urls = new ArrayList<>();
        if(links != null){
            for (UrlEntity link : links) {
                // SI SOY PÁGINA ENTONCES TENGO TITULO
                if(link.getTitle() != null){
                    urls.add(new Url(link.getDescription(), link.getExpandedUrl(), link.getDisplayUrl(), link.getTitle()));
                }
            }
        }
        return urls;
    }

    private String limpiarLinks(String texto, List<? extends UrlEntity> links){
        if(links != null){
            for (UrlEntity link : links) {
                if(link.getStatus() == 200){
                    texto = texto.replace(
                        link.getUrl(),
                        "<a href=\"" + link.getExpandedUrl() + "\" target=\"_blank\">" + link.getDisplayUrl() + "</a>");
                } else {
                    texto = texto.replace(link.getUrl(), "");
                }
            }
        }
        return texto;
    }
}
