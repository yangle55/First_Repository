package connection;

import com.rabbitmq.client.*;

import java.io.IOException;


public class Demo {
    public static void main(String[] args) {
        Publisher(); // 推送消息

        Consumer(); // 消费消息
    }

    /**
     * 推送消息
     */
    public static void Publisher() {
        // 获取链接
        Connection conn = ConnectionDemo.GetRabbitConnection();
        if (conn != null) {
            try {
             // 创建通道
                Channel channel = conn.createChannel();
                // 声明队列【参数说明：参数一：队列名称，参数二：是否持久化；参数三：是否独占模式；参数四：消费者断开连接时是否删除队列；参数五：消息其他参数】
                channel.queueDeclare("queue_name", false, false, false, null);
                String content = "今天真是个好日子！";
                // 发送内容【参数说明：参数一：交换机名称；参数二：队列名称，参数三：消息的其他属性-routing headers，此属性为MessageProperties.PERSISTENT_TEXT_PLAIN用于设置纯文本消息存储到硬盘；参数四：消息主体】
                channel.basicPublish("", "queue_name", null, content.getBytes("UTF-8"));
                System.out.println("生产消息：" + content);
                // 关闭连接
                channel.close();
                conn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 消费消息
     */
    public static void Consumer() {
        // 创建一个连接
        Connection conn = ConnectionDemo.GetRabbitConnection();
        if (conn != null) {
            try {
                // 创建通道
                final Channel channel = conn.createChannel();
                // 声明队列【参数说明：参数一：队列名称，参数二：是否持久化；参数三：是否独占模式；参数四：消费者断开连接时是否删除队列；参数五：消息其他参数】
                channel.queueDeclare("queue_name", false, false, false, null);

                // 创建订阅器，并接受消息
                channel.basicConsume("queue_name", false, "", new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
                                               byte[] body) throws IOException {
                        String routingKey = envelope.getRoutingKey(); // 队列名称
                        String contentType = properties.getContentType(); // 内容类型
                        String content = new String(body, "utf-8"); // 消息正文
                        System.out.println("消费消息：" + content);
                        channel.basicAck(envelope.getDeliveryTag(), false); // 手动确认消息【参数说明：参数一：该消息的index；参数二：是否批量应答，true批量确认小于index的消息】
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     *
     * @param a
     * @param b
     */
    public void test( int a ,int b ){

    }

}
