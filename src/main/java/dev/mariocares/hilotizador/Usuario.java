package dev.mariocares.hilotizador;

public class Usuario {
    public String nombre, avatar, nickname, descripcion;
    public Integer seguidores, tuits;

    public Usuario(String nombre, String avatar, String nickname, Integer seguidores, Integer tuits, String descripcion) {
        this.nombre = nombre;
        this.avatar = avatar;
        this.nickname = nickname;
        this.seguidores = seguidores;
        this.tuits = tuits;
        this.descripcion = descripcion;
    }
}
