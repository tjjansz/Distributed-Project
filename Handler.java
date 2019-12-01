import java.io.*; 
import java.io.DataInputStream; 
import java.net.*;
import java.util.*;
import java.lang.Math;

class Handler extends Thread  
{ 
    final DataInputStream dis1, dis2; 
    final DataOutputStream dos1, dos2; 
    final Player player1; 
    final Player player2;
    String board[] = new String[9];
    static int turn;
    static int counter = 0;
    
    // Constructor assigns players and their respective input/outputs
    public Handler(Player client1, DataInputStream dis1, DataOutputStream dos1, Player client2,DataInputStream dis2, DataOutputStream dos2)  
    { 
        this.counter = 0;

        this.player1 = client1;
        this.dis1 = dis1;
        this.dos1 = dos1;

        this.player2 = client2;
        this.dis2 = dis2;
        this.dos2 = dos2;
    } 
  
    //Player1 goes on turn = 0, Player2 goes on turn=1
    @Override
    public void run()  
    {
        try { 
        InitializeBoard(board);
            if(SelectFirst()){
                turn = 0;
            }
            else{
                turn = 1;
            }
            while (counter < 9)  
            { 
                if(turn == 0){
                    try{
                        TakeTurn(dis1, dos1, turn);
                    }catch (EOFException e){
                        continue;
                    }
                    turn = 1;
                }else if(turn == 1){
                    try{
                        TakeTurn(dis2, dos2, turn);
                    }catch (EOFException e){
                        continue;
                    }
                    turn = 0;
                }
                counter++;
            }
            if (counter != 101){
                dos1.writeInt(113); //Print tie
                dos2.writeInt(113);
                dos1.writeUTF(GetBoardState(counter)); //Write board state
                dos2.writeUTF(GetBoardState(counter)); //Write board state
            }
            // closing resources 
            this.dis1.close(); 
            this.dos1.close();
            this.dis2.close(); 
            this.dos2.close();  
        }catch(IOException e){ 
            e.printStackTrace(); 
        }
    }

    public void InitializeBoard(String[] board){
        for(int i = 0; i < 9; i++){
            board[i] = " ";
        }
    }
    public void PrintBoard(int turnNumber){
        String oneLine = ("\n-------Turn " + turnNumber + "--------\n\n"+" " + board[0] + " " + "|" + " " + board[1] + " " + "|" + " " + board[2] + " \n" + "---" + "|" + "---" + "|" + "---\n" + " " + board[3] + " " + "|" + " " + board[4] + " " + "|" + " " + board[5] + " \n" + "---" + "|" + "---" + "|" + "---\n" + " " + board[6] + " " + "|" + " " + board[7] + " " + "|" + " " + board[8] + " \n");
        //System.out.println("\n-------Turn " + turnNumber + "--------\n");
        //System.out.println(" " + board[0] + " " + "|" + " " + board[1] + " " + "|" + " " + board[2] + " ");
        //System.out.println("---" + "|" + "---" + "|" + "---");
        //System.out.println(" " + board[3] + " " + "|" + " " + board[4] + " " + "|" + " " + board[5] + " ");
        //System.out.println("---" + "|" + "---" + "|" + "---");
        //System.out.println(" " + board[6] + " " + "|" + " " + board[7] + " " + "|" + " " + board[8] + " \n");
        System.out.println(oneLine);
    }

    public Boolean SelectFirst(){
        if(Math.random() < 0.5) {
            return false;
        }
        else {
            return true;
            }
    }
    //TakeTurn(dis1,dos2);
    public void TakeTurn(DataInputStream dis, DataOutputStream dos, int turn) throws EOFException, IOException{
        try{
            if(counter == 0){
                dos.writeInt(117);
            }
            else if(IsWin(board, Math.abs(turn - 1))){ // Check if other player has won
                dos.writeInt(114); // Write the loss
                dos.writeUTF(GetBoardState(counter)); // Write board state
                counter = 100; // Set counter high to break from loop
            }
            else{
                dos.writeInt(116); // Continue match
            }
            dos.writeUTF(GetBoardState(counter)); // Write board state
            dos.writeUTF("It's your turn! Select a space by inputting (0-8)! (Tiles are numbered left-to-right and top-to-bottom)"); // Write message
            int choice = dis.readInt(); //Receive input

            if (choice < 9 && choice >= 0){ // Check for valid input
                if(board[choice].equals(" ")){ // Check space hasn't been used
                    board[choice] = Integer.toString(turn);
                }
            }
            PrintBoard(counter); //Print the updated board on server
            
            dos.writeUTF(GetBoardState(counter)); // Write the updated board to the client
            if(IsWin(board, turn)){ // Check if the move won the game
                dos.writeInt(115); // Write the win
                dos.writeUTF(GetBoardState(counter)); // Write board state
            }
        
        }catch(IOException e){
            e.printStackTrace();
        }
        // catch (EOFException e){
        //     return;
        // }
        
    }
    public String GetBoardState(int turnNumber){ // Formats the board state to a single string for sending over the output stream
        String oneLine = ("\n-------Turn " + turnNumber + "--------\n\n"+" " + board[0] + " " + "|" + " " + board[1] + " " + "|" + " "
        + board[2] + " \n" + "---" + "|" + "---" + "|" + "---\n" + " " + board[3] + " " + "|" + " " + board[4] + " " + "|" + " " + board[5]
        + " \n" + "---" + "|" + "---" + "|" + "---\n" + " " + board[6] + " " + "|" + " " + board[7] + " " + "|" + " " + board[8] + " \n");
        return oneLine;
    }

    public Boolean IsWin(String[] board, int turn){ // Checks all possible win conditions
        // Left to Right
        if(board[0].equals(board[1]) && board[1].equals(board[2]) && board[2].equals(board[0]) && board[0].equals(Integer.toString(turn))){
            for(int i = 0; i < 3; i++){
                System.out.println(board[i]);
            }
            System.out.println("Turn: " + turn);
            System.out.println("Win");
            return true;
        }else if(board[3].equals(board[4]) && board[4].equals(board[5]) && board[3].equals(board[5]) && board[3].equals(Integer.toString(turn))){
            for(int i = 0; i < 3; i++){
                System.out.println(board[i]);
            }
            System.out.println("Turn: " + turn);
            System.out.println("Win");
            return true;
        } else if(board[6].equals(board[7]) && board[7].equals(board[8]) && board[8].equals(board[6]) && board[6].equals(Integer.toString(turn))){
            for(int i = 0; i < 3; i++){
                System.out.println(board[i]);
            }
            System.out.println("Turn: " + turn);
            System.out.println("Win");
            return true;
        // Top to Bottom
        } else if(board[0].equals(board[3]) && board[3].equals(board[6]) && board[6].equals(board[0]) && board[0].equals(Integer.toString(turn))){
            for(int i = 0; i < 3; i++){
                System.out.println(board[i]);
            }
            System.out.println("Turn: " + turn);
            System.out.println("Win");
            return true;
        } else if(board[1].equals(board[4]) && board[4].equals(board[7]) && board[7].equals(board[1]) && board[1].equals(Integer.toString(turn))){
            for(int i = 0; i < 3; i++){
                System.out.println(board[i]);
            }
            System.out.println("Turn: " + turn);
            System.out.println("Win");
            return true;
        } else if(board[2].equals(board[5]) && board[5].equals(board[8]) && board[8].equals(board[2]) && board[2].equals(Integer.toString(turn))){
            for(int i = 0; i < 3; i++){
                System.out.println(board[i]);
            }
            System.out.println("Turn: " + turn);
            System.out.println("Win");
            return true;
        // Diagonal Win Conditions
        } else if(board[0].equals(board[4]) && board[4].equals(board[8]) && board[8].equals(board[0]) && board[0].equals(Integer.toString(turn))){
            for(int i = 0; i < 3; i++){
                System.out.println(board[i]);
            }
            System.out.println("Turn: " + turn);
            System.out.println("Win");
            return true;
        } else if(board[2].equals(board[4]) && board[4].equals(board[6]) && board[6].equals(board[2]) && board[2].equals(Integer.toString(turn))){
            for(int i = 0; i < 3; i++){
                System.out.println(board[i]);
            }
            System.out.println("Turn: " + turn);
            System.out.println("Win");
            return true;
        }
        // Otherwise there is no win
        else{ 
            for(int i = 0; i < 3; i++){
                System.out.println(board[i]);
            }
            System.out.println("Turn: " + turn);
            System.out.println("No Win");
            return false;
            }
    }
} 
// Match codes
// 117 = Match found
// 116 = Continue Match
// 115 = You've won
// 114 = You've lost
// 113 = It's a tie
// [0][1][2]
// [3][4][5]
// [6][7][8]

// //Rows
// [0][1][2]

// [3][4][5]

// [6][7][8]

// //Columns
// [0][3][6]

// [1][4][7]

// [2][5][8]

// //Diagonals
// [0][4][8]

// [2][4][6]


