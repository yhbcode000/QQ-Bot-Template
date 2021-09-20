import java.util.ArrayList;

/**
 * 图灵API信息交换Model类。
 */
class TuringMessageModel {
    /**
     * 图灵API上传类。
     */
    public static class TuringUp {
        int reqType;
        Perception perception = new Perception();
        UserInfo userInfo = new UserInfo();

        static class Perception {
            InputText inputText = new InputText();

            static class InputText {
                String text;
            }
        }

        static class UserInfo {
            String apiKey;
            String userId;
        }
    }

    /**
     * 图灵API接受类。
     */
    public static class TuringDown {
        // there are lots of different return value
        transient Object emotion;
        transient Object intent;

        ArrayList<Result> results = new ArrayList<Result>();

        static class Result {
            int groupType;
            String resultType;
            Value values = new Value();

            class Value {
                String text;
            }
        }

    }
}
