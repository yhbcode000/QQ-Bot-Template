import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.mamoe.mirai.Bot;
import net.mamoe.mirai.BotFactory;
import net.mamoe.mirai.event.events.FriendMessageEvent;
import net.mamoe.mirai.event.events.GroupMessageEvent;
import net.mamoe.mirai.message.data.MessageChainBuilder;
import net.mamoe.mirai.message.data.QuoteReply;
import net.mamoe.mirai.utils.BotConfiguration;

import java.util.Objects;
import java.util.Properties;

/**
 * QQ机器人Control类。
 */
public class QQbotControl {
    Properties clientProps;
    long MQQid;
    long QQid;
    String QQpwd;
    Bot bot;

    public QQbotControl() {
        this.readConfig();
        this.botInitialisation();
        this.botLogin();
    }

    /**
     * 机器人启动。
     */
    public void start() {
        this.bot.getEventChannel().subscribeAlways(GroupMessageEvent.class, this::groupEvents);
        this.bot.getEventChannel().subscribeAlways(FriendMessageEvent.class, this::managerEvent);
    }

    private void readConfig() {
        String clientPropsFilename = "client.cfg";
        this.clientProps = ConfigUtils.loadProperties(clientPropsFilename);
    }

    private void botInitialisation() {
        // get managers id
        MQQid = Long.parseLong(clientProps.getProperty("MQQid"));

        // bot initialisation
        QQid = Long.parseLong(clientProps.getProperty("QQid"));
        QQpwd = clientProps.getProperty("QQpwd");

        bot = BotFactory.INSTANCE.newBot(QQid, QQpwd, new BotConfiguration() {{
            fileBasedDeviceInfo();
            setProtocol(MiraiProtocol.ANDROID_PAD);
        }});
    }

    private void botLogin() {
        // try login
        try {
            bot.login();
            Objects.requireNonNull(bot.getFriend(MQQid)).sendMessage("莉莉白来啦！");
        } catch (Exception e) {
            System.out.println("Error occurs while bot login.");
        }
    }

    // TODO 添加对群功能位置
    private void groupEvents(GroupMessageEvent event) {
        if (event.getMessage().serializeToMiraiCode().startsWith("-r ")) {
            // 复读机功能
            event.getSubject().sendMessage(new MessageChainBuilder()
                    .append(new QuoteReply(event.getMessage()))
                    .append("你刚才说：'")
                    .append(event.getMessage().contentToString().substring(3))
                    .append("'")
                    .build()
            );
        } else if (event.getMessage().serializeToMiraiCode().startsWith("-h")){
            // 帮助文档。
            event.getSubject().sendMessage("输入：\n    -h\t打印帮助文档。\n    -r\t复读机。\n    -q\t回答问题。");
        }
    }

    // TODO 添加对个人功能位置
    private void managerEvent(FriendMessageEvent event) {
        if (event.getSender().getId() == MQQid) {
            // 捧哏
            event.getSubject().sendMessage(new MessageChainBuilder()
                    .append(new QuoteReply(event.getMessage()))
                    .append("确实。")
                    .build()
            );
        }
    }
}
