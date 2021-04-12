import com.google.gson.GsonBuilder;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.event.GlobalEventChannel;
import net.mamoe.mirai.event.Listener;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.utils.BotConfiguration;

import java.util.ArrayList;
import java.util.Properties;
import java.io.InputStream;

import com.google.gson.Gson;

public class QQbot {

    // get client prop form resource folder
    private static Properties loadProperties(String propsFilename) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        Properties props = new Properties();

        try {
            InputStream propsStream = loader.getResourceAsStream(propsFilename);
            props.load(propsStream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return props;
    }

    // internal field only used for transmission purposes
    static class TuringUp{
        int reqType;
        Perception perception = new Perception();
        UserInfo userInfo = new UserInfo();

        class Perception{
            InputText inputText = new InputText();
            class InputText {
                String text;
            }
        }

        class UserInfo{
            String apiKey;
            String userId;
        }
    }

    static class TuringDown{
        // there are lots of different return value
        transient Object emotion;
        transient Object intent;

        ArrayList<Result> results = new ArrayList<Result>();

        class Result{
            int groupType;
            String resultType;
            Value values = new Value();

            class Value{
                String text;
            }
        }

    }

    public static void main(String[] args) {
        // config messaging
        String clientPropsFilename = "client.cfg";
        Properties clientProps = QQbot.loadProperties(clientPropsFilename);
        String endpoint = clientProps.getProperty("endpoint");
        String turingAPIKey = clientProps.getProperty("turingAPIKey");
        String userId = clientProps.getProperty("userId");
        int QQid = Integer.parseInt(clientProps.getProperty("QQid"));
        String QQpwd = clientProps.getProperty("QQpwd");

        // bot initialisation
        Bot bot = BotFactory.INSTANCE.newBot(QQid, QQpwd, new BotConfiguration() {{
            fileBasedDeviceInfo();
            setProtocol(MiraiProtocol.ANDROID_PAD);
        }});
        bot.login();
        bot.getGroup(942848525).sendMessage("Start!");

        // start listening action
        Listener listener = GlobalEventChannel.INSTANCE.subscribeAlways(GroupMessageEvent.class, event -> {
            if (event.getMessage().serializeToMiraiCode().startsWith("#")){
                // construct upload data
                TuringUp upMessaging = new QQbot.TuringUp();
                upMessaging.reqType = 0;
                upMessaging.perception.inputText.text = event.getMessage().serializeToMiraiCode().substring(1);
                upMessaging.userInfo.apiKey = turingAPIKey;
                upMessaging.userInfo.userId = userId;

                // convert to json
                String data = new Gson().toJson(upMessaging);
                System.out.println(data);

                String response = "";
                TuringDown downMessaging = new QQbot.TuringDown();

                try {
                    // perform request
                    response = ClientIO.doPOSTRequest(endpoint, data);
                    Gson gs = new GsonBuilder().disableHtmlEscaping().create();
                    downMessaging = gs.fromJson(response,QQbot.TuringDown.class);
                    System.out.println(downMessaging.results.get(0).values.text);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // reply
                String reply = downMessaging.results.get(0).values.text;
                event.getSubject().sendMessage(reply);
            } else {
//                if (event.getSubject().equals(bot.getGroup(423779443))){
//                    event.getSubject().sendMessage(event.getMessage());
//                }
//                if (event.getSubject().equals(bot.getGroup(370259015))){
//                    event.getSubject().sendMessage(event.getMessage());
//                }
            }
        });
        //listener.complete();
    }
}

