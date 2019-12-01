import java.io.*; 
import java.io.DataInputStream; 
import java.net.*;
import java.util.*;

class Player {
    final String uName;
    final DataInputStream dis; 
    final DataOutputStream dos; 
    final Socket client;

    public Player(String uName, Socket client, DataInputStream dis, DataOutputStream dos)  
    { 
        this.uName = uName;
        this.client = client; 
        this.dis = dis; 
        this.dos = dos; 
    } 

    public DataOutputStream GetDOS(){
        return this.dos;
    }
    public DataInputStream GetDIS(){
        return this.dis;
    }
    public String getName(){
        return this.uName;
    }
}