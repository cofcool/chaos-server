package net.cofcool.chaos.server.cloud.stream.kafka;

/**
 * @author CofCool
 * @date 2018/9/21
 */
public class Log {

    private String type;

    private String content;

    public Log(String type, String content) {
        this.type = type;
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
