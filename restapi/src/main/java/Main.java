package main.java;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static spark.Spark.*;

public class Main {
    private static HashMap<Integer, String> greetings = new HashMap<>();
    private static Integer count = 1;

    public static void main(String[] args) {
        greetings.put(1, "Test Greeting");
        Database db = new Database();

        // Greeting endpoint
        path("/greeting", () -> {
            // All greetings in HashMap
            get("", (req, res) -> {
                res.type("application/json");
                res.status(200);
                return allGreetings();
            });

            // The message with :id
            get("/:id", (req, res) -> {
                res.type("application/json");
                res.status(200);
                return greeting(Integer.parseInt(req.params(":id")), res);
            });

            put("/:id", (req, res) -> {
                res.type("application/json");
                res.status(200);
                return putGreeting(Integer.parseInt(req.params(":id")), req.queryParams("message"), res);
            });

            // Create new greeting with message sent by POST
            post("", (req, res) -> {
                res.type("application/json");
                res.status(200);
                return newGreeting(req.queryParams("message"), res);
            });

            // Create a greeting with the and ID passed for testing purposes
            post("/:id", (req, res) -> {
                res.type("application/json");
                res.status(200);
                return newGreetingWithID(Integer.parseInt(req.params(":id")));
            });

            // Delete with id sent by param
            delete("/:id", (req, res) -> {
                res.type("application/json");
                res.status(200);
                return deleteGreeting(Integer.valueOf(req.params(":id")), res);
            });
        });

        // User endpoint
        path("/user", () -> {
            // Create new greeting with message sent by POST
            post("", (req, res) -> {
                // Verify the contentType of the request
                if (!req.contentType().equals("application/json")) {
                    return error(400, "Only accepts content of application/json type", res);
                }
                try {
                    JSONArray array = new JSONArray(req.body());

                    // Verify if the JSONArray is empty
                    if (array.length() == 0) {
                        return error(400, "User array is empty", res);
                    }

                    // Add Users from JSONArray to mysql DB
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject cur = array.getJSONObject(i);
                        String name = cur.get("name").toString();
                        String birthDate = cur.get("birthDate").toString();
                        String city = cur.get("city").toString();

                        if (testUserParams(name, birthDate, city) == false) {
                            return error(400, "Params don't follow the rules", res);
                        }

                        User user = new User(name, formatDate(birthDate), city);
                        db.insertUser(user, 0);
                    }
                    return new JSONObject().put("message", "Were added " + array.length() + " users.");
                }
                catch (Exception e) {
                    return error(400, "Wrong JSON format", res);
                }
            });

            post("/:id", (req,res) -> {
                User user = new User("Teste", "1999-01-01", "Marrocos");
                db.insertUser(user, Integer.parseInt(req.params(":id")));
                return new JSONObject().put("message", "Fake user inserted with success");
            });

            get("", (req, res) -> {
                res.type("application/json");
                res.status(200);
                return db.getUsers();
            });

            get("/:id", (req,res) -> {
                JSONArray result = db.getUser(Integer.parseInt(req.params(":id")));
                res.type("application/json");
                if(result == null) {
                    return error(400, "ID doesn't exist on DB.", res);
                }
                res.status(200);
                return result;
            });

            // Delete with id sent by param
            delete("/:id", (req, res) -> {
                res.type("application/json");
                res.status(200);
                if(!db.deleteUser(Integer.parseInt(req.params(":id")))) {
                    return error(400, "ID doesn't exist on DB.", res);
                }
                return new JSONObject().put("message", "User with id " + req.params(":id") + " deleted with success from DB.");
            });
        });


        get("/*", (req, res) -> {
            return error(400, "Invalid endpoint", res);
        });
    }

    private static JSONArray allGreetings() {
        JSONArray jsonArray = new JSONArray();
        for (Map.Entry<Integer, String> cur : greetings.entrySet()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put(cur.getKey().toString(), cur.getValue());
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }

    private static JSONObject greeting(Integer id, spark.Response res) {
        JSONObject jsonObject = new JSONObject();
        String message = greetings.get(id);

        if (message != null) {
            jsonObject.put(id.toString(), message);
        } else {
            res.status(400);
            jsonObject.put("message", "No entry with the id " + id);

        }
        return jsonObject;
    }

    private static JSONObject putGreeting(Integer id, String message, spark.Response res) {
        JSONObject jsonObject = new JSONObject();
        String cur = greetings.get(id);

        if (!message.matches("^[-a-zA-Z._ ]+")) {
            res.status(400);
            jsonObject.put("message", "Message contains invalid chars");
        } else {
            if (cur != null) {
                greetings.put(id, message);
                jsonObject.put(id.toString(), message);
            } else {
                res.status(400);
                jsonObject.put("message", "No entry with the id " + id);
            }
        }

        return jsonObject;
    }

    private static JSONObject newGreeting(String message, spark.Response res) {
        JSONObject jsonObject = new JSONObject();
        if (!message.matches("^[-a-zA-Z._ ]+")) {
            res.status(400);
            jsonObject.put("message", "Message contains invalid chars");
        } else {
            greetings.put(++count, message);
            jsonObject.put("message", message);
        }
        return jsonObject;
    }

    private static JSONObject newGreetingWithID(Integer id) {
        JSONObject jsonObject = new JSONObject();
        greetings.put(id, "Greeting message for Robot Testing");
        jsonObject.put("message", "Greeting message for Robot Testing");
        return jsonObject;
    }

    private static JSONObject deleteGreeting(Integer id, spark.Response res) {
        JSONObject jsonObject = new JSONObject();
        String mensagem = greetings.get(id);
        if (mensagem != null) {
            jsonObject.put("message", "Deleted the message with id " + id);
            greetings.remove(id);
        } else {
            res.status(400);
            jsonObject.put("message", "No entry with the id " + id);
        }
        return jsonObject;
    }

    private static boolean testUserParams(String name, String date, String city) {
        if ((date.charAt(4) == '/' || date.charAt(7) == '/')) {
            return false;
        }

        if ((date.charAt(2) == '/' || date.charAt(5) == '/')) {
            return false;
        }
        return true;
    }

    private static JSONObject error(Integer statusCode, String errorMessage, spark.Response res) {
        res.type("application/json");
        res.status(statusCode);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("message", errorMessage);
        return jsonObject;
    }

    private static String formatDate(String birthDate) throws ParseException {
        Date date;
        SimpleDateFormat lastFormatter = new SimpleDateFormat("yyyy-MM-dd");
        if (birthDate.charAt(2) == '-') {
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            date = formatter.parse(birthDate);
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            date = formatter.parse(birthDate);
        }
        return lastFormatter.format(date);
    }
}
