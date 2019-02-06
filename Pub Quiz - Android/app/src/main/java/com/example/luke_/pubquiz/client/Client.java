package com.example.luke_.pubquiz.client;

import com.example.luke_.pubquiz.ClientMessage;
import com.example.luke_.pubquiz.Question;
import com.example.luke_.pubquiz.ServerMessage;
import com.example.luke_.pubquiz.Team;
import com.example.luke_.pubquiz.client.Listener.ConnectionListener;
import com.example.luke_.pubquiz.client.Listener.ConnectionTimeOutListener;
import com.example.luke_.pubquiz.client.Listener.DisconnectListener;
import com.example.luke_.pubquiz.client.Listener.GameStateListener;
import com.example.luke_.pubquiz.client.Listener.QuestionListener;
import com.example.luke_.pubquiz.client.Listener.ReceiveCurrentPointsListener;
import com.example.luke_.pubquiz.client.Listener.ReconnectListener;
import com.example.luke_.pubquiz.client.Listener.RequestAnswerListener;
import com.example.luke_.pubquiz.client.Listener.SummaryListener;
import com.example.luke_.pubquiz.client.Listener.TeamListener;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class Client implements IClient {

    private String hostName;
    private int portNumber;
    private Socket connection;
    private ObjectOutputStream output;
    private ObjectInputStream input;

    private static IClient INSTANCE = null;

    //Listeners
    private List<ConnectionListener> connectionListeners = new CopyOnWriteArrayList<>();
    private List<QuestionListener> questionListeners = new CopyOnWriteArrayList<>();
    private List<RequestAnswerListener> requestAnswerListeners = new CopyOnWriteArrayList<>();
    private List<ReconnectListener> reconnectListeners = new CopyOnWriteArrayList<>();
    private List<TeamListener> teamListeners = new CopyOnWriteArrayList<>();
    private List<GameStateListener> gameStateListeners = new CopyOnWriteArrayList<>();
    private List<SummaryListener> summaryListeners = new CopyOnWriteArrayList<>();
    private List<ConnectionTimeOutListener> connectionTimeOutListeners = new CopyOnWriteArrayList<>();
    private List<DisconnectListener> disconnectListeners = new CopyOnWriteArrayList<>();
    private List<ReceiveCurrentPointsListener> receiveCurrentPointsListeners = new CopyOnWriteArrayList<>();

    public static IClient getInstance() {
        if(INSTANCE == null) {
            INSTANCE = new Client();
        }
        return INSTANCE;
    }

    @Override
    public void connect(String host, int port) {
        this.hostName = host;
        this.portNumber = port;
        run();
    }

    @Override
    public void disconnect() {
        closeClient();
    }

    @Override
    public void submitTeamName(String id, String name) {
        sendMessage(ServerMessage.ADDTEAM,name + "," + id);
    }

    @Override
    public void getCurrentPosition(String id) {
        sendMessage(ServerMessage.RECEIVEPOSITION, id);
    }

    @Override
    public void getCurrentPoints(String id) {
        sendMessage(ServerMessage.RECEIVEPOINTS, id);
    }

    @Override
    public void submitAnswer(String id,  int questionID, String answer) {
        sendMessage(ServerMessage.RECEIVEANSWER, id + "," + questionID + "," + answer);
    }

    @Override
    public void addConnectionListener(ConnectionListener listener) {
        connectionListeners.add(listener);
    }

    @Override
    public void removeConnectionListener(ConnectionListener listener) {
        connectionListeners.remove(listener);
    }

    @Override
    public void addGameStateListener(GameStateListener listener) {
        gameStateListeners.add(listener);
    }

    @Override
    public void removeGameStateListener(GameStateListener listener) {
        gameStateListeners.remove(listener);
    }

    @Override
    public void addQuestionListener(QuestionListener listener) {
        questionListeners.add(listener);
    }

    @Override
    public void removeQuestionListener(QuestionListener listener) {
        questionListeners.remove(listener);
    }

    @Override
    public void addRequestAnswerListener(RequestAnswerListener listener) {
        requestAnswerListeners.add(listener);
    }

    @Override
    public void removeRequestAnswerListener(RequestAnswerListener listener) {
        requestAnswerListeners.remove(listener);
    }

    @Override
    public void addReconnectListener(ReconnectListener listener) {
        reconnectListeners.add(listener);
    }

    @Override
    public void removeReconnectListener(ReconnectListener listener) {
        reconnectListeners.remove(listener);
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
    public void addSummaryListener(SummaryListener listener) {
        summaryListeners.add(listener);
    }

    @Override
    public void removeSummaryListener(SummaryListener listener) {
        summaryListeners.remove(listener);
    }

    @Override
    public void addReceiveCurrentPointsListener(ReceiveCurrentPointsListener listener) {
        receiveCurrentPointsListeners.add(listener);
    }

    @Override
    public void removeReceiveCurrentPointsListener(ReceiveCurrentPointsListener listener) {
        receiveCurrentPointsListeners.remove(listener);
    }

    @Override
    public void addConnectionTimeOutListener(ConnectionTimeOutListener listener) {
        connectionTimeOutListeners.add(listener);
    }

    @Override
    public void removeConnectionTimeOutListener(ConnectionTimeOutListener listener) {
        connectionListeners.remove(listener);
    }

    @Override
    public void addDisconnectListener(DisconnectListener listener) {
        disconnectListeners.add(listener);
    }

    @Override
    public void removeDisconnectListener(DisconnectListener listener) {
        disconnectListeners.remove(listener);
    }

    @Override
    public void checkTeamExist(String id) {
        sendMessage(ServerMessage.CHECKTEAM, id);
    }

    @Override
    public void requestQuestion() {
        sendMessage(ServerMessage.GETQUESTION," ");
    }


    private void run(){
        try{
            connectToServer();
            setupStreams();
            whilePlaying();
        }catch (EOFException e){
            System.out.println(e);
        }catch (IOException e){
            System.out.println(e);
        }finally {
            closeClient();
        }
    }


    private void connectToServer() throws IOException {
        while(true) {
            try {
                connection = new Socket();
                connection.connect(new InetSocketAddress(hostName,portNumber),10000);
                break;
            } catch(SocketTimeoutException e) {
                for(ConnectionTimeOutListener connectionTimeOutListener : connectionTimeOutListeners){
                    connectionTimeOutListener.onConnectionTimeout();
                }
            }catch (IOException exception){

            }
        }
    }

    private void setupStreams() throws IOException {
        output = new ObjectOutputStream(connection.getOutputStream());
        output.flush();
        input = new ObjectInputStream(connection.getInputStream());
        sendMessage(ServerMessage.CLIENT_CONNECTING, Team.getInstance().getId());
    }

    private void whilePlaying() throws IOException {
        String inputLine;
        try {
            while ((inputLine = (String) input.readObject()) != null) {
                //Split the message into sections ignoreing commas around quotes.
                String[] tokens = inputLine.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                int state = Integer.parseInt(tokens[0]);
                switch (state) {
                    case ClientMessage.CONNECTED:
                        for (ConnectionListener connectionListener : connectionListeners) {
                            connectionListener.onConnected();
                        }
                        break;
                    case ClientMessage.RECONNECTED:
                        String gameState1 = tokens[1];
                        for (ReconnectListener reconnectListener : reconnectListeners) {
                            reconnectListener.onReconnect(Integer.valueOf(gameState1));
                        }
                        break;
                    case ClientMessage.RECEIVEQUESTION:
                        int questionID = Integer.parseInt(tokens[1]);
                        String question = tokens[2];
                        question = question.replace("\"", "");
                        int time = Integer.valueOf(tokens[3]);
                        Question newQuestion = new Question(questionID, question,time);
                        for (QuestionListener questionListener : questionListeners) {
                            questionListener.onNewQuestion(newQuestion);
                        }
                        break;
                    case ClientMessage.ANWSERREQUEST:
                        for (RequestAnswerListener requestAnswerListener : requestAnswerListeners) {
                            requestAnswerListener.onAnswerRequest();
                        }
                        break;
                    case ClientMessage.TEAMEXIST:
                        String name = tokens[1];
                        if(!name.equals("0")){
                            for (TeamListener teamListener : teamListeners) {
                                teamListener.teamExist(name);
                            }
                        }
                        break;
                    case ClientMessage.GAMESTATE:
                        String gameState = tokens[1];
                        for (GameStateListener gameStateListener : gameStateListeners) {
                            gameStateListener.onStateChange(Integer.valueOf(gameState));
                        }
                        break;
                    case ClientMessage.SUMMARY:
                        String position = tokens[1];
                        for(SummaryListener summaryListener : summaryListeners){
                            summaryListener.onSummary(Integer.valueOf(position));
                        }
                        break;
                    case ClientMessage.RECIEVEPOINTS:
                        String points = tokens[1];
                        System.out.println(">>>>>" + points);
                        for(ReceiveCurrentPointsListener receiveCurrentPointsListener : receiveCurrentPointsListeners){
                            receiveCurrentPointsListener.onReceivePoints(Integer.valueOf(points));
                        }
                        break;
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch (IOException  e){
            for(DisconnectListener disconnectListener: disconnectListeners){
                disconnectListener.onDisconnect();
            }
        }
    }

    private void closeClient() {
        try{
            if(output != null)
                output.close();
            if(input != null)
                input.close();
            if(connection != null)
                connection.close();
        }catch (IOException e){
            System.err.println(e);
        }
    }

    public void sendMessage(int protocol, String message){
        try{
            output.writeObject(protocol + "," + message);
            output.flush();
            System.out.println("Protocol: " + protocol + " sent: " + message);
        } catch (SocketException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}