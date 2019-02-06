package com.example.luke_.pubquiz.client;


public class ClientProtocol {
    private static final int WAITING = 0;
    private static final int RECEIVEQUESTION = 1;

    public String processInput(String input) {
        String output = "";
        String message;
        int state = Integer.parseInt(input.split(",")[0]);
        message = input.split(",")[1];

        switch (state) {
            case WAITING:
                break;
            case RECEIVEQUESTION:

                System.out.println("Received message from server!");
                output = message;
                break;
        }
        return output;
    }


}
