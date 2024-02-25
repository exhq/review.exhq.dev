package dev.exhq;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.javalin.Javalin;
import io.javalin.json.JsonMapper;
import io.javalin.plugin.bundled.CorsPluginConfig;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.Instant;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws SQLException {
        String webhook = System.getenv("guh");
        if (webhook == null){
            System.out.println("\u001B[41m"+"specify a fucking webhook faggot" + "\u001B[0m");
            System.exit(0);
        }
        var dickBase = DriverManager.getConnection("jdbc:sqlite:balls.db");
        dickBase.prepareStatement("CREATE TABLE IF NOT EXISTS reviews (" +
                "reviewid INTEGER PRIMARY KEY," +
                "discordid TEXT NOT NULL," +
                "review TEXT NOT NULL," +
                "timestamp TEXT NOT NULL," +
                "accepted BOOL DEFAULT FALSE)").execute();
        var app = Javalin.create(javalinConfig -> {
            javalinConfig.bundledPlugins.enableCors(corsPluginConfig -> {
                corsPluginConfig.addRule(CorsPluginConfig.CorsRule::anyHost);
            });
            javalinConfig.jsonMapper(GsonMapper.createGsonMapper());
                })
                .post("/sendreview", ctx -> {
                    var stemented = dickBase.prepareStatement("INSERT INTO reviews(discordid,review,timestamp) VALUES (?,?,?);");
                    var token = ctx.header("Auth");
                    if (token == null){
                        ctx.result("log in faggot");
                        return;
                    }
                    var discordInfoMaybe = DiscordAuth.ValidateAuthorization(token);
                    if (discordInfoMaybe == null){
                        ctx.status(401);
                        return;
                    }
                    var discordInfo = discordInfoMaybe.user();
                    var discordId = discordInfo.id();
                    var review = ctx.queryParam("review");
                    if (review == null){
                        ctx.status(400);
                        return;
                    }
                    stemented.setString(1, discordId);
                    stemented.setString(2, review);
                    stemented.setString(3, String.valueOf(Instant.now().toEpochMilli()));
                    stemented.execute();


                    var stemented2ElectricBoogaloo = dickBase.prepareStatement("SELECT reviewid FROM reviews WHERE review = ? ORDER BY reviewid DESC LIMIT 1");
                    stemented2ElectricBoogaloo.setString(1, review);
                    var reviewIDThatWasJustSentButTheSQLBullshit = stemented2ElectricBoogaloo.executeQuery();
                    var theActualReviewID = reviewIDThatWasJustSentButTheSQLBullshit.getString(1);



                    String message = "new review from " + discordInfo.username() + "    " +
                                     "review: "+ review  + "    " +
                                     "reviewiD: "+ theActualReviewID;
                    String theshit = "{\"content\": \"" + message + "\"}";
                    String response = "fuck you kas";

                    URL obj = new URL(webhook);
                    HttpURLConnection conn = (HttpURLConnection) obj.openConnection();

                    conn.setRequestMethod("POST");
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setDoOutput(true);

                    byte[] bytes = theshit.getBytes(StandardCharsets.UTF_8);
                    conn.getOutputStream().write(bytes);
                    int responseCode = conn.getResponseCode();
                    if (responseCode != 200){
                        ctx.result(response + " kys");
                    }
                    ctx.result(response);
                    conn.disconnect();
                }).post("/acceptreview", ctx -> {
                    var token = ctx.header("Auth");
                    var acceptedreview = ctx.queryParam("review");
                    if (acceptedreview == null){
                        ctx.result("what fucking review do you wanna accept holy shit you're so fucking stupid fuck you and go kill yourself");
                        return;
                    }
                    try {
                        Integer.parseInt(acceptedreview);
                    }
                    catch(NumberFormatException nfe) {
                        ctx.result("no");
                        return;
                    }
                    if (!DiscordAuth.ValidateAuthorization(token).user().id().equals("712639419785412668")){
                        ctx.result("no");
                        return;
                    }
                    var statemented = dickBase.prepareStatement("UPDATE reviews SET accepted = TRUE WHERE reviewid = ?");
                    statemented.setInt(1, Integer.parseInt(acceptedreview));
                    statemented.execute();
                    ctx.result("review accepted, i think");
                }).get("/getreviews", ctx -> {
                    var statemented = dickBase.prepareStatement("SELECT reviewid, discordid, review, timestamp FROM reviews WHERE accepted = TRUE");
                    var info = statemented.executeQuery();
                    var list = new ArrayList<PublicReview>();
                    while (info.next()){
                        list.add(new PublicReview(info.getInt(1), info.getString(2), info.getString(3), info.getString(4)));
                    }
                    ctx.json(list);
                })
                .start(7070);
    }
}