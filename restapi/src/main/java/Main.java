package main.java;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import spark.Spark;

import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Main {
    private static HashMap<Integer,String> greetings = new HashMap<>();
    private static Integer count = 1;
    public static void main(String[] args) {
        greetings.put(1,"Test Greeting");

        // All greetings in HashMap
        get("/greeting", (req,res) -> {
             res.type("application/json");
             res.status(200);
             return allGreetings();
        });

        // The message with :id
        get("/greeting/:id", (req, res) -> {
            res.type("application/json");
            res.status(200);
            return greeting(Integer.parseInt(req.params(":id")), res);
        });

        put("/greeting/:id", (req,res) -> {
           res.type("application/json");
           res.status(200);
           return putGreeting(Integer.parseInt(req.params(":id")), req.queryParams("message"), res);
        });

        // Create new greeting with message sent by POST
        post("/greeting", (req, res) -> {
            res.type("application/json");
            res.status(200);
            return newGreeting(req.queryParams("message"), res);
        });

        // Create a greeting with the and ID passed for testing purposes
        post("/greeting/:id", (req, res) -> {
            res.type("application/json");
            res.status(200);
            return newGreetingWithID(Integer.parseInt(req.params(":id")));
        });

        // Delete with id sent by param
        delete("/greeting/:id", (req, res) -> {
            res.type("application/json");
            res.status(200);
            return deleteGreeting(Integer.valueOf(req.params(":id")), res);
        });

        get("/*", (req,res) -> {
            res.type("application/json");
            res.status(400);
            return invalidRoute();
        });
    }

    private static String allGreetings() {
        JSONArray jsonArray = new JSONArray();
        for (Map.Entry<Integer,String> cur : greetings.entrySet()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(cur.getKey(), cur.getValue());
            jsonArray.add(jsonObject);
        }
        return jsonArray.toJSONString();
    }

    private static String greeting(Integer id, spark.Response res) {
        JSONObject jsonObject = new JSONObject();
        String message = greetings.get(id);

        if(message != null) {
            jsonObject.put(id, message);
        }
        else {
            res.status(400);
            jsonObject.put("message", "No entry with the id " + id);

        }
        return jsonObject.toJSONString();
    }

    private static String putGreeting(Integer id, String message, spark.Response res) {
        JSONObject jsonObject = new JSONObject();
        String cur = greetings.get(id);

        if(!message.matches("^[-a-zA-Z._ ]+")) {
            res.status(400);
            jsonObject.put("message", "Message contains invalid chars");
        }
        else {
            if(cur != null) {
                greetings.put(id, message);
                jsonObject.put(id, message);
            }
            else {
                res.status(400);
                jsonObject.put("message", "No entry with the id " + id);
            }
        }

        return jsonObject.toJSONString();
    }

    private static String newGreeting(String message,spark.Response res) {
        JSONObject jsonObject = new JSONObject();
        if(!message.matches("^[-a-zA-Z._ ]+")) {
            res.status(400);
            jsonObject.put("message", "Message contains invalid chars");
        }
        else {
            greetings.put(++count, message);
            jsonObject.put("message", message);
        }
        return jsonObject.toJSONString();
    }

    private static String newGreetingWithID(Integer id) {
        JSONObject jsonObject = new JSONObject();
        greetings.put(id, "Greeting message for Robot Testing");
        jsonObject.put("message", "Greeting message for Robot Testing");
        return jsonObject.toJSONString();
    }

    private static String deleteGreeting(Integer id, spark.Response res) {
        JSONObject jsonObject = new JSONObject();
        String mensagem = greetings.get(id);
        if(mensagem != null) {
            jsonObject.put("message", "Deleted the message with id " + id);
            greetings.remove(id);
        }
        else {
            res.status(400);
            jsonObject.put("message", "No entry with the id " + id);
        }
        return jsonObject.toJSONString();
    }

    private static String invalidRoute() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", "Invalid endpoint");
        return jsonObject.toJSONString();
    }
}
