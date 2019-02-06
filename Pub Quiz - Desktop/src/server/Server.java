package server;

import datebase.DbHelper;
import datebase.Team;
import datebase.Teambyscore;
import server.listeners.*;

import java.net.*;
import java.io.*;
import java.util.*;

public class Server implements IServer {

    private static IServer INSTANCE = null;

    private static Integer counter = 0;
    private static int portNumber;
    private boolean listening = true;
    private ServerSocket server;
    private Socket connection;

    //server clients
    Map<String, ServerThread> clients = new HashMap<>();

    private ArrayList<ClientConnectListener> clientConnectListeners = new ArrayList();
    private ArrayList<TeamListener> teamListeners = new ArrayList();
    private ArrayList<ReceiveAnswerListener> receiveAnswerListeners = new ArrayList();
    private ArrayList<ClientReconnectListener> clientReconnectListeners = new ArrayList();
    private ArrayList<QuestionRequestListener> questionRequestListeners = new ArrayList();
    private ArrayList<ClientDisconnectListener> clientDisconnectListeners = new ArrayList();
    private ArrayList<ReceiveCurrentPositionListener> receiveCurrentPositionListeners = new ArrayList();
    private ArrayList<ReceivePointsListener> receivePointsListeners = new ArrayList();
    private ArrayList<GameFinishListener> gameFinishListeners = new ArrayList();

    public static IServer getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new Server(8080);
        }
        return INSTANCE;
    }

    public Server(int port) {
        if (port == 0) {
            System.err.println("Usage: java server.Server <port number>");
            System.exit(1);
        }
        portNumber = port;
    }

    @Override
    public void start(){
        try {
            server = new ServerSocket(portNumber);
            while (listening) {
                try {
                    waitForConnection();
                }catch (EOFException e){
                    System.out.println(e);
                }
            }
        } catch (IOException e){
            System.err.println("Could not listen on port " + portNumber);
            System.exit(-1);
        }
    }

    @Override
    public void stop() {
        try {
            server.close();
        } catch (IOException e) {
            System.out.print("ERROR CLOSINGINGTON");
        }
    }

    @Override
    public void sendQuestion(int questionId, String question, int time) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\"");
        stringBuilder.append(question);
        stringBuilder.append("\"");
        question = stringBuilder.toString();
        sendToAll(ClientMessage.RECEIVEQUESTION, questionId + "," + question + "," + time);
    }

    @Override
    public void requestAnswers() {
        sendToAll(ClientMessage.ANWSERREQUEST, " ");
    }

    @Override
    //Sends current position on the quiz!
    public void sendRoundSummary(String id) {
        ArrayList<Team> teamScores = DbHelper.getAllTeams();
        Collections.sort(teamScores, new Teambyscore());
        for (int i = 0; i < teamScores.size(); i++){
            if(teamScores.get(i).getId().equals(id))
                sendToClient(teamScores.get(i).getId(),ClientMessage.SUMMARY,String.valueOf(i+1));
        }
    }


    @Override
    public void sendGameState(int state) {
        sendToAll(ClientMessage.GAMESTATE ,String.valueOf(state));
    }

    @Override
    public void sendGameStateToClient(String id, int state) {
        sendToClient(id,ClientMessage.GAMESTATE ,String.valueOf(state));
    }

    @Override
    public void addClientConnectListener(ClientConnectListener listener) {
        clientConnectListeners.add(listener);
    }

    @Override
    public void removeClientConnectListener(ClientConnectListener listener) {
        clientConnectListeners.remove(listener);
    }

    @Override
    public void addClientDisconnectListener(ClientDisconnectListener listener) {
        clientDisconnectListeners.add(listener);
    }

    @Override
    public void removeClientDisconnectListener(ClientDisconnectListener listener) {
        clientConnectListeners.remove(listener);
    }

    @Override
    public void addClientReconnectListener(ClientReconnectListener listener) {
        clientReconnectListeners.add(listener);
    }

    @Override
    public void removeClientReconnectListener(ClientReconnectListener listener) {
        clientConnectListeners.remove(listener);
    }

    @Override
    public void addTeamListener(TeamListener listener) {
        teamListeners.add(listener);
    }

    @Override
    public void removeTeamListener(TeamListener listener) {
        teamListeners.remove(listener);
    }

    @Override
    public void addReceiveAnswerListener(ReceiveAnswerListener listener) {
        receiveAnswerListeners.add(listener);
    }

    @Override
    public void removeReceiveAnwserListener(ReceiveAnswerListener listener) {
        receiveAnswerListeners.remove(listener);
    }

    @Override
    public void addRequestListener(QuestionRequestListener listener) {
        questionRequestListeners.add(listener);
    }

    @Override
    public void removeRequestListener(QuestionRequestListener listener) {
        questionRequestListeners.remove(listener);
    }

    @Override
    public void addGameFinishListener(GameFinishListener listener) {
        gameFinishListeners.add(listener);
    }

    @Override
    public void removeGameFinishListener(GameFinishListener listener) {
        gameFinishListeners.remove(listener);
    }

    @Override
    public void addReceiveCurrentPositionListener(ReceiveCurrentPositionListener listener) {
        receiveCurrentPositionListeners.add(listener);
    }

    @Override
    public void removeReceiveCurrentPositionListener(ReceiveCurrentPositionListener listener) {
        receiveCurrentPositionListeners.remove(listener);
    }

    @Override
    public void addReceivePointsListener(ReceivePointsListener listener) {
        receivePointsListeners.add(listener);
    }

    @Override
    public void removeReceivePointsListener(ReceivePointsListener listener) {
        receivePointsListeners.remove(listener);
    }


    //wait for connection, then display the connection information
    private void waitForConnection() throws IOException {
        System.out.println("Waiting for someone to connect...");
        connection = server.accept();
        ServerThread serverThread = new ServerThread(connection);
        serverThread.start();
        System.out.println("Now Connected to " + connection.getInetAddress().getHostName());
    }

    //Domt loop through server threads.. silly!
    public void sendToAll(int protocol, String message) {
        Iterator it = clients.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            try {
                ServerThread serverThread = (ServerThread) pair.getValue();
                serverThread.output.writeObject(protocol + "," + message);
                serverThread.output.flush();
                System.out.println("Protocol: " + protocol + " sent: " + message);
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void sendToClient(String id, int protocol, String message){
        Iterator it = clients.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if(id.equals(pair.getKey())){
                try{
                    ServerThread serverThread = (ServerThread) pair.getValue();
                    serverThread.output.writeObject(protocol + "," + message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean checkClientExist(String id){
        Boolean exist = false;
        Iterator it = clients.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            if(id.equals(pair.getKey())){
                exist = true;
            }
        }
        return exist;
    }

    private class ServerThread extends Thread {
        private final Socket connection;
        public ObjectOutputStream output;
        public ObjectInputStream input;

        public ServerThread(Socket socket) {
            this.connection = socket;
        }

        @Override
        public void run() {
            try {
                setupStreams();
                whilePlaying();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                closeServerThread();
                counter--;
                System.out.println("User disconnected. Total users = " + counter);
            }
        }

        //Get stream to send and receive data
        private void setupStreams() throws IOException {
            //send to client
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            //receive from the client
            input = new ObjectInputStream(connection.getInputStream());
            System.out.println("Streams are now ready!");
            counter++;
            System.out.println("Number of connections: " + counter);
        }

        //during the quiz
        private void whilePlaying() throws IOException {
            String inputLine;
            try {
                while ((inputLine = (String) input.readObject()) != null) {
                    int state = Integer.parseInt(inputLine.split(",")[0]);
                    String message = inputLine.split(",")[1];
                    switch (state) {
                        case ServerMessage.CLIENT_CONNECTING:
                            if(DbHelper.checkTeamExist(message)){
                                //reconnect need to update the clients value
                                clients.put(message, this);
                                for (ClientReconnectListener clientReconnectListener : clientReconnectListeners) {
                                    clientReconnectListener.onReconnect(message);
                                }
                            }else {
                                clients.put(message, this);
                                for (ClientConnectListener clientConnectListener : clientConnectListeners) {
                                    clientConnectListener.onConnected();
                                }
                                sendMessage(ClientMessage.CONNECTED, "Connected");

                            }
                            break;
                        case ServerMessage.ADDTEAM:
                            String id = inputLine.split(",")[2];
                            String teamName = message;
                            for (TeamListener teamListener : teamListeners) {
                                teamListener.onAddTeam(id, teamName);
                            }
                            break;
                        case ServerMessage.GETQUESTION:
                            for (QuestionRequestListener questionRequestListener : questionRequestListeners) {
                                questionRequestListener.onRequest();
                            }
                            break;
                        case ServerMessage.CHECKTEAM:
                            if(DbHelper.checkTeamExist(message)){
                                System.out.println("Team found");
                                sendMessage(ClientMessage.TEAMEXIST, DbHelper.getTeam(message).getName());
                            }else{
                                System.out.println("No team found for this phone");
                                sendMessage(ClientMessage.TEAMEXIST, "0");
                            }
                            break;
                        case ServerMessage.RECEIVEANSWER:
                            int questionID  = Integer.parseInt(inputLine.split(",")[2]);
                            String answer =  inputLine.split(",")[3];
                            for (ReceiveAnswerListener receiveAnswerListener : receiveAnswerListeners) {
                                receiveAnswerListener.onReceived(message,questionID,answer);
                            }
                            break;
                        case ServerMessage.RECEIVEPOSITION:
                            String teamid  = message;
                            for (ReceiveCurrentPositionListener receiveCurrentPositionListener : receiveCurrentPositionListeners) {
                                receiveCurrentPositionListener.onReceivedRequest(teamid);
                            }
                            break;
                        case ServerMessage.RECEIVEPOINTS:
                            for (ReceivePointsListener receivePointsListener : receivePointsListeners) {
                                receivePointsListener.onRecievePoints(message);
                            }
                            break;

                    }
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }catch (IOException  e){
                String id = getKeyByValue(clients, this);
                for (ClientDisconnectListener clientDisconnectListener : clientDisconnectListeners) {
                    clientDisconnectListener.onDisconnected(id);
                }
                clients.remove(id);
                System.out.println("Client disconnected!");
            }
        }

        private void closeServerThread() {
            System.out.println("Closing connections for server");
            try{
                output.close();
                input.close();
                connection.close();
            }catch (IOException e){
                System.err.println(e);
            }
        }

        private void sendMessage(int protocol, String message){
            try {
                output.writeObject(protocol + "," + message);
                output.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    //Out of place I think.
    private <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {

            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;

    }
}